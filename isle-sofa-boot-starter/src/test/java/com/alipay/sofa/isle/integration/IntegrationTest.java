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

import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.scan.SampleService;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.util.ClassPathUtil;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
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
    private ApplicationContext   applicationContext;

    @Autowired
    private SofaModuleProperties sofaModuleProperties;

    @SofaReference(uniqueId = "componentScanTest")
    private SampleService        sampleService;

    @BeforeClass
    public static void before() throws Exception {
        // test JarDeploymentDescriptor, add jar file at runtime
        ClassPathUtil.addClassPathAtRuntime("dev-module-0.1.0.jar");
        ClassPathUtil.addClassPathAtRuntime("test-module-0.1.0.jar");

        // add argument in System properties
        System.setProperty("com.alipay.sofa.boot.module-start-up-parallel", "true");
    }

    @Test
    public void test() {
        // test sofaModuleProperties
        assertEquals(sofaModuleProperties.getActiveProfiles(), "dev");
        assertEquals(sofaModuleProperties.isModuleStartUpParallel(), true);
        assertEquals(sofaModuleProperties.isPublishEventToParent(), false);
        assertEquals(sofaModuleProperties.isAllowBeanDefinitionOverriding(), false);
        assertEquals(sofaModuleProperties.getBeanLoadCost(), 0);

        ApplicationRuntimeModel applicationRuntimeModel = (ApplicationRuntimeModel) applicationContext
            .getBean(SofaModuleFrameworkConstants.APPLICATION);

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

    @Test
    public void testHealthChecker() {
        Assert.assertNotNull(applicationContext.getBean("sofaModuleHealthChecker"));

        HealthChecker healthChecker = (HealthChecker) applicationContext
            .getBean("sofaModuleHealthChecker");
        Assert.assertTrue(healthChecker.isHealthy().getStatus().equals(Status.UP));
        Assert.assertEquals("SOFABoot-Modules", healthChecker.getComponentName());
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT,
            healthChecker.getRetryCount());
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL,
            healthChecker.getRetryTimeInterval());
        Assert.assertEquals(true, healthChecker.isStrictCheck());
    }

    @Test
    public void testComponentScan() {
        Assert.assertNotNull(sampleService);
        "Hello from com.alipay.sofa.isle.scan.SampleServiceImpl.".equals(sampleService.message());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}