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

import com.alipay.sofa.boot.isle.deployment.DefaultModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.FileDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.JarDeploymentDescriptor;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
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

    @Test
    public void addSofaModule() throws Exception {
        // new ApplicationRuntimeModel Instance
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());

        // add first SOFAIsle module
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.common");
        URL jarUrl = new URL("jar:file:/whatever/path/demo.jar!/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(jarUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader());
        assertThat(dd instanceof JarDeploymentDescriptor).isTrue();
        assertThat(application.isModuleDeployment(dd)).isTrue();
        application.addDeployment(dd);

        // add second SOFAIsle module
        props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.REQUIRE_MODULE, "com.alipay.util");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTests.class.getClassLoader());
        assertThat(dd instanceof FileDeploymentDescriptor).isTrue();
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
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());

        //DeploymentDescriptor which misses Module-Name property
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.REQUIRE_MODULE, "com.alipay.util");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(fileUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader());
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
        URL jarUrl = new URL("jar:file:/demo/path/demo.jar!/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(jarUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTests.class.getClassLoader());
        assertThat(dd).isInstanceOf(JarDeploymentDescriptor.class);
        assertThat(application.isModuleDeployment(dd)).isTrue();
        application.addDeployment(dd);
        assertThat(0).isEqualTo(application.getDeployRegistry().getPendingEntries().size());
    }
}
