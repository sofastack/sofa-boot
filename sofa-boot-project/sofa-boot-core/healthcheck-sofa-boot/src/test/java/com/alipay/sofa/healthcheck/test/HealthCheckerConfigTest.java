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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthChecker;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HealthCheckerConfigTest
 *
 * @author xunfang
 * @version HealthCheckerConfigTest.java, v 0.1 2023/3/28
 */
public class HealthCheckerConfigTest {

    private ApplicationContext applicationContext;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    @ActiveProfiles("health")
    static class HealthIndicatorConfiguration {
        @Bean
        public DiskHealthChecker diskHealthChecker(@Value("${disk-health-checker.health}") boolean health) {
            return new DiskHealthChecker(health);
        }

        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health}") boolean health) {
            return new DiskHealthIndicator(health);
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
    public void testHealthCheckerConfig() {
        initApplicationContext(true);
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
            .getBean(HealthCheckerProcessor.class);
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        HashMap<String, Health> hashMap = new HashMap<>();

        HealthChecker diskHealthChecker = applicationContext.getBean(DiskHealthChecker.class);
        diskHealthChecker = healthCheckerProcessor
            .wrapperHealthCheckerForCustomConfig(diskHealthChecker);
        Assert.assertNotNull(diskHealthChecker);
        Assert.assertEquals(20, diskHealthChecker.getRetryCount());
        Assert.assertEquals(1000, diskHealthChecker.getRetryTimeInterval());
        Assert.assertTrue(diskHealthChecker.isStrictCheck());
        Assert.assertEquals(10000, diskHealthChecker.getTimeout());

        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get(diskHealthChecker.getComponentName());
        Assert.assertTrue(result);
        Assert.assertEquals(Status.UP, diskHealth.getStatus());

        try {
            healthIndicatorProcessor.readinessHealthCheck(hashMap);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Timeout must be greater than zero"));
        }

    }

    private void initApplicationContext(boolean health) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("disk-health-checker.health", health);
        properties.put("disk-health-indicator.health", health);
        properties.put("com.alipay.sofa.boot.health-checker-configs.diskHealthChecker.retryCount",
            20);
        properties
            .put("com.alipay.sofa.boot.health-checker-configs.diskHealthChecker.retryTimeInterval",
                1000);
        properties.put("com.alipay.sofa.boot.health-checker-configs.diskHealthChecker.strictCheck",
            true);
        properties.put("com.alipay.sofa.boot.health-checker-configs.diskHealthChecker.timeout",
            10000);

        properties.put("com.alipay.sofa.boot.health-indicator-configs.diskHealthIndicator.timeout",
            -1);

        properties.put(SofaBootConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK, "true");
        properties.put(SofaBootConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK, "true");
        properties.put("spring.application.name", "HealthIndicatorCheckProcessorTest");
        SpringApplication springApplication = new SpringApplication(
            HealthIndicatorConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }
}
