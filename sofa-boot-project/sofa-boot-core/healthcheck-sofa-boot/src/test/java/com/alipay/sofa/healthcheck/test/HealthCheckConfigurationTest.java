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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class HealthCheckConfigurationTest {

    private ApplicationContext applicationContext;

    @Test
    public void testInject() {
        initApplicationContext(new HashMap<String, Object>(),
            HealthCheckConfigurationTestConfiguration.class);
        Assert.notNull(applicationContext.getBean(ReadinessCheckListener.class));
        Assert.notNull(applicationContext.getBean(HealthCheckerProcessor.class));
        Assert.notNull(applicationContext.getBean(HealthIndicatorProcessor.class));
        Assert.notNull(applicationContext.getBean(AfterReadinessCheckCallbackProcessor.class));

        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.isTrue(!readinessCheckListener.skipIndicator());
        Assert.isTrue(!readinessCheckListener.skipComponent());
        Assert.isTrue(!readinessCheckListener.skipAllCheck());
    }

    @Test
    public void testHealthCheckConfiguration() {
        HashMap<String, Object> healthCheckConfiguration = new HashMap<>();
        healthCheckConfiguration.put(SofaBootConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK, true);
        healthCheckConfiguration.put(SofaBootConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK, true);
        healthCheckConfiguration.put(SofaBootConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK, true);
        initApplicationContext(healthCheckConfiguration,
            HealthCheckConfigurationTestConfiguration.class);

        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.isTrue(readinessCheckListener.skipIndicator());
        Assert.isTrue(readinessCheckListener.skipComponent());
        Assert.isTrue(readinessCheckListener.skipAllCheck());
    }

    private void initApplicationContext(Map<String, Object> properties, Class configuration) {
        properties.put("spring.application.name", "HealthCheckConfigurationTest");
        SpringApplication springApplication = new SpringApplication(configuration);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }

    @Configuration
    static class HealthCheckConfigurationTestConfiguration {
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
}