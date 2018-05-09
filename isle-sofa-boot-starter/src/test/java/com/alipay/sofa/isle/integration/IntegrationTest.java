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
import com.alipay.sofa.isle.util.ClassPathUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
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
public abstract class IntegrationTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @BeforeClass
    public static void before() throws Exception {
        // test JarDeploymentDescriptor, add jar file at runtime
        ClassPathUtil.addClassPathAtRuntime("service-0.1.0.jar");
        ClassPathUtil.addClassPathAtRuntime("require-module-0.1.0.jar");
    }

    @Test
    public void test() {
        ApplicationRuntimeModel applicationRuntimeModel = (ApplicationRuntimeModel) applicationContext
            .getBean(SofaIsleFrameworkConstants.APPLICATION);

        // contains three Deployments
        assertEquals(3, applicationRuntimeModel.getAllDeployments().size());
        assertEquals(3, applicationRuntimeModel.getInstalled().size());
        assertEquals(0, applicationRuntimeModel.getFailed().size());
        assertEquals(0, applicationRuntimeModel.getAllInactiveDeployments().size());

        // check module name
        assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.service",
            "com.alipay.sofa.require.module").contains(
            applicationRuntimeModel.getAllDeployments().get(0).getModuleName()));
        assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.service",
            "com.alipay.sofa.require.module").contains(
            applicationRuntimeModel.getAllDeployments().get(1).getModuleName()));
        assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.service",
            "com.alipay.sofa.require.module").contains(
            applicationRuntimeModel.getAllDeployments().get(2).getModuleName()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
