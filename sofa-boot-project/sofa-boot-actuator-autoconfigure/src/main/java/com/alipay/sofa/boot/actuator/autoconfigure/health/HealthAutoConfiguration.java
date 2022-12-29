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

import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.health.SofaBootHealthIndicator;
import com.alipay.sofa.boot.actuator.health.impl.ComponentHealthChecker;
import com.alipay.sofa.boot.actuator.health.impl.ModuleHealthChecker;
import com.alipay.sofa.boot.actuator.health.impl.SofaRuntimeHealthChecker;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaLogger;
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@AutoConfiguration
@EnableConfigurationProperties(HealthProperties.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class HealthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReadinessCheckListener readinessCheckListener(Environment environment,
                                                         HealthCheckerProcessor healthCheckerProcessor,
                                                         HealthIndicatorProcessor healthIndicatorProcessor,
                                                         AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                         HealthProperties healthCheckProperties) {
        ReadinessCheckListener readinessCheckListener = new ReadinessCheckListener(environment, healthCheckerProcessor,
            healthIndicatorProcessor, afterReadinessCheckCallbackProcessor);
        readinessCheckListener.setManualReadinessCallback(healthCheckProperties.isManualReadinessCallback());
        readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(healthCheckProperties.isInsulator());
        return readinessCheckListener;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthCheckerProcessor healthCheckerProcessor(HealthProperties healthCheckProperties,
                                                         ThreadPoolExecutor healthCheckExecutor) {
        HealthCheckerProcessor healthCheckerProcessor = new HealthCheckerProcessor(healthCheckExecutor);
        healthCheckerProcessor.setParallelCheck(healthCheckProperties.isParallelCheck());
        healthCheckerProcessor.setParallelCheckTimeout(healthCheckProperties.getParallelCheckTimeout());
        return healthCheckerProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthIndicatorProcessor healthIndicatorProcessor(HealthProperties healthCheckProperties,
                                                             ThreadPoolExecutor healthCheckExecutor) {
        HealthIndicatorProcessor healthIndicatorProcessor = new HealthIndicatorProcessor(healthCheckExecutor);
        healthIndicatorProcessor.initExcludedIndicators(healthCheckProperties.getExcludedIndicators());
        healthIndicatorProcessor.setParallelCheck(healthCheckProperties.isParallelCheck());
        healthIndicatorProcessor.setParallelCheckTimeout(healthCheckProperties.getParallelCheckTimeout());
        return healthIndicatorProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
        return new AfterReadinessCheckCallbackProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaBootHealthIndicator sofaBootHealthIndicator(HealthCheckerProcessor healthCheckerProcessor,
                                                           ReadinessCheckListener readinessCheckListener) {
        return new SofaBootHealthIndicator(healthCheckerProcessor, readinessCheckListener);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolExecutor healthCheckExecutor(HealthProperties properties) {
        int threadPoolSize;
        if (properties.isParallelCheck()) {
            threadPoolSize = Runtime.getRuntime().availableProcessors() * 5;
        } else {
            threadPoolSize = 1;
        }
        SofaLogger.info("Create health-check thread pool, corePoolSize: {}, maxPoolSize: {}.",
                threadPoolSize, threadPoolSize);
        return new SofaThreadPoolExecutor(threadPoolSize, threadPoolSize, 30,
                TimeUnit.SECONDS, new SynchronousQueue<>(), new NamedThreadFactory("health-check"),
                new ThreadPoolExecutor.CallerRunsPolicy(), "health-check",
                SofaBootConstants.SOFABOOT_SPACE_NAME);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.alipay.sofa.isle.stage.ModelCreatingStage")
    @ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
    static class SofaModuleHealthIndicatorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ModuleHealthChecker sofaModuleHealthChecker() {
            return new ModuleHealthChecker();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.alipay.sofa.runtime.spi.component.SofaRuntimeContext")
    @ConditionalOnBean(SofaRuntimeContext.class)
    static class SofaRuntimeHealthIndicatorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new ComponentHealthChecker(sofaRuntimeContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaRuntimeHealthChecker defaultRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext,
                                                                    List<HealthIndicator> healthIndicators,
                                                                    ReadinessCheckListener readinessCheckListener) {
            return new SofaRuntimeHealthChecker(sofaRuntimeContext, healthIndicators, readinessCheckListener);
        }
    }
}
