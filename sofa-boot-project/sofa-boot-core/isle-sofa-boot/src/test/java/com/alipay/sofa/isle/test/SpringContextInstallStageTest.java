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

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version SpringContextInstallStageTest.java, v 0.1 2021年12月20日 3:23 下午 huzijie Exp $
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringContextInstallStageTest {
    @Autowired
    private SpringContextInstallStage springContextInstallStage;
    @Autowired
    private SofaModuleProperties      moduleProperties;

    @Test
    public void testQuickFailure() {
        try {
            springContextInstallStage.process();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof DeploymentException);
            Assert.assertTrue(e.getMessage().contains("01-11007"));
            Assert.assertTrue(e.getMessage().contains("testFailModule"));
        }
        moduleProperties.setIgnoreModuleInstallFailure(true);
        try {
            springContextInstallStage.process();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Configuration
    static class SpringContextInstallConfiguration {

        @Bean("SOFABOOT-APPLICATION")
        public ApplicationRuntimeModel applicationRuntimeModel() {
            ApplicationRuntimeModel model = new ApplicationRuntimeModel();
            model.addFailed(new DeploymentDescriptor() {
                @Override
                public String getModuleName() {
                    return "testFailModule";
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
                    return null;
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
                    return null;
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
            });
            return model;
        }

        @Bean
        public SpringContextInstallStage springContextInstallStage(ApplicationContext applicationContext) {
            return new SpringContextInstallStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        public SofaModuleProperties sofaModuleProperties() {
            return new SofaModuleProperties();
        }

        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
            return new AutowiredAnnotationBeanPostProcessor();
        }
    }
}