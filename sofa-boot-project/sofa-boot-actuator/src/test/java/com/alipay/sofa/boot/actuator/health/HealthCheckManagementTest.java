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
package com.alipay.sofa.boot.actuator.health;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.actuator.autoconfigure.test.listener.HighOrderApplicationListener;
import com.alipay.sofa.boot.constant.SofaBootConstants;

/**
 * Attention:
 * 1. Before Spring Boot 2.3.x (exclude), Tomcat started after SpringContextRefreshed event
 * 2. After Spring Boot 2.3.x (include), tomcat started before SpringContextRefreshed event
 *
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.application.name=HealthCheckManagementTest",
                                  "management.server.port=8888",
                                  "management.endpoint.health.show-details=ALWAYS" })
public class HealthCheckManagementTest {

    @Autowired
    private HighOrderApplicationListener highOrderApplicationListener;

    @Test
    public void testHealthCheckNotReadyReadiness() {
        ResponseEntity<String> responseEntity = highOrderApplicationListener
            .getReadinessCheckResponse();
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getBody().contains(
            SofaBootConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_MSG));
    }

    @Test
    public void testHealthCheckNotReadyLiveness() {
        ResponseEntity<String> responseEntity = highOrderApplicationListener
            .getLivenessCheckResponse();
        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getBody().contains(
            SofaBootConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_MSG));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class HealthCheckManagementTestConfiguration {
        @Bean
        public HighOrderApplicationListener highOrderApplicationListener() {
            return new HighOrderApplicationListener();
        }
    }
}
