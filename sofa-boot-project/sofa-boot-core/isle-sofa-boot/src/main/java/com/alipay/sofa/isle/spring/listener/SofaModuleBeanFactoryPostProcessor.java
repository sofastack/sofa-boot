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
package com.alipay.sofa.isle.spring.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alipay.sofa.boot.constant.SofaBootConstants;

/**
 * get all the BeanPostProcessors and BeanFactoryPostProcessors of the root application context
 *
 * @author xuanbei 18/3/26
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class SofaModuleBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    /** spring will add automatically  **/
    private final String[] whiteNameList = new String[] {
            ConfigurationClassPostProcessor.class.getName() + ".importAwareProcessor",
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry",
            ConfigurationClassPostProcessor.class.getName() + ".enhancedConfigurationProcessor" };

    private SofaModulePostProcessorShareManager sofaModulePostProcessorShareManager;

    private SofaModuleProperties properties;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        this.properties = beanFactory.getBean(SofaModuleProperties.class);
        this.sofaModulePostProcessorShareManager = beanFactory.getBean(SofaModulePostProcessorShareManager.class);

        if (this.properties.enableShareParentContextPostProcessors()) {
            Map<String, BeanDefinition> postProcessorDefinitions = new HashMap<>();
            postProcessorDefinitions.putAll(getBeanDefinitionsForType(beanFactory, BeanPostProcessor.class));
            postProcessorDefinitions.putAll(getBeanDefinitionsForType(beanFactory, BeanFactoryPostProcessor.class));
            beanFactory.registerSingleton(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT, postProcessorDefinitions);
        }
    }

    private Map<String, BeanDefinition> getBeanDefinitionsForType(ConfigurableListableBeanFactory beanFactory, Class type) {
        Map<String, BeanDefinition> map = new HashMap<>();

        // get all beanDefinitionNames from parent context
        List<String> allBeanDefinitionNames = Arrays.asList(beanFactory.getBeanDefinitionNames());

        String[] beanNamesForType = beanFactory.getBeanNamesForType(type);

        for (String beanName : beanNamesForType) {
            if (notInWhiteNameList(beanName) && allBeanDefinitionNames.contains(beanName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                Class cls = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
                if (sofaModulePostProcessorShareManager.unableToShare(cls) || sofaModulePostProcessorShareManager.unableToShare(beanName)) {
                    continue;
                }
                map.put(beanName, beanFactory.getBeanDefinition(beanName));
            }
        }

        return map;
    }

    private boolean notInWhiteNameList(String beanName) {
        for (String whiteName : whiteNameList) {
            if (whiteName.equals(beanName)) {
                return false;
            }
        }

        return true;
    }
}
