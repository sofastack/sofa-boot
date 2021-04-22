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
package com.alipay.sofa.startup.stage.isle;

import com.alipay.sofa.boot.startup.ContextRefreshStageStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.startup.StartupReporter;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import static com.alipay.sofa.boot.startup.BootStageConstants.ISLE_SPRING_CONTEXT_INSTALL_STAGE;

/**
 * Wrapper for SpringContextInstallStage to calculate time cost by install spring context
 *
 * @author Zhijie
 * @since 2020/7/8
 */
public class StartupSpringContextInstallStage extends SpringContextInstallStage {
    private final StartupReporter   startupReporter;
    private ContextRefreshStageStat contextRefreshStageStat;

    public StartupSpringContextInstallStage(AbstractApplicationContext applicationContext,
                                            StartupReporter startupReporter) {
        super(applicationContext);
        this.startupReporter = startupReporter;
    }

    @Override
    protected void doProcess() throws Exception {
        contextRefreshStageStat = new ContextRefreshStageStat();
        contextRefreshStageStat.setStageName(ISLE_SPRING_CONTEXT_INSTALL_STAGE);
        contextRefreshStageStat.setStageStartTime(System.currentTimeMillis());
        try {
            super.doProcess();
        } finally {
            contextRefreshStageStat.setStageEndTime(System.currentTimeMillis());
            startupReporter.addCommonStartupStat(contextRefreshStageStat);
        }
    }

    @Override
    protected void doRefreshSpringContext(DeploymentDescriptor deployment,
                                          ApplicationRuntimeModel application) {

        ModuleStat moduleStat = new ModuleStat();
        moduleStat.setModuleName(deployment.getModuleName());
        moduleStat.setModuleStartTime(System.currentTimeMillis());

        super.doRefreshSpringContext(deployment, application);

        moduleStat.setModuleEndTime(System.currentTimeMillis());
        moduleStat.setElapsedTime(moduleStat.getModuleEndTime() - moduleStat.getModuleStartTime());
        moduleStat.setThreadName(Thread.currentThread().getName());
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) deployment
            .getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
        if (beanFactory instanceof BeanLoadCostBeanFactory) {
            moduleStat.setBeanStats(((BeanLoadCostBeanFactory) beanFactory).getBeanStats());
        }

        contextRefreshStageStat.appendModuleStat(moduleStat);
    }
}
