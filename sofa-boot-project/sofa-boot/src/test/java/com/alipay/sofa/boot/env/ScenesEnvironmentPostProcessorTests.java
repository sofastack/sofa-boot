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

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yuanxuan
 * @version : ScenesEnvironmentPostProcessorTests.java, v 0.1 2023年03月09日 12:57 yuanxuan Exp $
 */
public class ScenesEnvironmentPostProcessorTests {

    private final ScenesEnvironmentPostProcessor scenesEnvironmentPostProcessor = new ScenesEnvironmentPostProcessor();

    @Test
    public void addScenesConfigs() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("sofa.boot.scenes", "function");
        scenesEnvironmentPostProcessor.postProcessEnvironment(environment, new SpringApplication());
        assertThat(
            environment.getPropertySources().contains(
                "classpath:/sofa-boot/scenes" + File.separator + "function.properties")).isTrue();
        assertThat(
            environment.getPropertySources().contains(
                "classpath:/sofa-boot/scenes" + File.separator + "function.yml")).isTrue();
    }
}
