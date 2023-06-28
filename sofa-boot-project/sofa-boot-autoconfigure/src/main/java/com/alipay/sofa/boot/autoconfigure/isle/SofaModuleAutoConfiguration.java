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
package com.alipay.sofa.boot.autoconfigure.isle;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.context.ContextRefreshInterceptor;
import com.alipay.sofa.boot.context.processor.SofaPostProcessorShareFilter;
import com.alipay.sofa.boot.context.processor.SofaPostProcessorShareManager;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DefaultModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.deployment.ModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.boot.isle.loader.SpringContextLoader;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.boot.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.boot.isle.spring.SofaModuleContextLifecycle;
import com.alipay.sofa.boot.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import com.alipay.sofa.boot.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.boot.isle.stage.PipelineContext;
import com.alipay.sofa.boot.isle.stage.PipelineStage;
import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.common.thread.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for sofa Isle.
 *
 * @author xuanbei 18/3/12
 * @author huzijie
 */
@AutoConfiguration
@Conditional(SofaModuleAvailableCondition.class)
@EnableConfigurationProperties(SofaModuleProperties.class)
public class SofaModuleAutoConfiguration {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(SofaModuleAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public PipelineContext pipelineContext(List<PipelineStage> stageList) {
        PipelineContext pipelineContext = new DefaultPipelineContext();
        stageList.forEach(pipelineContext::appendStage);
        return pipelineContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaModuleContextLifecycle sofaModuleContextLifecycle(PipelineContext pipelineContext) {
        return new SofaModuleContextLifecycle(pipelineContext);
    }

    @Bean(ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME)
    @ConditionalOnMissingBean
    public ApplicationRuntimeModel applicationRuntimeModel(ModuleDeploymentValidator moduleDeploymentValidator,
                                                           SofaModuleProfileChecker sofaModuleProfileChecker) {
        ApplicationRuntimeModel applicationRuntimeModel = new ApplicationRuntimeModel();
        applicationRuntimeModel.setModuleDeploymentValidator(moduleDeploymentValidator);
        applicationRuntimeModel.setSofaModuleProfileChecker(sofaModuleProfileChecker);
        return applicationRuntimeModel;
    }

    @Bean
    @ConditionalOnMissingBean
    public ModelCreatingStage modelCreatingStage(
            SofaModuleProperties sofaModuleProperties,
            ApplicationRuntimeModel applicationRuntimeModel) {
        ModelCreatingStage modelCreatingStage = new ModelCreatingStage();
        sofaModuleProperties.getIgnoreModules().forEach(modelCreatingStage::addIgnoreModule);
        sofaModuleProperties.getIgnoreCalculateRequireModules().forEach(modelCreatingStage::addIgnoredCalculateRequireModule);
        modelCreatingStage.setAllowModuleOverriding(sofaModuleProperties.isAllowModuleOverriding());
        modelCreatingStage.setApplicationRuntimeModel(applicationRuntimeModel);
        return modelCreatingStage;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContextInstallStage springContextInstallStage(SofaModuleProperties sofaModuleProperties,
                                                               SpringContextLoader springContextLoader,
                                                               ApplicationRuntimeModel applicationRuntimeModel) {
        SpringContextInstallStage springContextInstallStage = new SpringContextInstallStage();
        springContextInstallStage.setApplicationRuntimeModel(applicationRuntimeModel);
        springContextInstallStage.setSpringContextLoader(springContextLoader);
        springContextInstallStage.setModuleStartUpParallel(sofaModuleProperties
            .isModuleStartUpParallel());
        springContextInstallStage.setIgnoreModuleInstallFailure(sofaModuleProperties
            .isIgnoreModuleInstallFailure());
        return springContextInstallStage;
    }

    @Bean
    @ConditionalOnMissingBean
    public ModuleLogOutputStage moduleLogOutputStage(ApplicationRuntimeModel applicationRuntimeModel) {
        ModuleLogOutputStage moduleLogOutputStage = new ModuleLogOutputStage();
        moduleLogOutputStage.setApplicationRuntimeModel(applicationRuntimeModel);
        return moduleLogOutputStage;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContextLoader sofaDynamicSpringContextLoader(SofaModuleProperties sofaModuleProperties,
                                                              ApplicationContext applicationContext,
                                                              ObjectProvider<ContextRefreshInterceptor> contextRefreshInterceptors,
                                                              SofaPostProcessorShareManager sofaPostProcessorShareManager) {
        DynamicSpringContextLoader dynamicSpringContextLoader = new DynamicSpringContextLoader(
            applicationContext);
        dynamicSpringContextLoader.setActiveProfiles(sofaModuleProperties.getActiveProfiles());
        dynamicSpringContextLoader.setAllowBeanOverriding(sofaModuleProperties
            .isAllowBeanDefinitionOverriding());
        dynamicSpringContextLoader.setPublishEventToParent(sofaModuleProperties
            .isPublishEventToParent());
        dynamicSpringContextLoader.setContextRefreshInterceptors(new ArrayList<>(
            contextRefreshInterceptors.orderedStream().toList()));
        dynamicSpringContextLoader.setSofaPostProcessorShareManager(sofaPostProcessorShareManager);
        return dynamicSpringContextLoader;
    }

    @Bean(SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME)
    @ConditionalOnProperty(value = "sofa.boot.isle.moduleStartUpParallel", havingValue = "true", matchIfMissing = true)
    public Supplier<ThreadPoolExecutor> sofaModuleRefreshExecutor(SofaModuleProperties sofaModuleProperties) {
        int coreSize = (int) (SofaBootConstants.CPU_CORE * sofaModuleProperties.getParallelRefreshPoolSizeFactor());
        long taskTimeout = sofaModuleProperties.getParallelRefreshTimeout();
        long checkPeriod = sofaModuleProperties.getParallelRefreshCheckPeriod();

        LOGGER.info("Create SOFA module refresh thread pool, corePoolSize: {}, maxPoolSize: {}," +
                        " taskTimeout:{}, checkPeriod:{}",
                coreSize, coreSize, taskTimeout, checkPeriod);
        return () -> new SofaThreadPoolExecutor(coreSize, coreSize, 60,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000),
                new NamedThreadFactory("sofa-module-refresh"), new ThreadPoolExecutor.CallerRunsPolicy(),
                "sofa-module-refresh", SofaBootConstants.SOFA_BOOT_SPACE_NAME, taskTimeout, checkPeriod,
                TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaModuleProfileChecker sofaModuleProfileChecker(SofaModuleProperties sofaModuleProperties) {
        DefaultSofaModuleProfileChecker defaultSofaModuleProfileChecker = new DefaultSofaModuleProfileChecker();
        defaultSofaModuleProfileChecker.setUserCustomProfiles(sofaModuleProperties
            .getActiveProfiles());
        return defaultSofaModuleProfileChecker;
    }

    @Bean
    @ConditionalOnMissingBean
    public ModuleDeploymentValidator sofaModuleDeploymentValidator() {
        return new DefaultModuleDeploymentValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaPostProcessorShareManager sofaModulePostProcessorShareManager(SofaModuleProperties sofaModuleProperties,
                                                                             ObjectProvider<SofaPostProcessorShareFilter> sofaPostProcessorShareFilters) {
        SofaPostProcessorShareManager sofaPostProcessorShareManager = new SofaPostProcessorShareManager();
        sofaPostProcessorShareManager.setShareParentContextPostProcessors(sofaModuleProperties
            .isShareParentPostProcessor());
        sofaPostProcessorShareManager
            .setSofaPostProcessorShareFilters(sofaPostProcessorShareFilters.orderedStream()
                .toList());
        return sofaPostProcessorShareManager;
    }

}
