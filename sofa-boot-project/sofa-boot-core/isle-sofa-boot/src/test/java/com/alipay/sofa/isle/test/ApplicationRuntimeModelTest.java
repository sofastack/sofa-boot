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
package com.alipay.sofa.isle.test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.impl.DefaultModuleDeploymentValidator;
import com.alipay.sofa.isle.deployment.impl.FileDeploymentDescriptor;
import com.alipay.sofa.isle.deployment.impl.JarDeploymentDescriptor;

/**
 * @author xuanbei 18/5/8
 */
public class ApplicationRuntimeModelTest {
    @Test
    public void test() throws Exception {
        // new ApplicationRuntimeModel Instance
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setAppName("testCase");
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());

        // new DeploymentDescriptorConfiguration instance
        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
            Collections.singletonList(SofaBootConstants.MODULE_NAME),
            Collections.singletonList(SofaBootConstants.REQUIRE_MODULE));

        // add first SOFAIsle module
        Properties props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.common");
        URL jarUrl = new URL("jar:file:/demo/path/demo.jar!/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(jarUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(dd instanceof JarDeploymentDescriptor);
        Assert.assertTrue(application.isModuleDeployment(dd));
        application.addDeployment(dd);

        // add second SOFAIsle module
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.REQUIRE_MODULE, "com.alipay.util");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(dd instanceof FileDeploymentDescriptor);
        Assert.assertTrue(application.isModuleDeployment(dd));
        application.addDeployment(dd);

        // test DeploymentDescriptor which misses Module-Name property
        props = new Properties();
        props.setProperty(SofaBootConstants.REQUIRE_MODULE, "com.alipay.util");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(dd instanceof FileDeploymentDescriptor);
        Assert.assertFalse(application.isModuleDeployment(dd));

        // missing com.alipay.util module
        Assert.assertEquals(2, application.getDeployRegistry().getPendingEntries().size());
        Assert.assertTrue(Arrays.asList("com.alipay.util", "com.alipay.dal").contains(
            application.getDeployRegistry().getPendingEntries().get(0).getKey()));
        Assert.assertTrue(Arrays.asList("com.alipay.util", "com.alipay.dal").contains(
            application.getDeployRegistry().getPendingEntries().get(1).getKey()));
        Assert.assertEquals(null, application.getDeployRegistry().getEntry("com.alipay.util")
            .getWaitsFor());
        Assert.assertEquals("com.alipay.util",
            application.getDeployRegistry().getEntry("com.alipay.dal").getWaitsFor().iterator()
                .next().getKey());

        // add missing module
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.util");
        jarUrl = new URL("jar:file:/demo/path/demo.jar!/isle-module.config");
        dd = DeploymentBuilder.build(jarUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(dd instanceof JarDeploymentDescriptor);
        Assert.assertTrue(application.isModuleDeployment(dd));
        application.addDeployment(dd);
        Assert.assertEquals(0, application.getDeployRegistry().getPendingEntries().size());
    }
}
