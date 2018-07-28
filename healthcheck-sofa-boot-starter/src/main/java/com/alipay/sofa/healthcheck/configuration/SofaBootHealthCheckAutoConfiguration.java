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
import com.alipay.sofa.healthcheck.service.SofaBootReadinessCheckMvcEndpoint;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.alipay.sofa.healthcheck.configuration.HealthCheckConstants.READINESS_CHECK_ENDPOINT_NAME;

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

    @Bean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.healthcheck", name = "enabled", matchIfMissing = true)
    public SofaBootReadinessCheckEndpoint readinessCheck() {
        return new SofaBootReadinessCheckEndpoint(READINESS_CHECK_ENDPOINT_NAME, false);
    }

    @Bean
    @ConditionalOnBean(SofaBootReadinessCheckEndpoint.class)
    @ConditionalOnWebApplication
    public SofaBootReadinessCheckMvcEndpoint sofaBootReadinessCheckMvcEndpoint(SofaBootReadinessCheckEndpoint delegate) {
        return new SofaBootReadinessCheckMvcEndpoint(delegate);
    }
}