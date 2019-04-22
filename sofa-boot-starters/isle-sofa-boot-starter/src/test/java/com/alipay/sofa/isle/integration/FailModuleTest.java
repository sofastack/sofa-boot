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

import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.util.AddCustomJar;
import com.alipay.sofa.isle.util.SeparateClassLoaderTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;

/**
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SeparateClassLoaderTestRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
@AddCustomJar({ "dev-module-0.1.0.jar", "fail-module-0.1.0.jar" })
@DirtiesContext
public class FailModuleTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        ApplicationRuntimeModel applicationRuntimeModel = (ApplicationRuntimeModel) applicationContext
            .getBean(SofaModuleFrameworkConstants.APPLICATION);

        // contains three Deployments
        assertEquals(3, applicationRuntimeModel.getAllDeployments().size());
        assertEquals(2, applicationRuntimeModel.getInstalled().size());
        assertEquals(1, applicationRuntimeModel.getFailed().size());

        // check module not in installed list
        DeploymentDescriptor failModule = applicationRuntimeModel.getFailed().get(0);
        Assert.assertEquals("com.alipay.sofa.fail", failModule.getModuleName());
        Assert.assertFalse(applicationRuntimeModel.getInstalled().contains(failModule));
    }

    @Test
    public void testHealthChecker() {
        Assert.assertNotNull(applicationContext.getBean("sofaModuleHealthChecker"));
        HealthChecker healthChecker = (HealthChecker) applicationContext
            .getBean("sofaModuleHealthChecker");
        Assert.assertEquals(Status.DOWN, healthChecker.isHealthy().getStatus());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}