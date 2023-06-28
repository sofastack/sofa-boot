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

import com.alipay.sofa.boot.actuator.startup.StartupEndpoint;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.startup.StartupReporter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupEndPointAutoConfiguration}.
 *
 * @author huzijie
 * @version StartupEndpointAutoConfigurationTests.java, v 0.1 2022年12月29日 5:58 PM huzijie Exp $
 */
public class StartupEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(
                                                                 AutoConfigurations
                                                                     .of(StartupEndPointAutoConfiguration.class))
                                                             .withClassLoader(
                                                                 new FilteredClassLoader(
                                                                     ApplicationRuntimeModel.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .withPropertyValues("management.endpoints.web.exposure.include=startup")
                .run((context) -> assertThat(context).hasSingleBean(StartupEndpoint.class));
    }

    @Test
    void runWhenNotStartupReporterBeanShouldNotHaveEndpointBean() {
        this.contextRunner
                .withPropertyValues("management.endpoints.web.exposure.include=startup")
                .run((context) -> assertThat(context).doesNotHaveBean(StartupEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .run((context) -> assertThat(context).doesNotHaveBean(StartupEndpoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .withPropertyValues("management.endpoint.startup.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(StartupEndpoint.class));
    }

    @Test
    void customStartupProperties() {
        this.contextRunner
                .withBean(StartupReporter.class)
                .withPropertyValues("management.endpoints.web.exposure.include=startup")
                .withPropertyValues("sofa.boot.startup.costThreshold=2000")
                .withPropertyValues("sofa.boot.startup.bufferSize=2048")
                .run((context) -> {
                    StartupProperties startupProperties = context.getBean(StartupProperties.class);
                    assertThat(startupProperties.getCostThreshold()).isEqualTo(2000);
                    assertThat(startupProperties.getBufferSize()).isEqualTo(2048);
                });
    }
}
