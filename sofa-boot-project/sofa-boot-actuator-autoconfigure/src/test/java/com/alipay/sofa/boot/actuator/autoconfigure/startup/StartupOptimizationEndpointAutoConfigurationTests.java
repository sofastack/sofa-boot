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

import com.alipay.sofa.boot.actuator.startup.StartupOptimizationEndpoint;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.startup.StartupOptimizer;
import com.alipay.sofa.boot.startup.StartupReporter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupOptimizationEndpointAutoConfiguration}.
 *
 * @author OpenAI
 */
public class StartupOptimizationEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(
                                                                 AutoConfigurations
                                                                     .of(StartupOptimizationEndpointAutoConfiguration.class))
                                                             .withClassLoader(
                                                                 new FilteredClassLoader(
                                                                     ApplicationRuntimeModel.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .withPropertyValues("management.endpoints.web.exposure.include=startup-optimization")
                .run((context) -> assertThat(context)
                        .hasSingleBean(StartupOptimizer.class)
                        .hasSingleBean(StartupOptimizationEndpoint.class));
    }

    @Test
    void runWhenNotStartupReporterBeanShouldNotHaveEndpointBean() {
        this.contextRunner
                .withPropertyValues("management.endpoints.web.exposure.include=startup-optimization")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(StartupOptimizer.class)
                        .doesNotHaveBean(StartupOptimizationEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(StartupOptimizationEndpoint.class));
    }
}
