///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alipay.sofa.boot.actuator.health;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * @author liangen
// * @author qilong.zql
// * @version 2.3.0
// */
//@SpringBootApplication
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=health,info,readiness" })
//@TestPropertySource(properties = "spring.application.name=ReadinessHealthCheckHttpCodeTest")
//public class ReadinessHealthCheckHttpCodeTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void testReadinessCheckFailedHttpCode() {
//        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/readiness",
//            String.class);
//        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    public void testVersions404HttpCode() {
//        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/versions",
//            String.class);
//        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    static class DownHealthIndicatorConfiguration {
//        @Bean
//        public HealthIndicator downHealthIndicator() {
//            return new HealthIndicator() {
//                @Override
//                public Health health() {
//                    return Health.down().build();
//                }
//            };
//        }
//    }
//}
