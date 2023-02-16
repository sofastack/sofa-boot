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
package com.alipay.sofa.boot.startup;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.management.ManagementFactory;
import java.time.Duration;

/**
 * Implementation of {@link SpringApplicationRunListener} to compute startup stages cost time.
 *
 * @author Zhijie
 * @since 2020/7/20
 */
public class StartupSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication application;

    private final String[]          args;

    private final StartupReporter   startupReporter;

    private BaseStat                jvmStartingStage;

    private BaseStat                environmentPrepareStage;

    private BaseStat                applicationContextPrepareStage;

    private BaseStat                applicationContextLoadStage;

    public StartupSpringApplicationRunListener(SpringApplication springApplication, String[] args) {
        this.application = springApplication;
        this.args = args;
        this.startupReporter = new StartupReporter();
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        BaseStat stat = new BaseStat();
        stat.setName(BootStageConstants.JVM_STARTING_STAGE);
        stat.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        stat.setEndTime(System.currentTimeMillis());
        jvmStartingStage = stat;
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        BaseStat stat = new BaseStat();
        stat.setName(BootStageConstants.ENVIRONMENT_PREPARE_STAGE);
        stat.setStartTime(jvmStartingStage.getEndTime());
        stat.setEndTime(System.currentTimeMillis());
        environmentPrepareStage = stat;
        startupReporter.setAppName(environment.getProperty(SofaBootConstants.APP_NAME_KEY));
        startupReporter.bindToStartupReporter(environment);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        ChildrenStat<BaseStat> stat = new ChildrenStat<>();
        stat.setName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        stat.setStartTime(environmentPrepareStage.getEndTime());
        stat.setEndTime(System.currentTimeMillis());
        if (application instanceof StartupSpringApplication) {
            stat.setChildren(((StartupSpringApplication) application)
                .getInitializerStartupStatList());
        }
        applicationContextPrepareStage = stat;
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        BaseStat stat = new BaseStat();
        stat.setName(BootStageConstants.APPLICATION_CONTEXT_LOAD_STAGE);
        stat.setStartTime(applicationContextPrepareStage.getEndTime());
        stat.setEndTime(System.currentTimeMillis());
        BeanCostBeanPostProcessor beanCostBeanPostProcessor = new BeanCostBeanPostProcessor();
        beanCostBeanPostProcessor.setCostThreshold(startupReporter.getCostThreshold());
        context.getBeanFactory().addBeanPostProcessor(beanCostBeanPostProcessor);
        context.getBeanFactory().addBeanPostProcessor(
            new StartupReporterBeanPostProcessor(startupReporter));
        context.getBeanFactory().registerSingleton("STARTUP_REPORTER_BEAN", startupReporter);
        StartupSmartLifecycle startupSmartLifecycle = new StartupSmartLifecycle(startupReporter);
        startupSmartLifecycle.setApplicationContext(context);
        context.getBeanFactory().registerSingleton("STARTUP_SMART_LIfE_CYCLE",
            startupSmartLifecycle);
        applicationContextLoadStage = stat;
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        // refresh applicationRefreshStage
        ChildrenStat<ModuleStat> applicationRefreshStage = (ChildrenStat<ModuleStat>) startupReporter
            .getStageNyName(BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE);
        applicationRefreshStage.setStartTime(applicationContextLoadStage.getEndTime());
        applicationRefreshStage.setCost(applicationRefreshStage.getEndTime()
                                        - applicationRefreshStage.getStartTime());

        // init rootModuleStat
        ModuleStat rootModule = applicationRefreshStage.getChildren().get(0);
        rootModule.setStartTime(applicationRefreshStage.getStartTime());
        rootModule.setCost(rootModule.getEndTime() - rootModule.getStartTime());

        // report all stage
        startupReporter.addCommonStartupStat(jvmStartingStage);
        startupReporter.addCommonStartupStat(environmentPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextLoadStage);
        startupReporter.applicationBootFinish();

        // print log
        printLog(startupReporter.report());

        // clear statics when doesn't need store
        if (!startupReporter.isStoreStatics()) {
            startupReporter.clear();
        }
    }

    private void printLog(StartupReporter.StartupStaticsModel report) {
        //todo 打印耗时日志
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
