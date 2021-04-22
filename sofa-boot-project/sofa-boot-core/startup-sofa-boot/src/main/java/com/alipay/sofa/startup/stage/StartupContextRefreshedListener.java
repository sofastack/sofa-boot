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
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.startup.StartupReporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Collections;

import static com.alipay.sofa.boot.startup.BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE;

/**
 * SpringApplicationRunListener to record application startup time
 *
 * @author Zhijie
 * @since 2020/7/20
 */
public class StartupContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>,
                                            ApplicationContextAware, PriorityOrdered {
    public static final String ROOT_MODULE_NAME = "ROOT_APPLICATION_CONTEXT";
    private ApplicationContext applicationContext;
    private StartupReporter    startupReporter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            try {
                startupReporter = applicationContext.getBean(StartupReporter.class);
            } catch (NoSuchBeanDefinitionException e) {
                // just happen in unit tests
                SofaLogger.warn("Failed to found bean StartupReporter", e);
                return;
            }
            // init ContextRefreshStageStat
            ContextRefreshStageStat stageStat = new ContextRefreshStageStat();
            stageStat.setStageName(APPLICATION_CONTEXT_REFRESH_STAGE);
            stageStat.setStageEndTime(System.currentTimeMillis());

            // build root ModuleStat
            ModuleStat rootModuleStat = new ModuleStat();
            rootModuleStat.setModuleName(ROOT_MODULE_NAME);
            rootModuleStat.setModuleEndTime(stageStat.getStageEndTime());
            rootModuleStat.setThreadName(Thread.currentThread().getName());

            // getBeanStatList from BeanCostBeanPostProcessor
            BeanCostBeanPostProcessor beanCostBeanPostProcessor = applicationContext
                .getBean(BeanCostBeanPostProcessor.class);
            rootModuleStat.setBeanStats((beanCostBeanPostProcessor.getBeanStatList()));

            // report ContextRefreshStageStat
            stageStat.setModuleStats(Collections.singletonList(rootModuleStat));
            startupReporter.addCommonStartupStat(stageStat);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
