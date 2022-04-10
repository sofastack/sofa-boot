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
package com.alipay.sofa.startup.test.configuration;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.healthcheck.StartupReadinessCheckListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author huzijie
 * @version SofaStartupHealthCheckAutoConfiguration.java, v 0.1 2021年01月04日 9:19 下午 huzijie Exp $
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ReadinessCheckListener.class, StartupReporter.class })
public class SofaStartupHealthCheckAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ReadinessCheckListener.class, search = SearchStrategy.CURRENT)
    public StartupReadinessCheckListener startupReadinessCheckListener(Environment environment,
                                                                       HealthCheckerProcessor healthCheckerProcessor,
                                                                       HealthIndicatorProcessor healthIndicatorProcessor,
                                                                       AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                                       SofaRuntimeConfigurationProperties sofaRuntimeConfigurationProperties,
                                                                       HealthCheckProperties healthCheckProperties,
                                                                       StartupReporter startupReporter) {
        return new StartupReadinessCheckListener(environment, healthCheckerProcessor,
            healthIndicatorProcessor, afterReadinessCheckCallbackProcessor,
            sofaRuntimeConfigurationProperties, healthCheckProperties, startupReporter);
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
    public HealthCheckExecutor healthCheckExecutor(HealthCheckProperties properties) {
        return new HealthCheckExecutor(properties);
    }
}
