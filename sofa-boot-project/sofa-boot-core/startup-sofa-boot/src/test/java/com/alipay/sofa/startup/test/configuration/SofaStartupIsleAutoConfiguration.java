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
package com.alipay.sofa.startup.test.configuration;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.SofaModuleBeanFactoryPostProcessor;
import com.alipay.sofa.isle.spring.SofaModuleContextLifecycle;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareManager;
import com.alipay.sofa.isle.stage.*;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.isle.StartupModelCreatingStage;
import com.alipay.sofa.startup.stage.isle.StartupSpringContextInstallStage;
import com.alipay.sofa.startup.test.stage.TestModelCreatingStage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author huzijie
 * @version SofaStartupIsleAutoConfiguration.java, v 0.1 2021年01月04日 7:07 下午 huzijie Exp $
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ApplicationRuntimeModel.class, StartupReporter.class })
@EnableConfigurationProperties({ SofaModuleProperties.class })
public class SofaStartupIsleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
    public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext,
                                                                             StartupReporter startupReporter) {
        return new StartupSpringContextInstallStage(
            (AbstractApplicationContext) applicationContext, startupReporter);
    }

    @Bean
    @ConditionalOnMissingBean(value = ModelCreatingStage.class, search = SearchStrategy.CURRENT)
    public StartupModelCreatingStage startupModelCreatingStage(ApplicationContext applicationContext,
                                                               StartupReporter startupReporter) {
        return new TestModelCreatingStage((AbstractApplicationContext) applicationContext,
            startupReporter);
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaModuleContextLifecycle sofaModuleContextLifecycle() {
        return new SofaModuleContextLifecycle();
    }

    @Bean
    @ConditionalOnMissingBean
    public ModuleLogOutputStage moduleLogOutputStage(ApplicationContext applicationContext) {
        return new ModuleLogOutputStage((AbstractApplicationContext) applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public PipelineContext pipelineContext() {
        return new DefaultPipelineContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaModuleProfileChecker sofaModuleProfileChecker() {
        return new DefaultSofaModuleProfileChecker();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaModulePostProcessorShareManager sofaModulePostProcessorShareManager(ApplicationContext applicationContext) {
        return new SofaModulePostProcessorShareManager(
            (AbstractApplicationContext) applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaModuleBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor(SofaModulePostProcessorShareManager shareManager) {
        return new SofaModuleBeanFactoryPostProcessor(shareManager);
    }

    @Bean(destroyMethod = "")
    @ConditionalOnMissingBean
    public static SofaRuntimeManager sofaRuntimeManager() {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager("IsleStageCostTest",
            Thread.currentThread().getContextClassLoader(), clientFactoryInternal);
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        return sofaRuntimeManager;
    }
}
