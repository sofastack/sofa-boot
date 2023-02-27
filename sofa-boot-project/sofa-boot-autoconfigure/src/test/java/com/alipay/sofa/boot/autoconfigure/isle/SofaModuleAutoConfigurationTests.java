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
import com.alipay.sofa.boot.context.processor.SofaPostProcessorShareManager;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.ModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.boot.isle.loader.SpringContextLoader;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.boot.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.boot.isle.spring.SofaModuleContextLifecycle;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import com.alipay.sofa.boot.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.boot.isle.stage.PipelineContext;
import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaModuleAutoConfiguration}.
 *
 * @author huzijie
 * @version SofaModuleAutoConfigurationTests.java, v 0.1 2023年02月01日 4:46 PM huzijie Exp $
 */
public class SofaModuleAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaModuleAutoConfiguration.class));

    @Test
    public void registerIsleBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(PipelineContext.class)
                        .hasSingleBean(SofaModuleContextLifecycle.class)
                        .hasSingleBean(ApplicationRuntimeModel.class)
                        .hasSingleBean(ModelCreatingStage.class)
                        .hasSingleBean(SpringContextInstallStage.class)
                        .hasSingleBean(ModuleLogOutputStage.class)
                        .hasSingleBean(SpringContextLoader.class)
                        .hasBean(SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME)
                        .hasSingleBean(SofaModuleProfileChecker.class)
                        .hasSingleBean(ModuleDeploymentValidator.class)
                        .hasSingleBean(SofaPostProcessorShareManager.class));
    }

    @Test
    public void noIsleBeansWhenApplicationRuntimeModelClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(PipelineContext.class)
                        .doesNotHaveBean(SofaModuleContextLifecycle.class)
                        .doesNotHaveBean(ApplicationRuntimeModel.class)
                        .doesNotHaveBean(ModelCreatingStage.class)
                        .doesNotHaveBean(SpringContextInstallStage.class)
                        .doesNotHaveBean(ModuleLogOutputStage.class)
                        .doesNotHaveBean(SpringContextLoader.class)
                        .doesNotHaveBean(SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME)
                        .doesNotHaveBean(SofaModuleProfileChecker.class)
                        .doesNotHaveBean(ModuleDeploymentValidator.class)
                        .doesNotHaveBean(SofaPostProcessorShareManager.class));
    }

    @Test
    public void noIsleBeansWhenDisablePropertyExist() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(PipelineContext.class)
                        .doesNotHaveBean(SofaModuleContextLifecycle.class)
                        .doesNotHaveBean(ApplicationRuntimeModel.class)
                        .doesNotHaveBean(ModelCreatingStage.class)
                        .doesNotHaveBean(SpringContextInstallStage.class)
                        .doesNotHaveBean(ModuleLogOutputStage.class)
                        .doesNotHaveBean(SpringContextLoader.class)
                        .doesNotHaveBean(SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME)
                        .doesNotHaveBean(SofaModuleProfileChecker.class)
                        .doesNotHaveBean(ModuleDeploymentValidator.class)
                        .doesNotHaveBean(SofaPostProcessorShareManager.class));
    }

    @Test
    public void noSofaModuleRefreshExecutorWhenDisableParallel() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.moduleStartUpParallel=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME));
    }

    @Test
    void customSpringContextInstallStage() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.moduleStartUpParallel=false")
                .withPropertyValues("sofa.boot.isle.ignoreModuleInstallFailure=true")
                .run((context) -> {
                    SpringContextInstallStage springContextInstallStage = context.getBean(SpringContextInstallStage.class);
                    assertThat(springContextInstallStage.isModuleStartUpParallel()).isFalse();
                    assertThat(springContextInstallStage.isIgnoreModuleInstallFailure()).isTrue();
                });
    }

    @Test
    void customSpringContextLoader() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.allowBeanDefinitionOverriding=true")
                .withPropertyValues("sofa.boot.isle.publishEventToParent=true")
                .run((context) -> {
                    DynamicSpringContextLoader DynamicSpringContextLoader = context.getBean(DynamicSpringContextLoader.class);
                    assertThat(DynamicSpringContextLoader.isAllowBeanOverriding()).isTrue();
                    assertThat(DynamicSpringContextLoader.isPublishEventToParent()).isTrue();
                });
    }

    @Test
    void customSofaModuleRefreshExecutor() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.parallelRefreshPoolSizeFactor=2")
                .withPropertyValues("sofa.boot.isle.parallelRefreshTimeout=10")
                .withPropertyValues("sofa.boot.isle.parallelRefreshCheckPeriod=20")
                .run((context) -> {
                    SofaThreadPoolExecutor threadPoolExecutor = (SofaThreadPoolExecutor) context.getBean(
                            SpringContextInstallStage.SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME, Supplier.class).get();
                    assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(SofaBootConstants.CPU_CORE * 2);
                    assertThat(threadPoolExecutor.getConfig().getTaskTimeout()).isEqualTo(10);
                    assertThat(threadPoolExecutor.getConfig().getPeriod()).isEqualTo(20);
                });
    }

    @Test
    void customSofaModuleProfileChecker() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.activeProfiles=ab,cd")
                .run((context) -> {
                    DefaultSofaModuleProfileChecker sofaModuleProfileChecker = context.getBean(DefaultSofaModuleProfileChecker.class);
                    assertThat(sofaModuleProfileChecker.getUserCustomProfiles()).contains("ab", "cd");
                });
    }

    @Test
    void customSofaPostProcessorShareManager() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.shareParentPostProcessor=false")
                .run((context) -> {
                    SofaPostProcessorShareManager sofaPostProcessorShareManager = context.getBean(SofaPostProcessorShareManager.class);
                    assertThat(sofaPostProcessorShareManager.isShareParentContextPostProcessors()).isFalse();
                });
    }

    @Test
    void customModelCreatingStage() {
        this.contextRunner
                .withPropertyValues("sofa.boot.isle.ignoreModules=a,b,c")
                .withPropertyValues("sofa.boot.isle.ignoreCalculateRequireModules=e,f,g")
                .withPropertyValues("sofa.boot.isle.allowModuleOverriding=true")
                .run((context) -> {
                    ModelCreatingStage modelCreatingStage = context.getBean(ModelCreatingStage.class);
                    assertThat(modelCreatingStage.getIgnoreModules()).contains("a", "b", "c");
                    assertThat(modelCreatingStage.getIgnoreCalculateRequireModules()).contains("e", "f", "g");
                    assertThat(modelCreatingStage.isAllowModuleOverriding()).isTrue();
                });
    }
}
