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
package com.alipay.sofa.boot.actuator.beans;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version IsleBeanEndpointTest.java, v 0.1 2022年03月17日 5:42 PM huzijie Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class IsleBeanEndpointTest {

    @Test
    public void testBeans() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext();
        context.setId("bootstrap");
        context.refresh();
        ApplicationRuntimeModel model = new ApplicationRuntimeModel();
        model.addInstalled(new MockDeploymentDescriptor("A"));
        model.addInstalled(new MockDeploymentDescriptor("B"));
        context.getBeanFactory().registerSingleton(SofaBootConstants.APPLICATION, model);
        IsleBeansEndpoint isleBeansEndpoint = new IsleBeansEndpoint(context);
        BeansEndpoint.ApplicationBeans applicationBeans = isleBeansEndpoint.beans();
        Assert.assertNotNull(applicationBeans);
        Map<String, BeansEndpoint.ContextBeans> beansMap = applicationBeans.getContexts();
        Assert.assertNotNull(beansMap);
        Assert.assertEquals(3, beansMap.size());
        Assert.assertNotNull(beansMap.get("bootstrap"));

        BeansEndpoint.ContextBeans contextBeansA = beansMap.get("isle-module-A");
        Assert.assertNotNull(contextBeansA);
        Assert.assertEquals("isle-module-parentA", contextBeansA.getParentId());
        Assert.assertTrue(contextBeansA.getBeans().toString()
            .contains("com.alipay.sofa.boot.actuator.beans.IsleBeanEndpointTest$TestBean"));

        BeansEndpoint.ContextBeans contextBeansB = beansMap.get("isle-module-B");
        Assert.assertNotNull(contextBeansB);
        Assert.assertEquals("isle-module-parentB", contextBeansB.getParentId());
        Assert.assertTrue(contextBeansB.getBeans().toString()
            .contains("com.alipay.sofa.boot.actuator.beans.IsleBeanEndpointTest$TestBean"));
    }

    private static class TestBean {

    }

    private static class MockDeploymentDescriptor implements DeploymentDescriptor {
        private final String name;

        private MockDeploymentDescriptor(String name) {
            this.name = name;
        }

        @Override
        public String getModuleName() {
            return "isle-module-" + name;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public List<String> getRequiredModules() {
            return null;
        }

        @Override
        public String getProperty(String key) {
            return null;
        }

        @Override
        public String getSpringParent() {
            return "isle-module-parent" + name;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void setApplicationContext(ApplicationContext context) {

        }

        @Override
        public ApplicationContext getApplicationContext() {
            GenericApplicationContext applicationContext = new GenericApplicationContext();
            applicationContext.registerBean(TestBean.class);
            applicationContext.refresh();
            return applicationContext;
        }

        @Override
        public void addInstalledSpringXml(String fileName) {

        }

        @Override
        public List<String> getInstalledSpringXml() {
            return null;
        }

        @Override
        public boolean isSpringPowered() {
            return false;
        }

        @Override
        public void startDeploy() {

        }

        @Override
        public void deployFinish() {

        }

        @Override
        public Map<String, Resource> getSpringResources() {
            return null;
        }

        @Override
        public long getElapsedTime() {
            return 0;
        }

        @Override
        public long getStartTime() {
            return 0;
        }

        @Override
        public int compareTo(DeploymentDescriptor o) {
            return 0;
        }
    }
}