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

import com.alipay.sofa.boot.actuator.autoconfigure.health.HealthAutoConfiguration;
import com.alipay.sofa.boot.actuator.autoconfigure.health.HealthProperties;
import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.startup.StartupEndPoint;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.stage.BeanCostBeanPostProcessor;
import com.alipay.sofa.boot.actuator.startup.stage.StartupContextRefreshedListener;
import com.alipay.sofa.boot.actuator.startup.stage.health.StartupReadinessCheckListener;
import com.alipay.sofa.boot.actuator.startup.stage.isle.StartupModelCreatingStage;
import com.alipay.sofa.boot.actuator.startup.stage.isle.StartupSpringContextInstallStage;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author Zhijie
 * @since 2020/7/8
 */
@AutoConfiguration
@EnableConfigurationProperties(StartupProperties.class)
@ConditionalOnAvailableEndpoint(endpoint = StartupEndPoint.class)
public class StartupAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StartupReporter sofaStartupReporter(Environment environment) {
        return new StartupReporter(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public static BeanCostBeanPostProcessor beanCostBeanPostProcessor(StartupProperties startupProperties) {
        return new BeanCostBeanPostProcessor(startupProperties.getBeanInitCostThreshold(),
            startupProperties.isSkipSofaBean());
    }

    @Bean
    @ConditionalOnMissingBean
    public StartupContextRefreshedListener startupContextRefreshedListener() {
        return new StartupContextRefreshedListener();
    }

    @AutoConfiguration(before = HealthAutoConfiguration.class)
    @ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
    static class StartupHealthAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = ReadinessCheckListener.class, search = SearchStrategy.CURRENT)
        public StartupReadinessCheckListener startupReadinessCheckListener(Environment environment,
                                                                           HealthCheckerProcessor healthCheckerProcessor,
                                                                           HealthIndicatorProcessor healthIndicatorProcessor,
                                                                           AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                                           HealthProperties healthCheckProperties,
                                                                           StartupReporter startupReporter) {
            StartupReadinessCheckListener readinessCheckListener = new StartupReadinessCheckListener(
                    environment, healthCheckerProcessor, healthIndicatorProcessor,
                    afterReadinessCheckCallbackProcessor, startupReporter);
            readinessCheckListener.setManualReadinessCallback(healthCheckProperties
                    .isManualReadinessCallback());
            readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(healthCheckProperties
                    .isInsulator());
            return readinessCheckListener;
        }
    }

    @AutoConfiguration(before = SofaModuleAutoConfiguration.class)
    @ConditionalOnClass({ ApplicationRuntimeModel.class })
    @ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
    static class StartupIsleAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
        public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext,
                                                                                 SofaModuleProperties sofaModuleProperties,
                                                                                 StartupReporter startupReporter) {
            return new StartupSpringContextInstallStage(
                (AbstractApplicationContext) applicationContext, sofaModuleProperties,
                startupReporter);
        }

        @Bean
        @ConditionalOnMissingBean(value = ModelCreatingStage.class, search = SearchStrategy.CURRENT)
        public StartupModelCreatingStage startupModelCreatingStage(ApplicationContext applicationContext,
                                                                   SofaModuleProperties sofaModuleProperties,
                                                                   SofaModuleProfileChecker sofaModuleProfileChecker,
                                                                   StartupReporter startupReporter) {
            return new StartupModelCreatingStage((AbstractApplicationContext) applicationContext,
                sofaModuleProperties, sofaModuleProfileChecker, startupReporter);
        }
    }
}
