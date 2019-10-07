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

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 02:34
 */
public class SimpleTaskAImpl extends AbstractRepeatableTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskAImpl.class);

    @Override
    public Boolean checkTerminateJob(ExitStatus exitStatus) {
        // This task implementation states that job should not terminate regardless of it's exit status
        return false;
    }

    @Override
    public ExitStatus preRepeat(JobContext jobContext) throws ExecutionException {
        LOGGER.debug("doing preRepeat for {}", jobContext.toString());
        jobContext.putData("taskA.preRepeat", UUID.randomUUID().toString());
        return ExitStatus.COMPLETED;
    }

    @Override
    public ExitStatus executeRepeatable(JobContext jobContext) throws ExecutionException {
        LOGGER.debug("doing executeRepeatable for {}", jobContext.toString());
        jobContext.putData("taskA.executeRepeatable", UUID.randomUUID().toString());
        return ExitStatus.COMPLETED;
    }
}
