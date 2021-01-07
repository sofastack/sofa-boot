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
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.StartupContextRefreshedListener;
import com.alipay.sofa.startup.test.beans.InitCostBean;
import com.alipay.sofa.startup.test.configuration.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.test.spring.StartupApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author huzijie
 * @version StartupReporterTest.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Import(SofaStartupAutoConfiguration.class)
public class SimpleStartupReporterTest {
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        Assert.assertNotNull(startupReporter);
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
        Assert.assertNotNull(startupStaticsModel);
        Assert.assertEquals(5, startupStaticsModel.getStageStats().size());

        Assert.assertTrue(startupStaticsModel.getApplicationBootTime() > 0);
        Assert.assertTrue(startupStaticsModel.getApplicationBootElapsedTime() > 0);
        Assert.assertEquals("StartupTest", startupStaticsModel.getAppName());

        StageStat jvmStartingStage = startupReporter.getStageNyName(BootStageConstants.JVM_STARTING_STAGE);
        Assert.assertNotNull(jvmStartingStage);
        Assert.assertTrue(jvmStartingStage.getElapsedTime() > 0);

        StageStat environmentPrepareStage = startupReporter.getStageNyName(BootStageConstants.ENVIRONMENT_PREPARE_STAGE);
        Assert.assertNotNull(environmentPrepareStage);
        Assert.assertTrue(environmentPrepareStage.getElapsedTime() > 0);
        Assert.assertEquals(jvmStartingStage.getStageEndTime(), environmentPrepareStage.getStageStartTime());

        StageStat applicationContextPrepareStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        Assert.assertNotNull(applicationContextPrepareStage);
        Assert.assertTrue(applicationContextPrepareStage.getElapsedTime() > 0);
        Assert.assertEquals(environmentPrepareStage.getStageEndTime(), applicationContextPrepareStage.getStageStartTime());

        StageStat applicationContextLoadStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_LOAD_STAGE);
        Assert.assertNotNull(applicationContextLoadStage);
        Assert.assertTrue(applicationContextLoadStage.getElapsedTime() > 0);
        Assert.assertEquals(applicationContextPrepareStage.getStageEndTime(), applicationContextLoadStage.getStageStartTime());

        StageStat applicationContextRefreshStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE);
        Assert.assertNotNull(applicationContextRefreshStage);
        Assert.assertTrue(applicationContextRefreshStage.getElapsedTime() > 0);
        Assert.assertEquals(applicationContextLoadStage.getStageEndTime(), applicationContextRefreshStage.getStageStartTime());

        Assert.assertTrue(applicationContextRefreshStage instanceof ContextRefreshStageStat);
        Assert.assertEquals(1, ((ContextRefreshStageStat) applicationContextRefreshStage).getModuleStats().size());
        ModuleStat moduleStat = ((ContextRefreshStageStat) applicationContextRefreshStage).getModuleStats().get(0);
        Assert.assertNotNull(moduleStat);
        Assert.assertEquals(StartupContextRefreshedListener.ROOT_MODULE_NAME, moduleStat.getModuleName());
        Assert.assertTrue(moduleStat.getModuleEndTime() > moduleStat.getModuleStartTime());
        Assert.assertEquals(moduleStat.getElapsedTime(), moduleStat.getModuleEndTime() - moduleStat.getModuleStartTime());

        List<BeanStat> beanStats =  moduleStat.getBeanStats();
        Assert.assertNotNull(beanStats);
        Assert.assertTrue(beanStats.size() >= 1);
        BeanStat initBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("InitCostBean")).findFirst().orElse(null);
        Assert.assertNotNull(initBeanStat);
        Assert.assertEquals(initBeanStat.getRefreshElapsedTime(), initBeanStat.getBeanRefreshEndTime() - initBeanStat.getBeanRefreshStartTime());
        Assert.assertEquals(InitCostBean.INIT_COST_TIME, initBeanStat.getRealRefreshElapsedTime(), 20);
        Assert.assertNull(initBeanStat.getBeanType());
        Assert.assertEquals(InitCostBean.class.getName() + " (initCostBean)", initBeanStat.getBeanClassName());
        Assert.assertTrue(initBeanStat.getChildren().isEmpty());
        Assert.assertEquals(0, initBeanStat.getAfterPropertiesSetTime());
        Assert.assertEquals(0, initBeanStat.getInitTime());
        Assert.assertNull(initBeanStat.getInterfaceType());
        Assert.assertNull(initBeanStat.getExtensionProperty());
    }
}
