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
package com.alipay.sofa.boot.env;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SofaBootEnvironmentPostProcessor}.
 *
 * @author huzijie
 * @version SofaBootEnvironmentPostProcessorTests.java, v 0.1 2023年02月01日 11:30 AM huzijie Exp $
 */
public class SofaBootEnvironmentPostProcessorTests {

    private final SofaBootEnvironmentPostProcessor sofaBootEnvironmentPostProcessor = new SofaBootEnvironmentPostProcessor();

    @Test
    public void registerFrameworkVersion() {
        MockEnvironment environment = new MockEnvironment();
        sofaBootEnvironmentPostProcessor.postProcessEnvironment(environment, null);
        assertThat(
            environment.getPropertySources().get(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE))
            .isNotNull();
        assertThat(environment.containsProperty(SofaBootConstants.SOFA_BOOT_VERSION)).isTrue();
        assertThat(environment.containsProperty(SofaBootConstants.SOFA_BOOT_FORMATTED_VERSION))
            .isTrue();
    }

    @Test
    public void registerExposureEndpoint() {
        MockEnvironment environment = new MockEnvironment();
        sofaBootEnvironmentPostProcessor.postProcessEnvironment(environment, null);
        assertThat(environment.getProperty(SofaBootConstants.ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG))
            .isEqualTo(SofaBootConstants.SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE);
    }

    @Test
    public void registerRequiredProperty() {
        MockEnvironment environment = new MockEnvironment();
        environment.validateRequiredProperties();
        sofaBootEnvironmentPostProcessor.postProcessEnvironment(environment, null);
        assertThatThrownBy(environment::validateRequiredProperties)
                .hasMessageContaining(SofaBootConstants.APP_NAME_KEY)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void registerDuplicated() {
        MockEnvironment environment = new MockEnvironment();
        environment.getPropertySources().addLast(
            new PropertiesPropertySource(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE,
                new Properties()));
        sofaBootEnvironmentPostProcessor.postProcessEnvironment(environment, null);
        assertThat(environment.containsProperty(SofaBootConstants.SOFA_BOOT_VERSION)).isFalse();
        assertThat(environment.containsProperty(SofaBootConstants.SOFA_BOOT_FORMATTED_VERSION))
            .isFalse();
    }
}
