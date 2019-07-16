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
package com.alipay.sofa.healthcheck.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
import com.alipay.sofa.healthcheck.test.bean.MemoryHealthChecker;
import com.alipay.sofa.healthcheck.test.bean.MiddlewareHealthCheckCallback;

/**
 * @author liangen
 * @version 2.3.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.application.name=ReadinessCheckListenerTest")
public class ReadinessCheckListenerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Configuration
    static class HealthCheckConfiguration {
        @Bean
        public MemoryHealthChecker memoryHealthChecker(@Value("${memory-health-checker.count:0}") int count,
                                                       @Value("${memory-health-checker.strict:false}") boolean strict,
                                                       @Value("${memory-health-checker.retry-count:0}") int retryCount) {
            return new MemoryHealthChecker(count, strict, retryCount);
        }

        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health:true}") boolean health) {
            return new DiskHealthIndicator(health);
        }

        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${middleware-health-check-callback.health:true}") boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public ReadinessCheckListener readinessCheckListener() {
            return new ReadinessCheckListener();
        }

        @Bean
        public HealthCheckerProcessor healthCheckerProcessor() {
            return new HealthCheckerProcessor();
        }

        @Bean
        public HealthIndicatorProcessor healthIndicatorProcessor() {
            return new HealthIndicatorProcessor();
        }
    }

    @Test
    public void testReadinessCheck() throws BeansException {
        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.assertNotNull(readinessCheckListener);
        Assert.assertFalse(readinessCheckListener.skipAllCheck());
        Assert.assertFalse(readinessCheckListener.skipComponent());
        Assert.assertFalse(readinessCheckListener.skipIndicator());
        Assert.assertTrue(readinessCheckListener.getHealthCheckerStatus());
        Assert.assertTrue(readinessCheckListener.getHealthIndicatorStatus());
        Assert.assertTrue(readinessCheckListener.getHealthCallbackStatus());
        Assert.assertTrue(readinessCheckListener.getHealthCheckerDetails().size() == 1);

        Health health = readinessCheckListener.getHealthCheckerDetails().get("memoryHealthChecker");
        Assert.assertTrue("memory is bad".equals(health.getDetails().get("memory")));
        health = readinessCheckListener.getHealthCallbackDetails().get(
            "middlewareHealthCheckCallback");
        Assert.assertTrue("server is ok".equals(health.getDetails().get("server")));
        health = readinessCheckListener.getHealthIndicatorDetails().get("disk");
        Assert.assertTrue("hard disk is ok".equals(health.getDetails().get("disk")));
    }
}