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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.listener.SofaModuleBeanFactoryPostProcessor;
import com.alipay.sofa.isle.spring.listener.SofaModuleContextRefreshedListener;
import com.alipay.sofa.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.isle.stage.PipelineContext;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;

/**
 * @author xuanbei 18/3/12
 */
@Configuration
@EnableConfigurationProperties(SofaModuleProperties.class)
@ConditionalOnClass(ApplicationRuntimeModel.class)
public class SofaModuleAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public static SofaModuleBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor() {
        return new SofaModuleBeanFactoryPostProcessor();
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
}
