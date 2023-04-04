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

import com.alipay.sofa.boot.util.SofaBootEnvUtils;
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
 * Tests for {@link OnMasterBizCondition}.
 *
 * @author huzijie
 * @version OnMasterBizConditionTests.java, v 0.1 2023年04月04日 2:20 PM huzijie Exp $
 */
public class OnMasterBizConditionTests {

    private ConfigurableApplicationContext context;

    private final ConfigurableEnvironment  environment = new StandardEnvironment();

    @Test
    void checkArkEnvironment() {
        boolean isArkEnv = SofaBootEnvUtils.isArkEnv();
        load(OnSwitchConfiguration.class);
        assertThat(this.context.containsBean("sampleA")).isEqualTo(!isArkEnv);
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
        @ConditionalOnMasterBiz()
        public String sampleA() {
            return "sample";
        }
    }

}
