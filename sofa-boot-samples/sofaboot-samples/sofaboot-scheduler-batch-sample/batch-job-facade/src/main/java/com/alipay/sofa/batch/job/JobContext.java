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
package com.alipay.sofa.batch.job;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-17
 * Time: 19:39
 */
@Getter
@Setter
public class JobContext {
    /**
     * name of the job
     */
    private String jobName;

    /**
     * A field to uniquely identify a job request.
     * Task service implementation shall use this to identify if a particular job request has been executed before.
     */
    private String jobRequestId;

    /**
     * Job parameters required by task service for execution of the job
     */
    private JobParameters jobParameters;

    /**
     * Status of the batch job
     */
    private BatchStatus batchStatus = BatchStatus.STARTING;

    /**
     * Data of the job context that will be passed along the processing chain
     */
    private Map<String, Object> data = new ConcurrentHashMap<>();

    /**
     * Put data into this job context
     *
     * @param key
     * @param value
     */
    public void putData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get the data from context
     *
     * @param key
     * @return
     */
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{jobName:%s, jobRequestId:%s, batchStatus:%s}",
                jobName, jobRequestId, batchStatus));
        if (null != jobParameters) {
            sb.append(String.format("%njobParameters="));
            sb.append(jobParameters.toString());
        }
        sb.append(String.format("%ndata=%s", data.toString()));
        return sb.toString();
    }
}
