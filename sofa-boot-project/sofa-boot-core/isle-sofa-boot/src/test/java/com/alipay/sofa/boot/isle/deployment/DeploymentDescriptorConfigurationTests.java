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
package com.alipay.sofa.boot.isle.deployment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DeploymentDescriptorConfiguration}.
 *
 * @author huzijie
 * @version DeploymentDescriptorConfigurationTests.java, v 0.1 2023年04月06日 8:35 PM huzijie Exp $
 */
public class DeploymentDescriptorConfigurationTests {

    @Test
    public void getModuleNameIdentities() {
        List<String> expectedModuleNames = List.of("module1", "module2");
        List<String> requireModuleNames = List.of("module3", "module4");
        DeploymentDescriptorConfiguration config = new DeploymentDescriptorConfiguration(
            expectedModuleNames, requireModuleNames);
        assertThat(config.getModuleNameIdentities()).isEqualTo(expectedModuleNames);
    }

    @Test
    public void getRequireModuleIdentities() {
        List<String> moduleNames = List.of("module1", "module2");
        List<String> expectedRequireModuleNames = List.of("module3", "module4");
        DeploymentDescriptorConfiguration config = new DeploymentDescriptorConfiguration(
            moduleNames, expectedRequireModuleNames);
        assertThat(config.getRequireModuleIdentities()).isEqualTo(expectedRequireModuleNames);
    }
}
