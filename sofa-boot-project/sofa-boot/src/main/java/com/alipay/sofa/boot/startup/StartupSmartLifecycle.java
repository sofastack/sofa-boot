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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;

import static com.alipay.sofa.boot.startup.BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE;

/**
 * Implementation of {@link SmartLifecycle} to compute application context refresh time.
 *
 * @author Zhijie
 * @since 2020/7/20
 */
public class StartupSmartLifecycle implements SmartLifecycle, ApplicationContextAware {

    public static final String             ROOT_MODULE_NAME = "ROOT_APPLICATION_CONTEXT";

    private final StartupReporter          startupReporter;

    private ConfigurableApplicationContext applicationContext;

    public StartupSmartLifecycle(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void start() {
        // init ContextRefreshStageStat
        ChildrenStat<ModuleStat> stat = new ChildrenStat<>();
        stat.setName(APPLICATION_CONTEXT_REFRESH_STAGE);
        stat.setEndTime(System.currentTimeMillis());

        // build root ModuleStat
        ModuleStat rootModuleStat = new ModuleStat();
        rootModuleStat.setName(ROOT_MODULE_NAME);
        rootModuleStat.setEndTime(stat.getEndTime());
        rootModuleStat.setThreadName(Thread.currentThread().getName());

        // getBeanStatList from ApplicationStartup
        rootModuleStat.setChildren(startupReporter.generateBeanStats(applicationContext));

        // report ContextRefreshStageStat
        stat.addChild(rootModuleStat);
        startupReporter.addCommonStartupStat(stat);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }
}
