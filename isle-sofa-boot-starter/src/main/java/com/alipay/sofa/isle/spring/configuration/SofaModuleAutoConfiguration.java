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
package com.alipay.sofa.isle.spring.configuration;

import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.health.SofaModuleHealthChecker;
import com.alipay.sofa.isle.spring.health.SofaModuleHealthIndicator;
import com.alipay.sofa.isle.spring.listener.SofaModuleBeanFactoryPostProcessor;
import com.alipay.sofa.isle.spring.listener.SofaModuleContextRefreshedListener;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuanbei 18/3/12
 */
@Configuration
public class SofaModuleAutoConfiguration {
    @Bean
    public SofaModuleProperties sofaModuleProperties() {
        return new SofaModuleProperties();
    }

    @Bean
    public SofaModuleBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor() {
        return new SofaModuleBeanFactoryPostProcessor();
    }

    @Bean
    public SofaModuleContextRefreshedListener sofaModuleContextRefreshedListener() {
        return new SofaModuleContextRefreshedListener();
    }

    @Configuration
    @ConditionalOnClass({ HealthIndicator.class })
    @ConditionalOnMissingClass({ "com.alipay.sofa.healthcheck.core.HealthChecker" })
    public static class SofaModuleHealthIndicatorConfiguration {
        @Bean
        public SofaModuleHealthIndicator sofaModuleHealthIndicator() {
            return new SofaModuleHealthIndicator();
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class })
    public static class SofaModuleHealthCheckerConfiguration {
        @Bean
        public SofaModuleHealthChecker sofaModuleHealthChecker() {
            return new SofaModuleHealthChecker();
        }
    }
}
