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
package com.alipay.sofa.boot.initializer;

import com.alipay.sofa.boot.Initializer.SwitchableApplicationContextInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Profiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SwitchableApplicationContextInitializer}.
 *
 * @author yuanxuan
 * @version : SampleSwitchSpringContextInitializerTests.java, v 0.1 2023年02月22日 11:30 yuanxuan Exp $
 */
public class SampleSwitchSpringContextInitializerTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withInitializer(new SampleSwitchSpringContextInitializer());

    @Test
    void enableFalse() {
        contextRunner.withPropertyValues("sofa.boot.switch.initializer.sampleswitchabletest.enabled=false").run(context -> assertThat(context.getEnvironment().acceptsProfiles(Profiles.of("sampleswitchtest"))).isFalse());
    }

    @Test
    void enableTrue() {
        contextRunner.withPropertyValues("sofa.boot.switch.initializer.sampleswitchabletest.enabled=true").run(context -> assertThat(context.getEnvironment().acceptsProfiles(Profiles.of("sampleswitchtest"))).isTrue());
    }

    @Test
    void enableDefault() {
        contextRunner.run(context -> assertThat(context.getEnvironment().acceptsProfiles(Profiles.of("sampleswitchtest"))).isTrue());
    }

    static class SampleSwitchSpringContextInitializer extends
                                                     SwitchableApplicationContextInitializer {

        @Override
        protected void doInitialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getEnvironment().addActiveProfile("sampleswitchtest");
        }

        @Override
        protected String switchKey() {
            return "sampleswitchabletest";
        }

    }

}
