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
package com.alipay.sofa.boot.actuator.autoconfigure.components;

import com.alipay.sofa.boot.actuator.autoconfigure.beans.IsleBeansEndpointAutoConfiguration;
import com.alipay.sofa.boot.actuator.components.ComponentsEndpoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IsleBeansEndpointAutoConfiguration}.
 *
 * @author huzijie
 * @version ComponentsEndpointAutoConfigurationTests.java, v 0.1 2022年12月29日 5:56 PM huzijie Exp $
 */
public class ComponentsEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(ComponentsEndpointAutoConfiguration.class,
                                                                     SofaRuntimeAutoConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=components")
                .run((context) -> assertThat(context).hasSingleBean(ComponentsEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveIsleClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SofaRuntimeContext.class))
                .run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndpoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.components.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndpoint.class));
    }
}
