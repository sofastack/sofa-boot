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

import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.common.thread.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import org.slf4j.Logger;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for readiness components.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
@EnableConfigurationProperties(HealthProperties.class)
public class ReadinessAutoConfiguration {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(ReadinessAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(value = ReadinessCheckListener.class)
    public ReadinessCheckListener readinessCheckListener(HealthCheckerProcessor healthCheckerProcessor,
                                                         HealthIndicatorProcessor healthIndicatorProcessor,
                                                         ReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                         ThreadPoolExecutor readinessHealthCheckExecutor,
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
        readinessCheckListener.setHealthCheckExecutor(readinessHealthCheckExecutor);
        return readinessCheckListener;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthCheckerProcessor healthCheckerProcessor(HealthProperties healthCheckProperties,
                                                         ThreadPoolExecutor readinessHealthCheckExecutor) {
        HealthCheckerProcessor healthCheckerProcessor = new HealthCheckerProcessor();
        healthCheckerProcessor.setHealthCheckExecutor(readinessHealthCheckExecutor);
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
                                                             ThreadPoolExecutor readinessHealthCheckExecutor) {
        HealthIndicatorProcessor healthIndicatorProcessor = new HealthIndicatorProcessor();
        healthIndicatorProcessor.setHealthCheckExecutor(readinessHealthCheckExecutor);
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

    @Bean(name = ReadinessCheckListener.READINESS_HEALTH_CHECK_EXECUTOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = ReadinessCheckListener.READINESS_HEALTH_CHECK_EXECUTOR_BEAN_NAME)
    public ThreadPoolExecutor readinessHealthCheckExecutor(HealthProperties properties) {
        int threadPoolSize;
        if (properties.isParallelCheck()) {
            threadPoolSize = SofaBootConstants.CPU_CORE * 5;
        } else {
            threadPoolSize = 1;
        }
        LOGGER.info("Create health-check thread pool, corePoolSize: {}, maxPoolSize: {}.",
            threadPoolSize, threadPoolSize);
        return new SofaThreadPoolExecutor(threadPoolSize, threadPoolSize, 30, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new NamedThreadFactory("health-check"),
            new ThreadPoolExecutor.CallerRunsPolicy(), "health-check",
            SofaBootConstants.SOFA_BOOT_SPACE_NAME);
    }
}
