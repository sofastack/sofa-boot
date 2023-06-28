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
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  * Tests for {@link FileDeploymentDescriptorTests}.
 *
 * @author huzijie
 * @version FileDeploymentDescriptorTests.java, v 0.1 2023年04月07日 10:26 AM huzijie Exp $
 */

public class FileDeploymentDescriptorTests {

    @Test
    void loadSpringXMLs() throws Exception {
        // Given
        String path = getClass().getClassLoader().getResource("test-module.properties").getPath();
        URL url = new URL("file://" + path);
        Properties properties = new Properties();
        DeploymentDescriptorConfiguration configuration = new DeploymentDescriptorConfiguration(
            null, null);
        ClassLoader classLoader = getClass().getClassLoader();

        // When
        DeploymentDescriptorFactory factory = new DeploymentDescriptorFactory();
        DeploymentDescriptor descriptor = factory.build(url, properties, configuration,
            classLoader, "test-module.properties");

        // Then
        String xmlPath = getClass().getClassLoader().getResource("META-INF/spring").getPath();
        URI expectedUri = new URI("file://" + xmlPath);
        File expectedFile = new File(expectedUri);
        List<File> expectedSpringFiles = new ArrayList<>();
        expectedSpringFiles.add(new File(expectedFile, "spring1.xml"));
        expectedSpringFiles.add(new File(expectedFile, "spring2.xml"));
        assertThat(descriptor.getSpringResources().size()).isEqualTo(2);
        for (File f : expectedSpringFiles) {
            assertThat(descriptor.getSpringResources().containsValue(new FileSystemResource(f)))
                .isTrue();
        }
    }

}
