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
package com.alipay.sofa.boot.actuator.startup.isle;

import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import static com.alipay.sofa.boot.startup.BootStageConstants.ISLE_SPRING_CONTEXT_INSTALL_STAGE;

/**
 * Extend for {@link SpringContextInstallStage} to compute sofa module refresh cost time.
 *
 * @author Zhijie
 * @since 2020/7/8
 */
public class StartupSpringContextInstallStage extends SpringContextInstallStage {

    private final StartupReporter    startupReporter;

    private ChildrenStat<ModuleStat> contextRefreshStageStat;

    public StartupSpringContextInstallStage(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @Override
    protected void doProcess() throws Exception {
        contextRefreshStageStat = new ChildrenStat<>();
        contextRefreshStageStat.setName(ISLE_SPRING_CONTEXT_INSTALL_STAGE);
        contextRefreshStageStat.setStartTime(System.currentTimeMillis());
        try {
            super.doProcess();
        } finally {
            contextRefreshStageStat.setEndTime(System.currentTimeMillis());
            startupReporter.addCommonStartupStat(contextRefreshStageStat);
        }
    }

    @Override
    protected void doRefreshSpringContext(DeploymentDescriptor deployment) {

        ModuleStat moduleStat = new ModuleStat();
        moduleStat.setName(deployment.getModuleName());
        moduleStat.setStartTime(System.currentTimeMillis());

        super.doRefreshSpringContext(deployment);

        moduleStat.setEndTime(System.currentTimeMillis());
        moduleStat.setCost(moduleStat.getEndTime() - moduleStat.getStartTime());
        moduleStat.setThreadName(Thread.currentThread().getName());
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) deployment
            .getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
        if (beanFactory instanceof BeanLoadCostBeanFactory) {
            moduleStat.setChildren(((BeanLoadCostBeanFactory) beanFactory).getBeanStats());
        }

        contextRefreshStageStat.addChild(moduleStat);
    }

    public StartupReporter getStartupReporter() {
        return startupReporter;
    }
}
