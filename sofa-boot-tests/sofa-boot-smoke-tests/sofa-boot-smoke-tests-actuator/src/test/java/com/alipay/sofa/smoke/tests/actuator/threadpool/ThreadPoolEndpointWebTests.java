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
package com.alipay.sofa.smoke.tests.actuator.threadpool;

import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link com.alipay.sofa.boot.actuator.threadpool.ThreadPoolEndpoint} web response.
 *
 * @author huzijie
 * @version ThreadPoolEndpointWebTests.java, v 0.1 2024年03月22日 14:06 huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=threadpool" })
@Import(ThreadPoolEndpointWebTests.Config.class)
public class ThreadPoolEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void threadPoolActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/threadpool", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("""
                        {"threadPoolName":"demoThreadPool","threadPoolClassName":"com.alipay.sofa.common.thread.SofaThreadPoolExecutor","coreSize":20,"maxSize":20,"queueClassName":"java.util.concurrent.LinkedBlockingQueue","queueSize":0,"queueRemainingCapacity":10,"monitorPeriod":5000,"taskTimeout":30000}""");
    }

    @Configuration
    static class Config {

        @Bean
        public SofaThreadPoolExecutor sofaThreadPoolExecutor() {
            return new SofaThreadPoolExecutor(20, 20, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10), "demoThreadPool");
        }
    }
}
