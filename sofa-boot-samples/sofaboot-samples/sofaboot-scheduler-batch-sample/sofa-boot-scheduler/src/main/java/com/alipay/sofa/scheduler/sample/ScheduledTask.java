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
package com.alipay.sofa.scheduler.sample;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alipay.sofa.batch.job.JobContext;
import com.alipay.sofa.batch.job.JobContextAssistant;
import com.alipay.sofa.batch.job.JobService;

/**
 * Created by IntelliJ IDEA.
 * Sample scheduler task that triggers every minute by default<br>
 * Schedule can be updated in application.properties accordingly
 *
 * @author tzeyong
 * Date: 2019-04-17
 * Time: 18:50
 */
@Component
public class ScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired()
    @Qualifier("sampleJobService")
    private JobService jobService;

    @Scheduled(cron = "${com.alipay.sofa.scheduler.sample.ScheduledTask.cron:0 * * * * *}")
    public void executeTask() {
        Date now = new Date();
        JobContext jobContext = prepareJobContext(now);
        LOGGER.info("JobContext Start : {}", jobContext.toString());
        jobContext = jobService.launchJob(jobContext);
        LOGGER.info("JobContext End = {}", jobContext.toString());
    }

    private JobContext prepareJobContext(Date now) {
        JobContext jobContext = new JobContext();
        jobContext.setJobRequestId(JobContextAssistant.generateJobRequestId());
        jobContext.setJobName("sample job name");
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("time-now", now);
        jobContext.setJobParameters(jobParametersBuilder.toJobParameters());
        return jobContext;
    }
}
