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
 * Abstract class for task that can be repeated for the same set of job parameters
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 00:41
 */
public abstract class AbstractRepeatableTask implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepeatableTask.class);

    /**
     * <p>
     * Execute the required processing before a task repeat execution on a set of job parameters that had been executed before.
     * Uniqueness can be checked on
     * </p>
     *
     * @param jobContext
     * @return
     * @throws ExecutionException
     */
    public abstract ExitStatus preRepeat(JobContext jobContext) throws ExecutionException;

    /**
     * <p>
     * Implementation of the repeatable execution
     * </p>
     *
     * @param jobContext
     * @return
     * @throws ExecutionException
     */
    public abstract ExitStatus executeRepeatable(JobContext jobContext) throws ExecutionException;


    @Override
    public ExitStatus execute(JobContext jobContext) throws ExecutionException {
        ExitStatus exitStatus = preRepeat(jobContext);
        if (ExitStatus.COMPLETED != exitStatus) {
            LOGGER.warn("preRepeat ExitStatus is {} != COMPLETED", exitStatus);
            return exitStatus;
        }
        return executeRepeatable(jobContext);
    }

}
