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

import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.health.SofaBootHealthIndicator;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaLogger;
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for readiness components.
 *
 * @author qilong.zql
 * @since 2.5.0
 */
@AutoConfiguration
@EnableConfigurationProperties(HealthProperties.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class ReadinessAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ReadinessCheckListener.class, search = SearchStrategy.CURRENT)
    public ReadinessCheckListener readinessCheckListener(HealthCheckerProcessor healthCheckerProcessor,
                                                         HealthIndicatorProcessor healthIndicatorProcessor,
                                                         ReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                         HealthProperties healthCheckProperties) {
        ReadinessCheckListener readinessCheckListener = new ReadinessCheckListener(
            healthCheckerProcessor, healthIndicatorProcessor, afterReadinessCheckCallbackProcessor);
        readinessCheckListener.setManualReadinessCallback(healthCheckProperties
            .isManualReadinessCallback());
        readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(healthCheckProperties
            .isInsulator());
        readinessCheckListener.setSkipAll(healthCheckProperties.isSkipAll());
        readinessCheckListener.setSkipHealthChecker(healthCheckProperties.isSkipHealthChecker());
        readinessCheckListener
            .setSkipHealthIndicator(healthCheckProperties.isSkipHealthIndicator());
        return readinessCheckListener;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthCheckerProcessor healthCheckerProcessor(HealthProperties healthCheckProperties,
                                                         ThreadPoolExecutor healthCheckExecutor) {
        HealthCheckerProcessor healthCheckerProcessor = new HealthCheckerProcessor();
        healthCheckerProcessor.setHealthCheckExecutor(healthCheckExecutor);
        healthCheckerProcessor.setParallelCheck(healthCheckProperties.isParallelCheck());
        healthCheckerProcessor.setParallelCheckTimeout(healthCheckProperties
            .getParallelCheckTimeout());
        healthCheckerProcessor.setGlobalTimeout(healthCheckProperties
            .getGlobalHealthCheckerTimeout());
        healthCheckerProcessor.setHealthCheckerConfigs(healthCheckProperties
            .getHealthCheckerConfig());
        return healthCheckerProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthIndicatorProcessor healthIndicatorProcessor(HealthProperties healthCheckProperties,
                                                             ThreadPoolExecutor healthCheckExecutor) {
        HealthIndicatorProcessor healthIndicatorProcessor = new HealthIndicatorProcessor();
        healthIndicatorProcessor.setHealthCheckExecutor(healthCheckExecutor);
        healthIndicatorProcessor.initExcludedIndicators(healthCheckProperties
            .getExcludedIndicators());
        healthIndicatorProcessor.setParallelCheck(healthCheckProperties.isParallelCheck());
        healthIndicatorProcessor.setParallelCheckTimeout(healthCheckProperties
            .getParallelCheckTimeout());
        healthIndicatorProcessor.setGlobalTimeout(healthCheckProperties
            .getGlobalHealthIndicatorTimeout());
        healthIndicatorProcessor.setHealthIndicatorConfig(healthCheckProperties
            .getHealthIndicatorConfig());
        return healthIndicatorProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
        return new ReadinessCheckCallbackProcessor();
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
        return new SofaThreadPoolExecutor(threadPoolSize, threadPoolSize, 30, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NamedThreadFactory("health-check"),
            new ThreadPoolExecutor.CallerRunsPolicy(), "health-check",
            SofaBootConstants.SOFA_BOOT_SPACE_NAME);
    }
}
