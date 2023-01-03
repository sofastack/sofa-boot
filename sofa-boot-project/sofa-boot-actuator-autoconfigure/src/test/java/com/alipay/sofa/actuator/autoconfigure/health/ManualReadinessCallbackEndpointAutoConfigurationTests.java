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
package com.alipay.sofa.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.autoconfigure.health.ManualReadinessCallbackEndpointAutoConfiguration;
import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ManualReadinessCallbackEndPoint;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ManualReadinessCallbackEndpointAutoConfiguration}.
 *
 * @author huzijie
 * @version ManualReadinessCallbackEndpointAutoConfigurationTests.java, v 0.1 2022年12月30日 2:08 PM huzijie Exp $
 */
public class ManualReadinessCallbackEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations
                    .of(ManualReadinessCallbackEndpointAutoConfiguration.class, MockReadinessCheckListenerConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=triggerReadinessCallback")
                .run((context) -> assertThat(context).hasSingleBean(ManualReadinessCallbackEndPoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(ManualReadinessCallbackEndPoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.triggerReadinessCallback.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(ManualReadinessCallbackEndPoint.class));
    }

    @Configuration(proxyBeanMethods = false)
    static class MockReadinessCheckListenerConfiguration {

        @Bean
        public MockReadinessCheckListener mockReadinessCheckListener() {
            return new MockReadinessCheckListener(null, null, null, null);
        }
    }

    static class MockReadinessCheckListener extends ReadinessCheckListener {


        public MockReadinessCheckListener(Environment environment, HealthCheckerProcessor healthCheckerProcessor, HealthIndicatorProcessor healthIndicatorProcessor, AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor) {
            super(environment, healthCheckerProcessor, healthIndicatorProcessor, afterReadinessCheckCallbackProcessor);
        }

        @Override
        public void onContextRefreshedEvent(ContextRefreshedEvent event) {
            // do nothing
        }
    }
}
