/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.batch.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;

/**
 * Helper class used when executing list of task.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 02:17
 */
public class TaskExecutionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutionHelper.class);

    /**
     * Helper method to check invocation of task service implementation
     *
     * @param taskService
     * @param jobContext
     * @return
     */
    public static Boolean checkInvokeTask(TaskService taskService, JobContext jobContext) {
        Boolean invokeSuccess = true;
        // prepare the list of exit status that can continue
        List<ExitStatus> okExitStatus = new ArrayList<>();
        okExitStatus.add(ExitStatus.COMPLETED);
        okExitStatus.add(ExitStatus.NOOP);

        try {
            ExitStatus exitStatus = taskService.execute(jobContext);
            // check if exit status is in the list of OK status to continue
            if (!okExitStatus.contains(exitStatus)) {
                // task exit status is not completed. check if task is a terminator.
                if (taskService.checkTerminateJob(exitStatus)) {
                    // task service implementation indicates that job should be terminated
                    LOGGER.warn("[{}] indicates to terminate job for exit status:{}", taskService.getClass().getName(), exitStatus);
                    // ABANDONED is used for steps that have finished processing, but were not successful
                    jobContext.setBatchStatus(BatchStatus.ABANDONED);
                    invokeSuccess = false;
                }
            }
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            invokeSuccess = false;
        }
        return invokeSuccess;
    }
}
