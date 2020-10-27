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

import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.listener.SofaModuleContextRefreshedListener;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareManager;
import com.alipay.sofa.isle.stage.*;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.SofaStartupProperties;
import com.alipay.sofa.startup.spring.IsleSpringContextAwarer;
import com.alipay.sofa.startup.spring.SpringContextAwarer;
import com.alipay.sofa.startup.stage.StartupSpringContextInstallStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Zhijie
 * @since: 2020/7/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class IsleStageCostTest {
    @Autowired
    private SofaStartupContext sofaStartupContext;

    @Test
    public void testComponentCost() {
        Assert.assertTrue(sofaStartupContext.getIsleInstallCost() >= 0);
    }

    @Configuration
    @EnableConfigurationProperties({ SofaModuleProperties.class, SofaStartupProperties.class })
    static class SofaStartupContextIsleStageCostTestConfiguration {
        @Bean
        public SofaStartupContext sofaStartupContext(SpringContextAwarer springContextAwarer,
                                                     SofaStartupProperties sofaStartupProperties) {
            return new SofaStartupContext(springContextAwarer, sofaStartupProperties);
        }

        @Bean
        public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext) {
            return new StartupSpringContextInstallStage(
                (AbstractApplicationContext) applicationContext);
        }

        @Bean
        public IsleSpringContextAwarer isleSpringContextAwarer(StartupSpringContextInstallStage startupSpringContextInstallStage) {
            return new IsleSpringContextAwarer(startupSpringContextInstallStage);
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaModuleContextRefreshedListener sofaModuleContextRefreshedListener() {
            return new SofaModuleContextRefreshedListener();
        }

        @Bean
        @ConditionalOnMissingBean
        public ModelCreatingStage modelCreatingStage(ApplicationContext applicationContext) {
            return new ModelCreatingStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public SpringContextInstallStage springContextInstallStage(ApplicationContext applicationContext) {
            return new SpringContextInstallStage((AbstractApplicationContext) applicationContext);
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

        @Bean(destroyMethod = "")
        @ConditionalOnMissingBean
        public static SofaRuntimeManager sofaRuntimeManager() {
            ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
            SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
                "IsleStageCostTest", Thread.currentThread().getContextClassLoader(),
                clientFactoryInternal);
            SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
            return sofaRuntimeManager;
        }
    }
}
