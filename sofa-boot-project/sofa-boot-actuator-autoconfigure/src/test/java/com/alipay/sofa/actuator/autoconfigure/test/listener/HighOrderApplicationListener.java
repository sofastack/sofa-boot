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
package com.alipay.sofa.actuator.autoconfigure.test.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ruoshan
 * @since 2.6.0
 */
public class HighOrderApplicationListener implements ApplicationListener<ContextRefreshedEvent>,
                                         PriorityOrdered {

    @Autowired
    private TestRestTemplate       testRestTemplate;

    @Value("${management.server.port}")
    private String                 managementPort;

    @Autowired
    private ApplicationContext     applicationContext;

    private ResponseEntity<String> readinessCheckResponse;

    private ResponseEntity<String> livenessCheckResponse;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // only listen to root application
        if (!applicationContext.equals(contextRefreshedEvent.getApplicationContext())) {
            return;
        }

        System.out.println(contextRefreshedEvent.getApplicationContext().getEnvironment()
            .getProperty("management.server.port"));

        readinessCheckResponse = testRestTemplate.getForEntity("http://localhost:" + managementPort
                                                               + "/actuator/readiness",
            String.class);

        livenessCheckResponse = testRestTemplate.getForEntity("http://localhost:" + managementPort
                                                              + "/actuator/health", String.class);

    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE + 1;
    }

    public ResponseEntity<String> getReadinessCheckResponse() {
        return readinessCheckResponse;
    }

    public ResponseEntity<String> getLivenessCheckResponse() {
        return livenessCheckResponse;
    }
}