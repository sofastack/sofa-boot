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
import java.util.Collections;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;

/**
 * @author xuanbei 18/5/8
 * @author qilong.zql
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "/config/application-profile-test.properties")
public class SofaModuleProfileCheckerTest {

    @Autowired
    private SofaModuleProfileChecker sofaModuleProfileChecker;

    @Test
    public void test() throws Exception {
        // new DeploymentDescriptorConfiguration instance
        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
            Collections.singletonList(SofaBootConstants.MODULE_NAME),
            Collections.singletonList(SofaBootConstants.REQUIRE_MODULE));

        // test dev profile
        Properties props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.MODULE_PROFILE, "dev");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(fileUrl, props,
            deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(sofaModuleProfileChecker.acceptModule(dd));

        // test product profile
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.MODULE_PROFILE, "product");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(sofaModuleProfileChecker.acceptModule(dd));

        // test !dev profile
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.MODULE_PROFILE, "!dev");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertFalse(sofaModuleProfileChecker.acceptModule(dd));

        // test test,grey profile
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.MODULE_PROFILE, "dev,grey");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(sofaModuleProfileChecker.acceptModule(dd));

        // test test,grey profile
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        props.setProperty(SofaBootConstants.MODULE_PROFILE, "test,grey");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertFalse(sofaModuleProfileChecker.acceptModule(dd));

        // test no profile, default pass
        props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.dal");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration,
            ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(sofaModuleProfileChecker.acceptModule(dd));
    }

    @Configuration
    @EnableConfigurationProperties(SofaModuleProperties.class)
    static class SofaModuleProfileCheckerTestConfiguration {
        @Bean
        public SofaModuleProfileChecker sofaModuleProfileChecker() {
            return new DefaultSofaModuleProfileChecker();
        }
    }
}
