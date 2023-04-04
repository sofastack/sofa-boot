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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OnSwitchCondition}.
 *
 * @author huzijie
 * @version OnSwitchConditionTests.java, v 0.1 2023年04月04日 2:20 PM huzijie Exp $
 */
public class OnSwitchConditionTests {

    private ConfigurableApplicationContext context;

    private final ConfigurableEnvironment  environment = new StandardEnvironment();

    @AfterEach
    void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    void switchPropertyAreDefined() {
        load(OnSwitchConfiguration.class, "sofa.boot.switch.bean.sampleA.enabled=false");
        assertThat(this.context.containsBean("sampleA")).isFalse();
        assertThat(this.context.containsBean("sampleB")).isTrue();
    }

    @Test
    void switchPropertyMissAreDefined() {
        load(OnSwitchMissIfNotConfiguration.class, "sofa.boot.switch.bean.sampleA.enabled=true");
        assertThat(this.context.containsBean("sampleA")).isTrue();
        assertThat(this.context.containsBean("sampleB")).isFalse();
    }

    private void load(Class<?> config, String... environment) {
        TestPropertyValues.of(environment).and("spring.application.name=test")
            .applyTo(this.environment);
        this.context = new SpringApplicationBuilder(config).environment(this.environment)
            .web(WebApplicationType.NONE).run();
    }

    @Configuration
    static class OnSwitchConfiguration {

        @Bean
        @ConditionalOnSwitch("sampleA")
        public String sampleA() {
            return "sample";
        }

        @Bean
        @ConditionalOnSwitch("sampleB")
        public String sampleB() {
            return "sample";
        }
    }

    @Configuration
    static class OnSwitchMissIfNotConfiguration {

        @Bean
        @ConditionalOnSwitch(value = "sampleA", matchIfMissing = false)
        public String sampleA() {
            return "sample";
        }

        @Bean
        @ConditionalOnSwitch(value = "sampleB", matchIfMissing = false)
        public String sampleB() {
            return "sample";
        }
    }
}
