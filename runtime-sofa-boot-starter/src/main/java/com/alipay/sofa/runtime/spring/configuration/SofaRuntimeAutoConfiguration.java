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
package com.alipay.sofa.runtime.spring.configuration;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spring.callback.CloseApplicationContextCallBack;
import com.alipay.sofa.runtime.spring.config.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.spring.health.DefaultRuntimeHealthChecker;
import com.alipay.sofa.runtime.spring.health.MultiApplicationHealthIndicator;
import com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker;
import com.alipay.sofa.runtime.spring.health.SofaComponentHealthIndicator;

/**
 * @author xuanbei 18/3/17
 */
@Configuration
@EnableConfigurationProperties(SofaRuntimeConfigurationProperties.class)
public class SofaRuntimeAutoConfiguration {
    @Bean
    public CloseApplicationContextCallBack closeApplicationContextCallBack() {
        return new CloseApplicationContextCallBack();
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class DefaultRuntimeHealthCheckerConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public DefaultRuntimeHealthChecker defaultRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new DefaultRuntimeHealthChecker(sofaRuntimeContext);
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class, Biz.class })
    public static class MultiApplicationHealthIndicatorConfiguration {
        @Bean
        public MultiApplicationHealthIndicator multiApplicationHealthIndicator() {
            return new MultiApplicationHealthIndicator();
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthIndicator.class })
    @ConditionalOnMissingClass({ "com.alipay.sofa.healthcheck.core.HealthChecker" })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class SofaRuntimeHealthIndicatorConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public SofaComponentHealthIndicator sofaComponentHealthIndicator(SofaRuntimeContext sofaRuntimeContext) {
            return new SofaComponentHealthIndicator(sofaRuntimeContext);
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class SofaModuleHealthCheckerConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public SofaComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new SofaComponentHealthChecker(sofaRuntimeContext);
        }
    }
}
