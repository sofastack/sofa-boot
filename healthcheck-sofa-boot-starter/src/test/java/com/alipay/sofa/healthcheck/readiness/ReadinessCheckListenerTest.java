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
package com.alipay.sofa.healthcheck.readiness;

import com.alipay.sofa.healthcheck.bean.MiddlewareHealthCheckCallback;
import com.alipay.sofa.healthcheck.bean.DiskHealthIndicator;
import com.alipay.sofa.healthcheck.bean.MemoryHealthChecker;
import com.alipay.sofa.healthcheck.configuration.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.base.BaseHealthCheckTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author liangen
 * @version 2.3.0
 */
public class ReadinessCheckListenerTest extends BaseHealthCheckTest {

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
    }

    @Test
    public void testReadinessCheck() throws BeansException {
        initApplicationContext(new HashMap<String, Object>(), HealthCheckConfiguration.class,
            SofaBootHealthCheckAutoConfiguration.class);
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