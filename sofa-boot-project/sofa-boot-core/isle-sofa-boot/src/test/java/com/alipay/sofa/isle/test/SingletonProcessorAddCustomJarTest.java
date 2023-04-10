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
package com.alipay.sofa.isle.test;

import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.SofaModuleContextLifecycle;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.isle.stage.PipelineContext;
import com.alipay.sofa.isle.stage.PipelineStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.isle.test.processor.SampleBeanPostProcessor;
import com.alipay.sofa.isle.test.processor.SingletonBeanPostProcessor;
import com.alipay.sofa.isle.test.util.AddCustomJar;
import com.alipay.sofa.isle.test.util.SeparateClassLoaderTestRunner;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spring.SofaShareBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import com.alipay.sofa.runtime.spring.share.SofaPostProcessorShareManager;
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

import java.util.Collection;
import java.util.List;

/**
 * @author huzijie
 * @version SingletonProcessorTest.java, v 0.1 2022年10月25日 11:33 AM huzijie Exp $
 */
@RunWith(SeparateClassLoaderTestRunner.class)
@SpringBootTest
@AddCustomJar({ "dev-module-0.1.0.jar" })
public class SingletonProcessorAddCustomJarTest {

    @Autowired
    private SingletonBeanPostProcessor singletonBeanPostProcessor;

    @Autowired
    private SampleBeanPostProcessor    sampleBeanPostProcessor;

    @Autowired
    private SofaRuntimeManager         sofaRuntimeManager;

    @Test
    public void testSingletonBpp() {
        Collection<ComponentInfo> components =
                sofaRuntimeManager.getComponentManager().getComponentInfosByType(SpringContextComponent.SPRING_COMPONENT_TYPE);
        ApplicationContext applicationContext = components.stream().filter(componentInfo -> componentInfo.getName().getRawName().contains("dev")).findFirst().get().getApplicationContext();
        SingletonBeanPostProcessor singletonBeanPostProcessor = applicationContext.getBean(SingletonBeanPostProcessor.class);
        SampleBeanPostProcessor sampleBeanPostProcessor = applicationContext.getBean(SampleBeanPostProcessor.class);
        Assert.assertEquals(singletonBeanPostProcessor, this.singletonBeanPostProcessor);
        Assert.assertNotEquals(sampleBeanPostProcessor, this.sampleBeanPostProcessor);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(SofaModuleProperties.class)
    public static class SofaModuleProfileCheckerTestConfiguration {

        @Bean
        public static SofaShareBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor() {
            return new SofaShareBeanFactoryPostProcessor();
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaModuleContextLifecycle sofaModuleContextLifecycle(PipelineContext pipelineContext) {
            return new SofaModuleContextLifecycle(pipelineContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public ModelCreatingStage modelCreatingStage(ApplicationContext applicationContext,
                                                     SofaModuleProperties sofaModuleProperties,
                                                     SofaModuleProfileChecker sofaModuleProfileChecker) {
            return new ModelCreatingStage((AbstractApplicationContext) applicationContext,
                sofaModuleProperties, sofaModuleProfileChecker);
        }

        @Bean
        @ConditionalOnMissingBean
        public SpringContextInstallStage springContextInstallStage(ApplicationContext applicationContext,
                                                                   SofaModuleProperties sofaModuleProperties) {
            return new SpringContextInstallStage((AbstractApplicationContext) applicationContext,
                sofaModuleProperties);
        }

        @Bean
        @ConditionalOnMissingBean
        public ModuleLogOutputStage moduleLogOutputStage(ApplicationContext applicationContext) {
            return new ModuleLogOutputStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public PipelineContext pipelineContext(List<PipelineStage> stageList) {
            return new DefaultPipelineContext(stageList);
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaModuleProfileChecker sofaModuleProfileChecker(SofaModuleProperties sofaModuleProperties) {
            return new DefaultSofaModuleProfileChecker(sofaModuleProperties);
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaPostProcessorShareManager sofaModulePostProcessorShareManager() {
            return new SofaPostProcessorShareManager();
        }

        @Bean(destroyMethod = "")
        @ConditionalOnMissingBean
        public static SofaRuntimeManager sofaRuntimeManager() {
            ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
            SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
                "FailModuleTest", Thread.currentThread().getContextClassLoader(),
                clientFactoryInternal);
            SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
            return sofaRuntimeManager;
        }

        @Bean
        public SingletonBeanPostProcessor singletonBeanPostProcessor() {
            return new SingletonBeanPostProcessor();
        }

        @Bean
        public SampleBeanPostProcessor sampleBeanPostProcessor() {
            return new SampleBeanPostProcessor();
        }
    }
}