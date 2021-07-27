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

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "spring.application.name=ManualReadinessCheckListenerTest",
                                  "com.alipay.sofa.boot.manualReadinessCallback=true" })
public class ManualReadinessCheckListenerTest {
    @Autowired
    private ApplicationContext      applicationContext;

    @Autowired
    private ApplicationAvailability applicationAvailability;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    @Import(ApplicationAvailabilityAutoConfiguration.class)
    static class HealthCheckConfiguration {
        @Bean
        public HealthChecker myHealthChecker() {
            return new HealthChecker() {
                @Override
                public Health isHealthy() {
                    return Health.down().withDetail("health", "failed").build();
                }

                @Override
                public String getComponentName() {
                    return "myHealthChecker";
                }
            };
        }

        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health:true}") boolean health) {
            return new DiskHealthIndicator(health);
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
        Assert.assertFalse(readinessCheckListener.getReadinessCallbackTriggered().get());
        Assert.assertFalse(readinessCheckListener.getHealthCheckerStatus());
        Assert.assertTrue(readinessCheckListener.getHealthIndicatorStatus());

        ReadinessCheckListener.ManualReadinessCallbackResult result = readinessCheckListener
            .triggerReadinessCallback();
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getDetails().contains("checker or indicator failed"));
        Assert.assertFalse(readinessCheckListener.getReadinessCallbackTriggered().get());
    }

    @Test
    public void testAvailabilityReadinessDown() {
        Assert.assertEquals(ReadinessState.REFUSING_TRAFFIC,
            applicationAvailability.getReadinessState());
    }
}
