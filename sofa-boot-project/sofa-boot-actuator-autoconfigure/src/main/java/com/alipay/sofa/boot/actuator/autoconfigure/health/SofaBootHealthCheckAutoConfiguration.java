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
package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.ManualReadinessCallbackEndPoint;
import com.alipay.sofa.boot.actuator.health.MultiApplicationHealthIndicator;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpointWebExtension;
import com.alipay.sofa.boot.actuator.health.SofaBootHealthIndicator;
import com.alipay.sofa.boot.actuator.health.SofaBootReadinessEndpoint;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.impl.ComponentHealthChecker;
import com.alipay.sofa.healthcheck.impl.ModuleHealthChecker;
import com.alipay.sofa.healthcheck.impl.SofaRuntimeHealthChecker;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HealthCheckProperties.class)
@ConditionalOnClass(HealthChecker.class)
public class SofaBootHealthCheckAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint(endpoint = ManualReadinessCallbackEndPoint.class)
    @ConditionalOnProperty(prefix = SofaBootConstants.PREFIX, name = "manualReadinessCallback", havingValue = "true")
    public ManualReadinessCallbackEndPoint manualReadinessCallbackEndPoint(ReadinessCheckListener readinessCheckListener) {
        return new ManualReadinessCallbackEndPoint(readinessCheckListener);
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
    public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
        return new AfterReadinessCheckCallbackProcessor();
    }

    @Bean
    public SofaBootHealthIndicator sofaBootHealthIndicator(HealthCheckerProcessor healthCheckerProcessor,
                                                           ReadinessCheckListener readinessCheckListener) {
        return new SofaBootHealthIndicator(healthCheckerProcessor, readinessCheckListener);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public SofaRuntimeHealthChecker defaultRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext,
                                                                List<HealthIndicator> healthIndicators,
                                                                ReadinessCheckListener readinessCheckListener) {
        return new SofaRuntimeHealthChecker(sofaRuntimeContext, healthIndicators,
            readinessCheckListener);
    }

    @Bean
    @ConditionalOnClass(name = { "com.alipay.sofa.ark.spi.model.Biz" })
    public MultiApplicationHealthIndicator multiApplicationHealthIndicator() {
        return new MultiApplicationHealthIndicator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint(endpoint = SofaBootReadinessEndpoint.class)
    public SofaBootReadinessEndpoint sofaBootReadinessCheckEndpoint(ReadinessCheckListener readinessCheckListener) {
        return new SofaBootReadinessEndpoint(readinessCheckListener);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public ComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        return new ComponentHealthChecker(sofaRuntimeContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthCheckExecutor healthCheckExecutor(HealthCheckProperties properties) {
        return new HealthCheckExecutor(properties);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ HealthChecker.class, ModelCreatingStage.class })
    @ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
    public static class SofaModuleHealthIndicatorConfiguration {
        @Bean
        public ModuleHealthChecker sofaModuleHealthChecker() {
            return new ModuleHealthChecker();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @AutoConfigureBefore(HealthEndpointAutoConfiguration.class)
    @ConditionalOnClass(HealthChecker.class)
    public static class ReadinessCheckExtensionConfiguration {
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpointWebExtension.class)
        public ReadinessEndpointWebExtension readinessEndpointWebExtension(SofaBootReadinessEndpoint delegate,
                                                                           HttpCodeStatusMapper statusMapper) {
            return new ReadinessEndpointWebExtension(delegate, statusMapper);
        }

        @Bean
        @ConditionalOnMissingBean
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public HttpCodeStatusMapper httpCodeStatusMapper(HealthEndpointProperties healthEndpointProperties) {
            return new SofaHttpCodeStatusMapper(healthEndpointProperties);
        }
    }
}
