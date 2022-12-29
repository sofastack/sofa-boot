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

import com.alipay.sofa.boot.actuator.health.bean.TestHealthIndicator;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
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
public class HealthIndicatorCheckProcessorParallelTest {

    private ApplicationContext applicationContext;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    static class HealthIndicatorConfiguration {
        @Bean
        public TestHealthIndicator testAHealthIndicator(@Value("${disk-health-indicator.health}") boolean health) {
            return new TestHealthIndicator(health);
        }

        @Bean
        public TestHealthIndicator testBHealthIndicator(@Value("${disk-health-indicator.health}") boolean health) {
            return new TestHealthIndicator(health);
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
            properties.setHealthCheckParallelEnable(true);
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
        Assert.assertTrue(result);
        Assert.assertEquals(2, hashMap.size());
    }

    @Test
    public void testCheckIndicatorFailed() {
        initApplicationContext(false);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Assert.assertFalse(result);
        Assert.assertEquals(2, hashMap.size());
    }

    private void initApplicationContext(boolean health) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("disk-health-indicator.health", health);
        properties.put("spring.application.name", "HealthIndicatorCheckProcessorTest");
        SpringApplication springApplication = new SpringApplication(
            HealthIndicatorConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }

}
