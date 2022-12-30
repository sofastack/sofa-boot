///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alipay.sofa.boot.actuator.startup;
//
//import com.alipay.sofa.boot.startup.BaseStat;
//import com.alipay.sofa.boot.startup.BeanStat;
//import com.alipay.sofa.boot.startup.BootStageConstants;
//import com.alipay.sofa.boot.startup.ChildrenStat;
//import com.alipay.sofa.boot.startup.ModuleStat;
//import com.alipay.sofa.startup.StartupReporter;
//import com.alipay.sofa.startup.stage.StartupContextRefreshedListener;
//import com.alipay.sofa.boot.actuator.startup.beans.InitCostBean;
//import com.alipay.sofa.boot.actuator.startup.configuration.SofaStartupAutoConfiguration;
//import com.alipay.sofa.boot.actuator.startup.spring.StartupApplication;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
///**
// * @author huzijie
// * @version StartupReporterTest.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
// */
//@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@RunWith(SpringRunner.class)
//@Import(SofaStartupAutoConfiguration.class)
////todo 不依赖启动整个应用
//public class SimpleStartupReporterTest {
//    @Autowired
//    private StartupReporter startupReporter;
//
//    @Test
//    public void testStartupReporter() {
//        Assert.assertNotNull(startupReporter);
//        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
//        Assert.assertNotNull(startupStaticsModel);
//        Assert.assertEquals(5, startupStaticsModel.getStageStats().size());
//
//        Assert.assertTrue(startupStaticsModel.getApplicationBootTime() > 0);
//        Assert.assertTrue(startupStaticsModel.getApplicationBootElapsedTime() > 0);
//        Assert.assertEquals("StartupTest", startupStaticsModel.getAppName());
//
//        BaseStat jvmStartingStage = startupReporter.getStageNyName(BootStageConstants.JVM_STARTING_STAGE);
//        Assert.assertNotNull(jvmStartingStage);
//        Assert.assertTrue(jvmStartingStage.getCost() > 0);
//
//        BaseStat environmentPrepareStage = startupReporter.getStageNyName(BootStageConstants.ENVIRONMENT_PREPARE_STAGE);
//        Assert.assertNotNull(environmentPrepareStage);
//        Assert.assertTrue(environmentPrepareStage.getCost() > 0);
//        Assert.assertEquals(jvmStartingStage.getEndTime(), environmentPrepareStage.getStartTime());
//
//        ChildrenStat<?> applicationContextPrepareStage = (ChildrenStat<?>) startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
//        Assert.assertNotNull(applicationContextPrepareStage);
//        Assert.assertTrue(applicationContextPrepareStage.getChildren().isEmpty());
//        Assert.assertTrue(applicationContextPrepareStage.getCost() > 0);
//        Assert.assertEquals(environmentPrepareStage.getEndTime(), applicationContextPrepareStage.getStartTime());
//
//        BaseStat applicationContextLoadStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_LOAD_STAGE);
//        Assert.assertNotNull(applicationContextLoadStage);
//        Assert.assertTrue(applicationContextLoadStage.getCost() > 0);
//        Assert.assertEquals(applicationContextPrepareStage.getEndTime(), applicationContextLoadStage.getStartTime());
//
//        BaseStat applicationContextRefreshStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE);
//        Assert.assertNotNull(applicationContextRefreshStage);
//        Assert.assertTrue(applicationContextRefreshStage.getCost() > 0);
//        Assert.assertEquals(applicationContextLoadStage.getEndTime(), applicationContextRefreshStage.getStartTime());
//
//        Assert.assertTrue(applicationContextRefreshStage instanceof ChildrenStat);
//        Assert.assertEquals(1, ((ChildrenStat<?>) applicationContextRefreshStage).getChildren().size());
//        ModuleStat moduleStat = (ModuleStat) ((ChildrenStat<?>) applicationContextRefreshStage).getChildren().get(0);
//        Assert.assertNotNull(moduleStat);
//        Assert.assertEquals(StartupContextRefreshedListener.ROOT_MODULE_NAME, moduleStat.getName());
//        Assert.assertTrue(moduleStat.getEndTime() > moduleStat.getStartTime());
//        Assert.assertEquals(moduleStat.getCost(), moduleStat.getEndTime() - moduleStat.getStartTime());
//
//        List<BeanStat> beanStats =  moduleStat.getChildren();
//        Assert.assertNotNull(beanStats);
//        Assert.assertTrue(beanStats.size() >= 1);
//        BeanStat initBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("InitCostBean")).findFirst().orElse(null);
//        Assert.assertNotNull(initBeanStat);
//        Assert.assertEquals(initBeanStat.getRefreshElapsedTime(), initBeanStat.getBeanRefreshEndTime() - initBeanStat.getBeanRefreshStartTime());
//        Assert.assertEquals(InitCostBean.INIT_COST_TIME, initBeanStat.getRealRefreshElapsedTime(), 20);
//        Assert.assertNull(initBeanStat.getBeanType());
//        Assert.assertEquals(InitCostBean.class.getName() + " (initCostBean)", initBeanStat.getBeanClassName());
//        Assert.assertTrue(initBeanStat.getChildren().isEmpty());
//        Assert.assertEquals(0, initBeanStat.getAfterPropertiesSetTime());
//        Assert.assertEquals(0, initBeanStat.getInitTime());
//        Assert.assertNull(initBeanStat.getInterfaceType());
//        Assert.assertNull(initBeanStat.getExtensionProperty());
//    }
//}
