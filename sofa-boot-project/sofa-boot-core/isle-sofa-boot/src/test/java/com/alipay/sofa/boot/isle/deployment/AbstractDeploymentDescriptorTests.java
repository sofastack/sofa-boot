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
import org.springframework.core.io.UrlResource;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AbstractDeploymentDescriptor}.
 *
 * @author huzijie
 * @version AbstractDeploymentDescriptorTests.java, v 0.1 2023年02月02日 8:02 PM huzijie Exp $
 */
public class AbstractDeploymentDescriptorTests {

    private final DeploymentDescriptorFactory       deploymentDescriptorFactory       = new DeploymentDescriptorFactory();

    private final DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.MODULE_NAME),
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.REQUIRE_MODULE));

    @Test
    public void whiteSpacePath() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> urls = classLoader
            .getResources("white space/" + DeploymentDescriptorConfiguration.SOFA_MODULE_FILE);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            UrlResource urlResource = new UrlResource(url);
            Properties props = new Properties();
            props.load(urlResource.getInputStream());
            DeploymentDescriptor dd = deploymentDescriptorFactory.build(url, props,
                deploymentDescriptorConfiguration, classLoader,
                DeploymentDescriptorConfiguration.SOFA_MODULE_FILE);
            assertThat(dd.isSpringPowered()).isTrue();
        }
    }
}
