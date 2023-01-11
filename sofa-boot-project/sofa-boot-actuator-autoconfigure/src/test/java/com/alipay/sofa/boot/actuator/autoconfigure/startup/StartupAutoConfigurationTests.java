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

import com.alipay.sofa.boot.actuator.autoconfigure.health.ReadinessAutoConfiguration;
import com.alipay.sofa.boot.actuator.startup.BeanCostBeanPostProcessor;
import com.alipay.sofa.boot.actuator.startup.StartupContextRefreshedListener;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.health.StartupReadinessCheckListener;
import com.alipay.sofa.boot.actuator.startup.isle.StartupModelCreatingStage;
import com.alipay.sofa.boot.actuator.startup.isle.StartupSpringContextInstallStage;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupAutoConfiguration}.
 *
 * @author huzijie
 * @version StartupAutoConfigurationTests.java, v 0.1 2022年12月29日 6:00 PM huzijie Exp $
 */
public class StartupAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(
                                                                 AutoConfigurations
                                                                     .of(StartupAutoConfiguration.class))
                                                             .withPropertyValues(
                                                                 "management.endpoints.web.exposure.include=startup");

    @Test
    void runShouldHaveStartupBeans() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .run((context) -> assertThat(context)
                .hasSingleBean(StartupReporter.class)
                .hasSingleBean(BeanCostBeanPostProcessor.class)
                .hasSingleBean(StartupContextRefreshedListener.class)
                .doesNotHaveBean(StartupReadinessCheckListener.class)
                .doesNotHaveBean(StartupSpringContextInstallStage.class)
                .doesNotHaveBean(StartupModelCreatingStage.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveStartupBeans() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .withPropertyValues("management.endpoints.web.exposure.include=info")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(StartupReporter.class)
                        .doesNotHaveBean(BeanCostBeanPostProcessor.class)
                        .doesNotHaveBean(StartupContextRefreshedListener.class));
    }

    @Test
    void customBeanCostBeanPostProcessorSkipSofaBean() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .withPropertyValues("sofa.boot.actuator.startup.skipSofaBean=true")
                .run((context) -> assertThat(context.getBean(BeanCostBeanPostProcessor.class).isSkipSofaBean()).isTrue());
    }

    @Test
    void customBeanCostBeanPostProcessorBeanLoadCost() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .withPropertyValues("sofa.boot.actuator.startup.beanInitCostThreshold=10")
                .run((context) -> assertThat(context.getBean(BeanCostBeanPostProcessor.class).getBeanLoadCost()).isEqualTo(10));
    }

    @Test
    void runWhenHaveReadinessEndpoints() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(StartupHealthAutoConfiguration.class, ReadinessAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .withPropertyValues("management.endpoints.web.exposure.include=readiness,startup")
                .run((context) -> assertThat(context)
                        .hasSingleBean(StartupReadinessCheckListener.class));
    }

    @Test
    void runWhenHaveIsleClasses() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(StartupIsleAutoConfiguration.class, SofaModuleAutoConfiguration.class, SofaRuntimeAutoConfiguration.class))
                .run((context) -> assertThat(context).hasSingleBean(StartupSpringContextInstallStage.class)
                        .hasSingleBean(StartupModelCreatingStage.class));
    }
}
