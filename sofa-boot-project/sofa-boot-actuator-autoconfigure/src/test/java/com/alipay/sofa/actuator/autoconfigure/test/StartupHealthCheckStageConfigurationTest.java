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
package com.alipay.sofa.actuator.autoconfigure.test;

import com.alipay.sofa.boot.actuator.autoconfigure.health.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.boot.actuator.autoconfigure.startup.StartupHealthCheckStageConfiguration;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.startup.SofaStartupAutoConfiguration;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.healthcheck.StartupReadinessCheckListener;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version StartupHealthCheckStageConfigurationTest.java, v 0.1 2021年01月05日 11:57 上午 huzijie Exp $
 */
public class StartupHealthCheckStageConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaStartupAutoConfiguration.class,
                                                                     SofaBootHealthCheckAutoConfiguration.class,
                                                                     StartupHealthCheckStageConfiguration.class,
                                                                     SofaRuntimeAutoConfiguration.class,
                                                                     TestHealthCheckConfiguration.class));

    @Test
    public void startupReporterAndHealthCheckerExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class, ModelCreatingStage.class))
                .run((context -> {
            assertThat(context).hasSingleBean(StartupReadinessCheckListener.class);
        }));
    }

    @Test
    public void healthCheckerNotExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(HealthChecker.class, ApplicationRuntimeModel.class, ModelCreatingStage.class))
                .run((context -> {
                    assertThat(context).doesNotHaveBean(ReadinessCheckListener.class);
                }));
    }

    @Test
    public void startupReporterNotExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(StartupReporter.class, ApplicationRuntimeModel.class, ModelCreatingStage.class))
                .run((context -> {
                    assertThat(context).hasSingleBean(ReadinessCheckListener.class);
                    assertThat(context).doesNotHaveBean(StartupReadinessCheckListener.class);
                }));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(value = { HealthEndpointProperties.class })
    static class TestHealthCheckConfiguration {
    }
}
