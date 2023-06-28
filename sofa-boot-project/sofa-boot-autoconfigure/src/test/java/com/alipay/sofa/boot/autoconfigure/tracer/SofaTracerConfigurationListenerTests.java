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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.boot.listener.SpringCloudConfigListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SofaTracerConfigurationListener}.
 *
 * @author huzijie
 * @version SofaTracerConfigurationListenerTests.java, v 0.1 2023年01月11日 3:28 PM huzijie Exp $
 */
public class SofaTracerConfigurationListenerTests {

    private ConfigurableApplicationContext context;

    @BeforeEach
    public void resetSofaTracerConfiguration() {
        Field field = ReflectionUtils.findField(SofaTracerConfiguration.class, "properties");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, null, new ConcurrentHashMap<String, Object>());
    }

    @AfterEach
    public void cleanUp() throws IllegalAccessException {
        resetSofaTracerConfiguration();
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void noSpringApplicationName() {
        SpringApplication application = new SpringApplication(Config.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        Map<String, Object> props = new HashMap<>();
        application.setDefaultProperties(props);
        application.addListeners(new SofaTracerConfigurationListener());
        assertThatThrownBy(application::run)
                .hasMessageContaining("spring.application.name must be configured!");
    }

    @Test
    public void injectSofaTracerConfiguration() {
        SpringApplication application = new SpringApplication(Config.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        Map<String, Object> props = new HashMap<>();
        props.put("spring.application.name", "tracer-test");
        props.put("sofa.boot.tracer.disableDigestLog", "true");
        props.put("sofa.boot.tracer.disableConfiguration[a]", "true");
        props.put("sofa.boot.tracer.tracerGlobalRollingPolicy", "'.'yyyy-MM-dd_HH");
        props.put("sofa.boot.tracer.tracerGlobalLogReserveDay", "8");
        props.put("sofa.boot.tracer.statLogInterval", "120");
        props.put("sofa.boot.tracer.baggageMaxLength", "2048");
        props.put("sofa.boot.tracer.samplerName", "test");
        props.put("sofa.boot.tracer.samplerPercentage", "200.0");
        props.put("sofa.boot.tracer.samplerCustomRuleClassName", "TestRuleClass");
        props.put("sofa.boot.tracer.jsonOutput", "false");
        application.setDefaultProperties(props);
        application.addListeners(new SpringCloudConfigListener());
        application.addListeners(new SofaTracerConfigurationListener());
        this.context = application.run();
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.DISABLE_MIDDLEWARE_DIGEST_LOG_KEY)).isEqualTo(
            "true");
        assertThat(
            SofaTracerConfiguration.getMapEmptyIfNull(
                SofaTracerConfiguration.DISABLE_DIGEST_LOG_KEY).get("a")).isEqualTo("true");
        assertThat(
            SofaTracerConfiguration.getProperty(SofaTracerConfiguration.TRACER_GLOBAL_ROLLING_KEY))
            .isEqualTo("'.'yyyy-MM-dd_HH");
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.TRACER_GLOBAL_LOG_RESERVE_DAY)).isEqualTo("8");
        assertThat(SofaTracerConfiguration.getProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL))
            .isEqualTo("120");
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.TRACER_PENETRATE_ATTRIBUTE_MAX_LENGTH))
            .isEqualTo("2048");
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.TRACER_SYSTEM_PENETRATE_ATTRIBUTE_MAX_LENGTH))
            .isEqualTo("2048");
        assertThat(
            SofaTracerConfiguration.getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_NAME_KEY))
            .isEqualTo("test");
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_PERCENTAGE_KEY)).isEqualTo(
            "200.0");
        assertThat(
            SofaTracerConfiguration
                .getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_CUSTOM_RULE_CLASS_NAME))
            .isEqualTo("TestRuleClass");
        assertThat(SofaTracerConfiguration.getProperty(SofaTracerConfiguration.JSON_FORMAT_OUTPUT))
            .isEqualTo("false");
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {

    }
}
