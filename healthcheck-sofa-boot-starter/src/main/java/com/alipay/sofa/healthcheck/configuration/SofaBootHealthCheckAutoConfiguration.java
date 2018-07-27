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
package com.alipay.sofa.healthcheck.configuration;

import com.alipay.sofa.healthcheck.core.AfterHealthCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.core.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.core.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.service.SofaBootHealthIndicator;
import com.alipay.sofa.healthcheck.service.SofaBootReadinessCheckEndpoint;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@Configuration
public class SofaBootHealthCheckAutoConfiguration {

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

    @Bean
    public AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor() {
        return new AfterHealthCheckCallbackProcessor();
    }

    @Bean
    public SofaBootHealthIndicator sofaBootHealthIndicator() {
        return new SofaBootHealthIndicator();
    }

    @ConditionalOnClass(Endpoint.class)
    public static class ConditionReadinessEndpointConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public SofaBootReadinessCheckEndpoint sofaBootReadinessCheckEndpoint() {
            return new SofaBootReadinessCheckEndpoint();
        }
    }
}