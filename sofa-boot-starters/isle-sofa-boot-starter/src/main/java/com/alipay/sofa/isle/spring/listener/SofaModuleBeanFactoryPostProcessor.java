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

import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * get all the BeanPostProcessors and BeanFactoryPostProcessors of the root application context
 *
 * @author xuanbei 18/3/26
 */
public class SofaModuleBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    /** spring will add automatically  **/
    private final String[] whiteNameList = new String[] {
            ConfigurationClassPostProcessor.class.getName() + ".importAwareProcessor",
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry",
            ConfigurationClassPostProcessor.class.getName() + ".enhancedConfigurationProcessor" };

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        Map<String, BeanDefinition> postProcessorDefinitions = getBeanDefinitionsForType(
            beanFactory, BeanPostProcessor.class, BeanFactoryPostProcessor.class);
        beanFactory.registerSingleton(
            SofaModuleFrameworkConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT,
            postProcessorDefinitions);
    }

    private Map<String, BeanDefinition> getBeanDefinitionsForType(ConfigurableListableBeanFactory beanFactory,
                                                                  Class... types) {
        Map<String, BeanDefinition> map = new HashMap<>();
        for (Class type : types) {
            String[] beanNamesForType = beanFactory.getBeanNamesForType(type);
            List<String> beanDefinitionNames = Arrays.asList(beanFactory.getBeanDefinitionNames());
            for (String beanName : beanNamesForType) {
                if (notInWhiteNameList(beanName) && beanDefinitionNames.contains(beanName)) {
                    map.put(beanName, beanFactory.getBeanDefinition(beanName));
                }
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
