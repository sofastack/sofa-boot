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
package com.alipay.sofa.smoke.tests.isle.model;

import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleProperties;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * @author huzijie
 * @version CustomModelContextCustomizer.java, v 0.1 2023年02月03日 10:37 AM huzijie Exp $
 */
public class CustomModelContextCustomizer implements ContextCustomizer, ApplicationContextAware,
                                         BeanDefinitionRegistryPostProcessor, BeanPostProcessor {

    private final String[]     paths;
    private ApplicationContext applicationContext;

    public CustomModelContextCustomizer(String[] paths) {
        this.paths = paths;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        if (context instanceof BeanDefinitionRegistry registry) {
            RootBeanDefinition beanDef = new RootBeanDefinition(CustomModelContextCustomizer.class);
            beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, paths);
            registry.registerBeanDefinition("customModelContextCustomizer", beanDef);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                                                                                  throws BeansException {
        String modelCreatingStageBeanName = "modelCreatingStage";
        if (registry.containsBeanDefinition(modelCreatingStageBeanName)) {
            registry.removeBeanDefinition(modelCreatingStageBeanName);

            RootBeanDefinition beanDef = new RootBeanDefinition(CustomModelCreatingStage.class);
            beanDef.getConstructorArgumentValues().addIndexedArgumentValue(0, paths);
            beanDef.getPropertyValues().addPropertyValue("applicationRuntimeModel",
                new RuntimeBeanReference(ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME));
            registry.registerBeanDefinition(modelCreatingStageBeanName, beanDef);
        }
    }

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ModelCreatingStage modelCreatingStage) {
            SofaModuleProperties sofaModuleProperties = applicationContext.getBean(SofaModuleProperties.class);
            sofaModuleProperties.getIgnoreModules().forEach(modelCreatingStage::addIgnoreModule);
            sofaModuleProperties.getIgnoreCalculateRequireModules().forEach(modelCreatingStage::addIgnoredCalculateRequireModule);
            modelCreatingStage.setAllowModuleOverriding(sofaModuleProperties.isAllowModuleOverriding());
            return bean;
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
