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
package com.alipay.sofa.actuator.autoconfigure.test;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version IsleBeanEndpointAutoConfiguration.java, v 0.1 2022年03月17日 11:43 AM huzijie Exp $
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = IsleBeanEndpointAutoConfiguration.MockConfiguration.class)
@RunWith(SpringRunner.class)
public class IsleBeanEndpointAutoConfiguration {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate
            .getForEntity("/actuator/beans", String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode contextNode = rootNode.get("contexts");
        Assert.assertNotNull(contextNode);
        Assert.assertEquals(4, contextNode.size());
        Assert.assertNotNull(contextNode.get("bootstrap"));

        JsonNode moduleNodeA = contextNode.get("isle-module-A");
        Assert.assertNotNull(moduleNodeA);
        Assert.assertEquals("isle-module-parentA", moduleNodeA.get("parentId").textValue());
        Assert
            .assertTrue(moduleNodeA
                .get("beans")
                .toString()
                .contains(
                    "com.alipay.sofa.actuator.autoconfigure.test.IsleBeanEndpointAutoConfiguration$TestBean"));

        JsonNode moduleNodeB = contextNode.get("isle-module-B");
        Assert.assertNotNull(moduleNodeB);
        Assert.assertEquals("isle-module-parentB", moduleNodeB.get("parentId").textValue());
        Assert
            .assertTrue(moduleNodeB
                .get("beans")
                .toString()
                .contains(
                    "com.alipay.sofa.actuator.autoconfigure.test.IsleBeanEndpointAutoConfiguration$TestBean"));
    }

    @EnableAutoConfiguration
    @Configuration(proxyBeanMethods = false)
    public static class MockConfiguration {

        @Bean
        public ApplicationRuntimeModel applicationRuntimeModel() {
            ApplicationRuntimeModel applicationRuntimeModel = new ApplicationRuntimeModel();
            applicationRuntimeModel.addInstalled(new MockDeploymentDescriptor("A"));
            applicationRuntimeModel.addInstalled(new MockDeploymentDescriptor("B"));
            return applicationRuntimeModel;
        }
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
