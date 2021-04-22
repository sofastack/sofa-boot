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
package com.alipay.sofa.startup.stage;

import com.alipay.sofa.boot.startup.ContextRefreshStageStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.boot.startup.StageStat;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.startup.StartupReporter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.management.ManagementFactory;

import static com.alipay.sofa.boot.startup.BootStageConstants.*;

/**
 * SpringApplicationRunListener to record application startup time
 *
 * @author Zhijie
 * @since 2020/7/20
 */
public class StartupSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {
    private final SpringApplication application;
    private final String[]          args;
    private StageStat               jvmStartingStage;
    private StageStat               environmentPrepareStage;
    private StageStat               applicationContextPrepareStage;
    private StageStat               applicationContextLoadStage;

    public StartupSpringApplicationRunListener(SpringApplication sa, String[] args) {
        this.application = sa;
        this.args = args;
    }

    @Override
    public void starting() {
        StageStat stageStat = new StageStat();
        stageStat.setStageName(JVM_STARTING_STAGE);
        stageStat.setStageStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        stageStat.setStageEndTime(System.currentTimeMillis());
        this.jvmStartingStage = stageStat;
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        StageStat stageStat = new StageStat();
        stageStat.setStageName(ENVIRONMENT_PREPARE_STAGE);
        stageStat.setStageStartTime(jvmStartingStage.getStageEndTime());
        stageStat.setStageEndTime(System.currentTimeMillis());
        this.environmentPrepareStage = stageStat;
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        StageStat stageStat = new StageStat();
        stageStat.setStageName(APPLICATION_CONTEXT_PREPARE_STAGE);
        stageStat.setStageStartTime(environmentPrepareStage.getStageEndTime());
        stageStat.setStageEndTime(System.currentTimeMillis());
        this.applicationContextPrepareStage = stageStat;
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        StageStat stageStat = new StageStat();
        stageStat.setStageName(APPLICATION_CONTEXT_LOAD_STAGE);
        stageStat.setStageStartTime(applicationContextPrepareStage.getStageEndTime());
        stageStat.setStageEndTime(System.currentTimeMillis());
        this.applicationContextLoadStage = stageStat;
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        StartupReporter startupReporter;
        try {
            startupReporter = context.getBean(StartupReporter.class);
        } catch (NoSuchBeanDefinitionException e) {
            // just happen in unit tests
            SofaLogger.warn("Failed to found bean StartupReporter", e);
            return;
        }
        // refresh applicationRefreshStage
        ContextRefreshStageStat applicationRefreshStage = (ContextRefreshStageStat) startupReporter
            .getStageNyName(APPLICATION_CONTEXT_REFRESH_STAGE);
        applicationRefreshStage.setStageStartTime(applicationContextLoadStage.getStageEndTime());
        applicationRefreshStage.setElapsedTime(applicationRefreshStage.getStageEndTime()
                                               - applicationRefreshStage.getStageStartTime());

        // init rootModuleStat
        ModuleStat rootModule = applicationRefreshStage.getModuleStats().get(0);
        rootModule.setModuleStartTime(applicationRefreshStage.getStageStartTime());
        rootModule.setElapsedTime(rootModule.getModuleEndTime() - rootModule.getModuleStartTime());

        // report all stage
        startupReporter.addCommonStartupStat(jvmStartingStage);
        startupReporter.addCommonStartupStat(environmentPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextLoadStage);
        startupReporter.applicationBootFinish();
    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
