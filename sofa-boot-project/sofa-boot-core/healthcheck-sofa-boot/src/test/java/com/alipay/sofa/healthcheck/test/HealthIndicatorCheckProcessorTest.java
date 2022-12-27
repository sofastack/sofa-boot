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

import com.alipay.sofa.healthcheck.*;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
import com.alipay.sofa.healthcheck.test.bean.TimeoutHealthIndicator;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthIndicatorCheckProcessorTest {

    private ApplicationContext applicationContext;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    static class HealthIndicatorConfiguration {
        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health}") boolean health) {
            return new DiskHealthIndicator(health);
        }

        @Bean
        public TimeoutHealthIndicator timeoutHealthIndicator(@Value("${timeout-health-indicator.health}") boolean health) {
            return new TimeoutHealthIndicator(health);
        }

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public ReadinessCheckListener readinessCheckListener(Environment environment,
                                                             HealthCheckerProcessor healthCheckerProcessor,
                                                             HealthIndicatorProcessor healthIndicatorProcessor,
                                                             AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                             SofaRuntimeConfigurationProperties sofaRuntimeConfigurationProperties,
                                                             HealthCheckProperties healthCheckProperties) {
            return new ReadinessCheckListener(environment, healthCheckerProcessor,
                healthIndicatorProcessor, afterReadinessCheckCallbackProcessor,
                sofaRuntimeConfigurationProperties, healthCheckProperties);
        }

        @Bean
        public HealthCheckerProcessor healthCheckerProcessor(HealthCheckProperties healthCheckProperties,
                                                             HealthCheckExecutor healthCheckExecutor) {
            return new HealthCheckerProcessor(healthCheckProperties, healthCheckExecutor);
        }

        @Bean
        public HealthIndicatorProcessor healthIndicatorProcessor(HealthCheckProperties properties,
                                                                 HealthCheckExecutor healthCheckExecutor) {
            return new HealthIndicatorProcessor(properties, healthCheckExecutor);
        }

        @Bean
        public HealthCheckExecutor healthCheckExecutor(HealthCheckProperties properties) {
            return new HealthCheckExecutor(properties);
        }
    }

    @Test
    public void testCheckIndicatorPassed() {
        initApplicationContext(true);
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertTrue(result);
        Assert.assertEquals(2, hashMap.size());
        Assert.assertNotNull(diskHealth);
        Assert.assertEquals(diskHealth.getStatus(), Status.UP);
        Assert.assertEquals("hard disk is ok", diskHealth.getDetails().get("disk"));
    }

    @Test
    public void testCheckIndicatorFailed() {
        initApplicationContext(false);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertFalse(result);
        Assert.assertEquals(2, hashMap.size());
        Assert.assertNotNull(diskHealth);
        Assert.assertEquals(Status.DOWN, diskHealth.getStatus());
        Assert.assertEquals("hard disk is bad", diskHealth.getDetails().get("disk"));

        Health timeoutHealth = hashMap.get("timeout");
        Assert.assertNotNull(timeoutHealth);
        Assert.assertEquals(Status.UNKNOWN, timeoutHealth.getStatus());
        Assert.assertEquals("java.util.concurrent.TimeoutException: null", timeoutHealth.getDetails().get("error"));
    }

    private void initApplicationContext(boolean health) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("disk-health-indicator.health", health);
        properties.put("timeout-health-indicator.health", true);
        if (!health) {
            properties.put("com.alipay.sofa.healthcheck.indicator.timeout.timeoutHealthIndicator", "1");
        }
        properties.put("com.alipay.sofa.healthcheck.skip.indicator", "true");
        properties.put("spring.application.name", "HealthIndicatorCheckProcessorTest");
        SpringApplication springApplication = new SpringApplication(
            HealthIndicatorConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }

}
