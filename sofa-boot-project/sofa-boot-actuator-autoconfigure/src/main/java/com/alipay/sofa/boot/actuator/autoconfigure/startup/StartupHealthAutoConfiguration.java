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
package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import com.alipay.sofa.boot.actuator.autoconfigure.health.HealthProperties;
import com.alipay.sofa.boot.actuator.autoconfigure.health.ReadinessAutoConfiguration;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.health.StartupReadinessCheckListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for startup health components.
 *
 * @author huzijie
 * @version StartupHealthAutoConfiguration.java, v 0.1 2023年01月04日 2:40 PM huzijie Exp $
 */
@AutoConfiguration(before = ReadinessAutoConfiguration.class, after = StartupAutoConfiguration.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
@ConditionalOnBean(StartupReporter.class)
public class StartupHealthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ReadinessCheckListener.class, search = SearchStrategy.CURRENT)
    public StartupReadinessCheckListener startupReadinessCheckListener(HealthCheckerProcessor healthCheckerProcessor,
                                                                       HealthIndicatorProcessor healthIndicatorProcessor,
                                                                       ReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                                       HealthProperties healthCheckProperties,
                                                                       StartupReporter startupReporter) {
        StartupReadinessCheckListener readinessCheckListener = new StartupReadinessCheckListener(
            healthCheckerProcessor, healthIndicatorProcessor, afterReadinessCheckCallbackProcessor,
            startupReporter);
        readinessCheckListener.setManualReadinessCallback(healthCheckProperties
            .isManualReadinessCallback());
        readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(healthCheckProperties
            .isInsulator());
        return readinessCheckListener;
    }
}
