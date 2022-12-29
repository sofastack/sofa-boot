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
package com.alipay.sofa.boot.actuator.health.manual;

import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.core.HealthChecker;
import com.alipay.sofa.boot.actuator.health.bean.DiskHealthIndicator;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
                                  "spring.application.name=ManualReadinessCheckListenerSuccessTest",
                                  "com.alipay.sofa.boot.manualReadinessCallback=true" })
public class ManualReadinessCheckListenerSuccessTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    static class HealthCheckConfiguration {
        @Bean
        public HealthChecker myHealthChecker() {
            return new HealthChecker() {
                @Override
                public Health isHealthy() {
                    return Health.up().withDetail("health", "success").build();
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
    public void testReadinessCheck() throws BeansException {
        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.assertFalse(readinessCheckListener.getReadinessCallbackTriggered().get());
        Assert.assertTrue(readinessCheckListener.getHealthCheckerStatus());
        Assert.assertTrue(readinessCheckListener.getHealthIndicatorStatus());

        ReadinessCheckListener.ManualReadinessCallbackResult result = readinessCheckListener
            .triggerReadinessCallback();
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(result.getDetails().contains("successfully with result:"));
        Assert.assertTrue(readinessCheckListener.getReadinessCallbackTriggered().get());
    }
}
