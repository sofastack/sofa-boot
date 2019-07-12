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

import org.springframework.batch.core.ExitStatus;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-17
 * Time: 23:33
 */
public interface TaskService {
    /**
     * Execute the task implementation based on the job parameters in the job context
     *
     * @param jobContext
     * @return
     * @throws ExecutionException
     */
    ExitStatus execute(JobContext jobContext) throws ExecutionException;

    /**
     * <p>
     * To control the life cycle of the job.
     * This will let the task service implementation indicate if job should be terminated base on the exit status
     * </p>
     *
     * @param exitStatus
     * @return true if the job should be terminated based on the exit status
     */
    Boolean checkTerminateJob(ExitStatus exitStatus);
}
