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
package com.alipay.sofa.boot.isle;

import com.alipay.sofa.boot.isle.deployment.AbstractDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DefaultModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorFactory;
import com.alipay.sofa.boot.isle.deployment.FileDeploymentDescriptor;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationRuntimeModel}.
 * 
 * @author xuanbei 18/5/8
 * @author huzijie
 */
public class ApplicationRuntimeModelTests {

    private final DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.MODULE_NAME),
                                                                                          Collections
                                                                                              .singletonList(DeploymentDescriptorConfiguration.REQUIRE_MODULE));

    private final DeploymentDescriptorFactory       deploymentDescriptorFactory       = new DeploymentDescriptorFactory();

    private final String                            testModuleConfigFile              = "test-module.config";

    @Test
    public void addSofaModule() throws Exception {
        // new ApplicationRuntimeModel Instance
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        DefaultSofaModuleProfileChecker checker = new DefaultSofaModuleProfileChecker();
        application.setSofaModuleProfileChecker(checker);
        assertThat(application.getSofaModuleProfileChecker()).isEqualTo(checker);

        // add first SOFAIsle module
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.common");
        URL jarUrl = new URL("file:/" + testModuleConfigFile);
        DeploymentDescriptor dd = deploymentDescriptorFactory.build(jarUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader(),
            testModuleConfigFile);
        assertThat(dd instanceof FileDeploymentDescriptor).isTrue();
        addSpringXml(dd);
        assertThat(application.isModuleDeployment(dd)).isTrue();
        application.addDeployment(dd);

        // add second SOFAIsle module
        props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.REQUIRE_MODULE, "com.alipay.util");
        URL fileUrl = new URL("file:/" + testModuleConfigFile);
        dd = deploymentDescriptorFactory.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTests.class.getClassLoader(), testModuleConfigFile);
        assertThat(dd instanceof FileDeploymentDescriptor).isTrue();
        addSpringXml(dd);
        assertThat(application.isModuleDeployment(dd)).isTrue();
        application.addDeployment(dd);

        // missing com.alipay.util module
        assertThat(2).isEqualTo(application.getDeployRegistry().getPendingEntries().size());
        assertThat(Arrays.asList("com.alipay.util", "com.alipay.dal")).contains(
            application.getDeployRegistry().getPendingEntries().get(0).getKey());
        assertThat(Arrays.asList("com.alipay.util", "com.alipay.dal")).contains(
            application.getDeployRegistry().getPendingEntries().get(1).getKey());
        assertThat(application.getDeployRegistry().getEntry("com.alipay.util").getWaitsFor())
            .isNull();
        assertThat("com.alipay.util").isEqualTo(
            application.getDeployRegistry().getEntry("com.alipay.dal").getWaitsFor().iterator()
                .next().getKey());
    }

    @Test
    public void addMissModuleNameModule() throws Exception {
        // new ApplicationRuntimeModel Instance
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        application.setModuleDeploymentValidator(validator);
        assertThat(application.getModuleDeploymentValidator()).isEqualTo(validator);

        //DeploymentDescriptor which misses Module-Name property
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.REQUIRE_MODULE, "com.alipay.util");
        URL fileUrl = new URL("file:/" + testModuleConfigFile);
        DeploymentDescriptor dd = deploymentDescriptorFactory.build(fileUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader(),
            testModuleConfigFile);
        assertThat(dd).isInstanceOf(FileDeploymentDescriptor.class);
        assertThat(application.isModuleDeployment(dd)).isFalse();
    }

    @Test
    public void addMissModule() throws Exception {
        // new ApplicationRuntimeModel Instance
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());

        // add missing module
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.util");
        URL jarUrl = new URL("file:/" + testModuleConfigFile);
        DeploymentDescriptor dd = deploymentDescriptorFactory.build(jarUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader(),
            testModuleConfigFile);
        assertThat(dd).isInstanceOf(FileDeploymentDescriptor.class);
        addSpringXml(dd);
        assertThat(application.isModuleDeployment(dd)).isTrue();
        application.addDeployment(dd);
        assertThat(0).isEqualTo(application.getDeployRegistry().getPendingEntries().size());
    }

    private void addSpringXml(DeploymentDescriptor deploymentDescriptor) {
        Field field = ReflectionUtils.findField(AbstractDeploymentDescriptor.class,
            "springResources");
        field.setAccessible(true);
        Map<String, Resource> map = new HashMap<>();
        map.put("test", new ByteArrayResource(new byte[] {}));
        ReflectionUtils.setField(field, deploymentDescriptor, map);
    }
}
