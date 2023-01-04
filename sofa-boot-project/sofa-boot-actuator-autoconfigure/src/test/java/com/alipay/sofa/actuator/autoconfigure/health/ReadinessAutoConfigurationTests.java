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
package com.alipay.sofa.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.autoconfigure.health.ReadinessAutoConfiguration;
import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.ComponentHealthChecker;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ModuleHealthChecker;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.SofaBootHealthIndicator;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReadinessAutoConfiguration}.
 *
 * @author huzijie
 * @version ReadinessAutoConfigurationTests.java, v 0.1 2022年12月30日 2:08 PM huzijie Exp $
 */
public class ReadinessAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ReadinessAutoConfiguration.class))
            .withPropertyValues("management.endpoints.web.exposure.include=readiness");


    @Test
    void runShouldHaveReadinessBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(ReadinessCheckListener.class)
                        .hasSingleBean(HealthCheckerProcessor.class)
                        .hasSingleBean(HealthIndicatorProcessor.class)
                        .hasSingleBean(AfterReadinessCheckCallbackProcessor.class)
                        .hasSingleBean(SofaBootHealthIndicator.class)
                        .hasBean("healthCheckExecutor"));
    }

    @Test
    void runWhenNotExposedShouldNotHaveReadinessBeans() {
        this.contextRunner
                .withPropertyValues("management.endpoints.web.exposure.include=info")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ReadinessCheckListener.class)
                        .doesNotHaveBean(HealthCheckerProcessor.class)
                        .doesNotHaveBean(HealthIndicatorProcessor.class)
                        .doesNotHaveBean(AfterReadinessCheckCallbackProcessor.class)
                        .doesNotHaveBean(SofaBootHealthIndicator.class)
                        .doesNotHaveBean("healthCheckExecutor"));
    }

    @Test
    void customReadinessCheckListenerInsulator() {
        this.contextRunner
                .withPropertyValues("sofa.boot.actuator.health.insulator=true")
                .run((context) -> assertThat(context.getBean(ReadinessCheckListener.class).isThrowExceptionWhenHealthCheckFailed()).isTrue());
    }

    @Test
    void customReadinessCheckListenerManualReadinessCallback() {
        this.contextRunner
                .withPropertyValues("sofa.boot.actuator.health.manualReadinessCallback=true")
                .run((context) -> assertThat(context.getBean(ReadinessCheckListener.class).isManualReadinessCallback()).isTrue());
    }

    @Test
    void customHealthCheckParallelCheck() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .withPropertyValues("sofa.boot.actuator.health.parallelCheck=false")
                .withPropertyValues("sofa.boot.actuator.health.parallelCheckTimeout=30000")
                .run((context) -> {
                    assertThat(context.getBean(HealthCheckerProcessor.class).isParallelCheck()).isFalse();
                    assertThat(context.getBean(HealthCheckerProcessor.class).getParallelCheckTimeout()).isEqualTo(30000);
                    assertThat(context.getBean(HealthIndicatorProcessor.class).isParallelCheck()).isFalse();
                    assertThat(context.getBean(HealthIndicatorProcessor.class).getParallelCheckTimeout()).isEqualTo(30000);
                    assertThat(context.getBean("healthCheckExecutor", ThreadPoolExecutor.class).getCorePoolSize()).isEqualTo(1);
                });
    }

    @Test
    void runWhenWithIsleConfiguration() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(ReadinessAutoConfiguration.SofaModuleHealthIndicatorConfiguration.class,
                        SofaRuntimeAutoConfiguration.class, SofaModuleAutoConfiguration.class))
                .run((context) -> assertThat(context).hasSingleBean(ModuleHealthChecker.class));
    }

    @Test
    void runWhenNotHaveIsleConfigurationWithoutBeans() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(ReadinessAutoConfiguration.SofaModuleHealthIndicatorConfiguration.class,
                        SofaRuntimeAutoConfiguration.class, SofaModuleAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .run((context) -> assertThat(context).doesNotHaveBean(ModuleHealthChecker.class));
    }

    @Test
    void runWhenWithRuntimeConfiguration() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(ReadinessAutoConfiguration.SofaRuntimeHealthIndicatorConfiguration.class, SofaRuntimeAutoConfiguration.class))
                .run((context) -> assertThat(context).hasSingleBean(ComponentHealthChecker.class));
    }

    @Test
    void runWhenNotHaveRuntimeConfigurationWithoutBeans() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(ReadinessAutoConfiguration.SofaRuntimeHealthIndicatorConfiguration.class,
                        SofaRuntimeAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(SofaRuntimeContext.class))
                .run((context) -> assertThat(context).doesNotHaveBean(ComponentHealthChecker.class));
    }
}
