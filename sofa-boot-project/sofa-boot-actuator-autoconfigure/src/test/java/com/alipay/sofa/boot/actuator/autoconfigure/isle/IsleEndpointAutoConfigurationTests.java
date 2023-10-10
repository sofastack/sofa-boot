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
package com.alipay.sofa.boot.actuator.autoconfigure.isle;

import com.alipay.sofa.boot.actuator.isle.IsleEndpoint;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IsleEndpointAutoConfiguration}.
 *
 * @author huzijie
 * @version IsleEndpointAutoConfigurationTests.java, v 0.1 2023年10月10日 3:54 PM huzijie Exp $
 */
public class IsleEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(IsleEndpointAutoConfiguration.class,
                                                                     SofaModuleAutoConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=isle")
                .run((context) -> assertThat(context).hasSingleBean(IsleEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(IsleEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveIsleClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .run((context) -> assertThat(context).doesNotHaveBean(IsleEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldUseDisableProperty() {
        this.contextRunner.withPropertyValues("sofa.boot.isle.enabled=false")
                .run((context) -> assertThat(context).doesNotHaveBean(IsleEndpoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.isle.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(IsleEndpoint.class));
    }
}
