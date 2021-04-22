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
package com.alipay.sofa.startup.test;

import com.alipay.sofa.boot.startup.*;
import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;
import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.test.beans.ChildBean;
import com.alipay.sofa.startup.test.beans.ParentBean;
import com.alipay.sofa.startup.test.configuration.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.test.configuration.SofaStartupIsleAutoConfiguration;
import com.alipay.sofa.startup.test.spring.StartupApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.alipay.sofa.startup.test.beans.ChildBean.CHILD_INIT_TIME;
import static com.alipay.sofa.startup.test.beans.ParentBean.PARENT_INIT_TIM;

/**
 * @author huzijie
 * @version StartupReporterTest.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "com.alipay.sofa.boot.beanLoadCost=1")
@RunWith(SpringRunner.class)
@Import(value = { SofaStartupAutoConfiguration.class, SofaStartupIsleAutoConfiguration.class })
public class IsleCheckStartupReporterTest {
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        Assert.assertNotNull(startupReporter);
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
        Assert.assertNotNull(startupStaticsModel);
        Assert.assertEquals(7, startupStaticsModel.getStageStats().size());

        StageStat isleModelCreatingStage = startupReporter.getStageNyName(BootStageConstants.ISLE_MODEL_CREATING_STAGE);
        Assert.assertNotNull(isleModelCreatingStage);
        Assert.assertTrue(isleModelCreatingStage.getElapsedTime() > 0);

        StageStat isleSpringContextInstallStage = startupReporter.getStageNyName(BootStageConstants.ISLE_SPRING_CONTEXT_INSTALL_STAGE);
        Assert.assertNotNull(isleSpringContextInstallStage);
        Assert.assertTrue(isleSpringContextInstallStage.getElapsedTime() > 0);

        Assert.assertTrue(isleSpringContextInstallStage instanceof ContextRefreshStageStat);
        Assert.assertEquals(1, ((ContextRefreshStageStat) isleSpringContextInstallStage).getModuleStats().size());
        ModuleStat moduleStat = ((ContextRefreshStageStat) isleSpringContextInstallStage).getModuleStats().get(0);
        Assert.assertNotNull(moduleStat);
        Assert.assertEquals("testModule", moduleStat.getModuleName());
        Assert.assertTrue(moduleStat.getModuleEndTime() > moduleStat.getModuleStartTime());
        Assert.assertEquals(moduleStat.getElapsedTime(), moduleStat.getModuleEndTime() - moduleStat.getModuleStartTime());

        List<BeanStat> beanStats = moduleStat.getBeanStats();
        Assert.assertNotNull(beanStats);
        Assert.assertTrue(beanStats.size() >= 4);

        //test parent bean
        BeanStat parentBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("(parent)")).findFirst().orElse(null);
        Assert.assertNotNull(parentBeanStat);
        Assert.assertEquals(CHILD_INIT_TIME + PARENT_INIT_TIM,  parentBeanStat.getRefreshElapsedTime(), 20);
        Assert.assertEquals(PARENT_INIT_TIM, parentBeanStat.getRealRefreshElapsedTime(), 20);
        Assert.assertEquals(PARENT_INIT_TIM, parentBeanStat.getAfterPropertiesSetTime(), 20);
        Assert.assertEquals(1, parentBeanStat.getChildren().size());
        Assert.assertEquals(ParentBean.class.getName() + " (parent)", parentBeanStat.getBeanClassName());

        // test child bean
        BeanStat childBeanStat = parentBeanStat.getChildren().get(0);
        Assert.assertNotNull(childBeanStat);
        Assert.assertEquals(CHILD_INIT_TIME, childBeanStat.getRealRefreshElapsedTime(), 15);
        Assert.assertEquals(CHILD_INIT_TIME, childBeanStat.getInitTime(), 10);
        Assert.assertEquals(0, childBeanStat.getChildren().size());
        Assert.assertEquals(ChildBean.class.getName() + " (child)", childBeanStat.getBeanClassName());

        //test sofa service
        BeanStat serviceBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ServiceFactoryBean")).findFirst().orElse(null);
        Assert.assertNotNull(serviceBeanStat);
        Assert.assertTrue(serviceBeanStat.getRefreshElapsedTime() > 0);
        Assert.assertEquals(ServiceFactoryBean.class.getName() + " (sample)", serviceBeanStat.getBeanClassName());
        Assert.assertEquals( "com.alipay.sofa.startup.test.beans.facade.SampleService", serviceBeanStat.getInterfaceType());

        // test sofa reference
        BeanStat referenceBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ReferenceFactoryBean")).findFirst().orElse(null);
        Assert.assertNotNull(referenceBeanStat);
        Assert.assertTrue(referenceBeanStat.getRefreshElapsedTime() > 0);
        Assert.assertEquals(ReferenceFactoryBean.class.getName() + " (reference)", referenceBeanStat.getBeanClassName());
        Assert.assertEquals( "com.alipay.sofa.startup.test.beans.facade.TestService", referenceBeanStat.getInterfaceType());

        // test extension bean
        BeanStat extensionBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ExtensionFactoryBean")).findFirst().orElse(null);
        Assert.assertNotNull(extensionBeanStat);
        Assert.assertTrue(extensionBeanStat.getRefreshElapsedTime() > 0);
        Assert.assertEquals(ExtensionFactoryBean.class.getName(), extensionBeanStat.getBeanClassName());
        Assert.assertEquals("ExtensionPointTarget: extension", extensionBeanStat.getExtensionProperty());

        // test extension point bean
        BeanStat extensionPointBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ExtensionPointFactoryBean")).findFirst().orElse(null);
        Assert.assertNotNull(extensionPointBeanStat);
        Assert.assertTrue(extensionPointBeanStat.getRefreshElapsedTime() > 0);
        Assert.assertEquals(ExtensionPointFactoryBean.class.getName(), extensionPointBeanStat.getBeanClassName());
        Assert.assertEquals("ExtensionPointTarget: extension", extensionPointBeanStat.getExtensionProperty());
    }
}
