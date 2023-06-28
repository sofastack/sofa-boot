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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AbstractDeploymentDescriptor}.
 *
 * @author huzijie
 * @version AbstractDeploymentDescriptorTests.java, v 0.1 2023年02月02日 8:02 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class AbstractDeploymentDescriptorTests {

    private final DeploymentDescriptorFactory       deploymentDescriptorFactory       = new DeploymentDescriptorFactory();

    private final DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.MODULE_NAME),
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.REQUIRE_MODULE));

    @Mock
    private Resource                                resource1;

    @Mock
    private Resource                                resource2;

    private final URL                               url                               = new URL(
                                                                                          "jar:file:/path/to/sofa-module.jar!/META-INF/sofa/sofa-module.properties");

    private Properties                              properties;

    private final ClassLoader                       classLoader                       = ClassLoader
                                                                                          .getSystemClassLoader();

    private AbstractDeploymentDescriptor            descriptor;

    public AbstractDeploymentDescriptorTests() throws MalformedURLException {
    }

    @BeforeEach
    public void setUp() throws MalformedURLException {
        List<String> requiredModules = new ArrayList<>();
        requiredModules.add("module1");
        requiredModules.add("module2");
        properties = new Properties();
        properties.setProperty("Spring-Parent", "parent-module");
        properties.setProperty("Module-Name", "sample-module");
        properties.setProperty("Require-Module", "module1,module2");
        descriptor = new AbstractDeploymentDescriptor(url, properties,
            deploymentDescriptorConfiguration, classLoader) {

            @Override
            protected void loadSpringXMLs() {

            }
        };
    }

    @Test
    public void getModuleName() {
        assertThat(descriptor.getModuleName()).isEqualTo("sample-module");
    }

    @Test
    public void getName() {
        assertThat(descriptor.getName()).isEqualTo("sofa-module.jar");
    }

    @Test
    public void getRequiredModules() {
        assertThat(descriptor.getRequiredModules()).containsExactly("module1", "module2",
            "parent-module");
    }

    @Test
    public void getSpringParent() {
        assertThat(descriptor.getSpringParent()).isEqualTo("parent-module");
    }

    @Test
    public void getProperty() {
        assertThat(descriptor.getProperty("Spring-Parent")).isEqualTo("parent-module");
    }

    @Test
    public void getClassLoader() {
        assertThat(descriptor.getClassLoader()).isEqualTo(classLoader);
    }

    @Test
    public void getApplicationContext() {
        assertThat(descriptor.getApplicationContext()).isNull();
    }

    @Test
    public void getSpringResources() {
        descriptor.getSpringResources().put("resource1", resource1);
        descriptor.getSpringResources().put("resource2", resource2);
        Map<String, Resource> springResources = descriptor.getSpringResources();
        assertThat(springResources).containsOnlyKeys("resource1", "resource2");
    }

    @Test
    public void isSpringPoweredReturnsTrueWhenSpringResourcesNotEmpty() {
        descriptor.getSpringResources().put("resource1", resource1);
        assertThat(descriptor.isSpringPowered()).isTrue();
    }

    @Test
    public void isSpringPoweredReturnsFalseWhenSpringResourcesEmpty() {
        assertThat(descriptor.isSpringPowered()).isFalse();
    }

    @Test
    public void addInstalledSpringXml() {
        List<String> installedSpringXml = descriptor.getInstalledSpringXml();
        assertThat(installedSpringXml).isEmpty();
        descriptor.addInstalledSpringXml("test.xml");
        installedSpringXml = descriptor.getInstalledSpringXml();
        assertThat(installedSpringXml).containsExactly("test.xml");
    }

    @Test
    public void startDeploy() {
        descriptor.startDeploy();
        assertThat(descriptor.getStartTime()).isNotEqualTo(0);
    }

    @Test
    public void deployFinish() throws InterruptedException {
        descriptor.startDeploy();
        Thread.sleep(1);
        descriptor.deployFinish();
        assertThat(descriptor.getElapsedTime()).isNotEqualTo(0);
    }

    @Test
    public void setApplicationContext() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        descriptor.setApplicationContext(applicationContext);
        assertThat(descriptor.getApplicationContext()).isEqualTo(applicationContext);
    }

    @Test
    public void compare() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getName()).thenReturn(descriptor.getName());
        assertThat(descriptor.compareTo(deploymentDescriptor)).isEqualTo(0);
    }

    @Test
    public void equals() {
        AbstractDeploymentDescriptor deploymentDescriptor = mock(AbstractDeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn(descriptor.getModuleName());
        assertThat(descriptor.equals(descriptor)).isTrue();
        assertThat(descriptor.equals(new Object())).isFalse();
        assertThat(descriptor.equals(deploymentDescriptor)).isTrue();
    }

    @Test
    public void checkHashCode() {
        assertThat(descriptor.hashCode()).isEqualTo(Objects.hash(descriptor.getModuleName()));
    }

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
