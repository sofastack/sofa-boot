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
package com.alipay.sofa.boot.autoconfigure.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OnVirtualThreadStartupDisableCondition} and {@link OnVirtualThreadStartupAvailableCondition}.
 *
 * @author huzijie
 * @version OnVirtualThreadStartupConditionTests.java, v 0.1 2023年12月05日 5:04 PM huzijie Exp $
 */
public class OnVirtualThreadStartupConditionTests {

    private ConfigurableApplicationContext context;

    private final ConfigurableEnvironment  environment = new StandardEnvironment();

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void checkJdk17Environment() {
        load(OnVirtualThreadStartupConditionTests.OnVirtualThreadStartupConfiguration.class);
        assertThat(this.context.containsBean("sampleA")).isTrue();
        assertThat(this.context.containsBean("sampleB")).isFalse();
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void checkJdk17EnvironmentEnableProperty() {
        load(OnVirtualThreadStartupConditionTests.OnVirtualThreadStartupConfiguration.class,
            "sofa.boot.startup.threads.virtual.enabled=true");
        assertThat(this.context.containsBean("sampleA")).isTrue();
        assertThat(this.context.containsBean("sampleB")).isFalse();
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void checkJdk21Environment() {
        load(OnVirtualThreadStartupConditionTests.OnVirtualThreadStartupConfiguration.class);
        assertThat(this.context.containsBean("sampleA")).isTrue();
        assertThat(this.context.containsBean("sampleB")).isFalse();
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void checkJdk21EnvironmentEnableProperty() {
        load(OnVirtualThreadStartupConditionTests.OnVirtualThreadStartupConfiguration.class,
            "sofa.boot.startup.threads.virtual.enabled=true");
        assertThat(this.context.containsBean("sampleA")).isFalse();
        assertThat(this.context.containsBean("sampleB")).isTrue();
    }

    private void load(Class<?> config, String... environment) {
        TestPropertyValues.of(environment).and("spring.application.name=test")
            .applyTo(this.environment);
        this.context = new SpringApplicationBuilder(config).environment(this.environment)
            .web(WebApplicationType.NONE).run();
    }

    @Configuration
    static class OnVirtualThreadStartupConfiguration {

        @Bean
        @Conditional(OnVirtualThreadStartupDisableCondition.class)
        public String sampleA() {
            return "sample";
        }

        @Bean
        @Conditional(OnVirtualThreadStartupAvailableCondition.class)
        public String sampleB() {
            return "sample";
        }
    }
}
