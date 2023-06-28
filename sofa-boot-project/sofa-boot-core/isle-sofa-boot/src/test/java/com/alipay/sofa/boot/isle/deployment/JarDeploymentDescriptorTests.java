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

import java.net.URL;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JarDeploymentDescriptorTests}.
 *
 * @author huzijie
 * @version JarDeploymentDescriptorTests.java, v 0.1 2023年04月07日 10:26 AM huzijie Exp $
 */

public class JarDeploymentDescriptorTests {

    @Test
    void loadSpringXMLs() throws Exception {
        // Given
        String path = getClass().getClassLoader().getResource("module.jar").getPath();
        URL url = new URL("jar:file://" + path + "!/");
        Properties properties = new Properties();
        DeploymentDescriptorConfiguration configuration = new DeploymentDescriptorConfiguration(
            null, null);
        ClassLoader classLoader = getClass().getClassLoader();

        // When
        DeploymentDescriptorFactory factory = new DeploymentDescriptorFactory();
        DeploymentDescriptor descriptor = factory.build(url, properties, configuration,
            classLoader, "test-module.properties");

        // Then
        assertThat(descriptor.getSpringResources().size()).isEqualTo(1);
    }

}
