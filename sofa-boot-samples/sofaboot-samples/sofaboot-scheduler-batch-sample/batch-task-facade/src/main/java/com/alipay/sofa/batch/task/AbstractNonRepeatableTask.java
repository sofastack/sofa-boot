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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;

/**
 * Abstract class for task that cannot be repeated for the same set of job parameters
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 00:41
 */
public abstract class AbstractNonRepeatableTask implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNonRepeatableTask.class);

    /**
     * <p>
     * Execute the implementation of a non-repeatable task
     * </p>
     *
     * @param jobContext
     * @return
     * @throws ExecutionException
     */
    public abstract ExitStatus executeNonRepeatable(JobContext jobContext) throws ExecutionException;

    /**
     * Checks if a task service implementation has already executed a particular set of job parameters before.
     *
     * @param jobContext
     * @return true if executed before
     */
    public abstract Boolean checkExecutedBefore(JobContext jobContext);


    @Override
    public ExitStatus execute(JobContext jobContext) throws ExecutionException {
        if (!checkExecutedBefore(jobContext)) {
            return executeNonRepeatable(jobContext);
        } else {
            LOGGER.warn("Job {} requestId:{} is not repeatable.", jobContext.getJobName(), jobContext.getJobRequestId());
            ExitStatus exitStatusNoop = ExitStatus.NOOP;
            LOGGER.warn("[task:{}] exit status:{}, for job {}:{}",
                    this.getClass().getName(), exitStatusNoop,
                    jobContext.getJobName(), jobContext.getJobRequestId());
            return exitStatusNoop;
        }
    }

}
