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
package com.alipay.sofa.isle.integration;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaIsleFrameworkConstants;
import com.alipay.sofa.isle.spring.config.SofaIsleProperties;
import com.alipay.sofa.isle.util.ClassPathUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author xuanbei 18/5/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
public class IntegrationTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private SofaIsleProperties sofaIsleProperties;

    @BeforeClass
    public static void before() throws Exception {
        // test JarDeploymentDescriptor, add jar file at runtime
        ClassPathUtil.addClassPathAtRuntime("dev-module-0.1.0.jar");
        ClassPathUtil.addClassPathAtRuntime("test-module-0.1.0.jar");

        // add argument in System properties
        System.setProperty("com.alipay.sofa.boot.moduleStartUpParallel", "true");
    }

    @Test
    public void test() {
        // test sofaIsleProperties
        assertEquals(sofaIsleProperties.getActiveProfiles(), "dev");
        assertEquals(sofaIsleProperties.isModuleStartUpParallel(), true);
        assertEquals(sofaIsleProperties.isAllowBeanDefinitionOverriding(), true);
        assertEquals(sofaIsleProperties.getBeanLoadCost(), 10);

        ApplicationRuntimeModel applicationRuntimeModel = (ApplicationRuntimeModel) applicationContext
            .getBean(SofaIsleFrameworkConstants.APPLICATION);

        // contains three Deployments
        assertEquals(2, applicationRuntimeModel.getAllDeployments().size());
        assertEquals(2, applicationRuntimeModel.getInstalled().size());
        assertEquals(0, applicationRuntimeModel.getFailed().size());
        assertEquals(1, applicationRuntimeModel.getAllInactiveDeployments().size());

        // check module name
        assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.dev").contains(
            applicationRuntimeModel.getInstalled().get(0).getModuleName()));
        assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.dev").contains(
            applicationRuntimeModel.getInstalled().get(1).getModuleName()));
        assertEquals("com.alipay.sofa.test", applicationRuntimeModel.getAllInactiveDeployments()
            .get(0).getModuleName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}