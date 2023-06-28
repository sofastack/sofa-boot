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
package com.alipay.sofa.boot.context.processor;

import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manager to found share PostProcessors.
 *
 * @author huzijie
 * Created by TomorJM on 2019-10-09.
 */
public class SofaPostProcessorShareManager implements BeanFactoryAware, InitializingBean {

    private static final String[]              WHITE_NAME_LIST                    = new String[] {
            ConfigurationClassPostProcessor.class.getName() + ".importAwareProcessor",
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry",
            ConfigurationClassPostProcessor.class.getName() + ".enhancedConfigurationProcessor" };

    private final Map<String, Object>          registerSingletonMap               = new HashMap<>();

    private final Map<String, BeanDefinition>  registerBeanDefinitionMap          = new HashMap<>();

    private ConfigurableListableBeanFactory    beanFactory;

    private boolean                            isShareParentContextPostProcessors = true;

    private List<SofaPostProcessorShareFilter> sofaPostProcessorShareFilters      = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!isShareParentContextPostProcessors) {
            return;
        }
        Assert.notNull(beanFactory, "beanFactory must not be null");
        Assert.notNull(registerSingletonMap, "registerSingletonMap must not be null");
        Assert.notNull(registerBeanDefinitionMap, "registerBeanDefinitionMap must not be null");
        Assert.notNull(sofaPostProcessorShareFilters,
            "sofaPostProcessorShareFilters must not be null");
        initShareSofaPostProcessors();
    }

    private void initShareSofaPostProcessors() {
        Set<String> beanNames = new HashSet<>();
        Set<String> allBeanDefinitionNames = new HashSet<>(Arrays.asList(beanFactory
            .getBeanDefinitionNames()));

        String[] eanFactoryPostProcessors = beanFactory.getBeanNamesForType(
            BeanFactoryPostProcessor.class, true, false);
        String[] beanPostProcessors = beanFactory.getBeanNamesForType(BeanPostProcessor.class,
            true, false);
        beanNames.addAll(Arrays.asList(eanFactoryPostProcessors));
        beanNames.addAll(Arrays.asList(beanPostProcessors));

        for (String beanName : beanNames) {
            if (notInWhiteNameList(beanName) && allBeanDefinitionNames.contains(beanName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                Class<?> clazz = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
                if (clazz == null) {
                    continue;
                }
                if (shouldBeanShare(beanName, clazz)) {
                    if (shouldBeanSingleton(beanName, clazz)) {
                        registerSingletonMap.put(beanName, beanFactory.getBean(beanName));
                    } else {
                        registerBeanDefinitionMap.put(beanName, beanDefinition);
                    }
                }
            }
        }
    }

    private boolean shouldBeanShare(String beanName, Class<?> clazz) {
        if (AnnotationUtils.findAnnotation(clazz, UnshareSofaPostProcessor.class) != null) {
            return false;
        }
        for (SofaPostProcessorShareFilter filter : sofaPostProcessorShareFilters) {
            if (filter.skipShareByBeanName(beanName) || filter.skipShareByClass(clazz)) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldBeanSingleton(String beanName, Class<?> clazz) {
        if (AnnotationUtils.findAnnotation(clazz, SingletonSofaPostProcessor.class) != null) {
            return true;
        }
        for (SofaPostProcessorShareFilter filter : sofaPostProcessorShareFilters) {
            if (filter.useSingletonByBeanName(beanName) || filter.useSingletonByClass(clazz)) {
                return true;
            }
        }
        return false;
    }

    private boolean notInWhiteNameList(String beanName) {
        for (String whiteName : WHITE_NAME_LIST) {
            if (whiteName.equals(beanName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isShareParentContextPostProcessors() {
        return isShareParentContextPostProcessors;
    }

    public void setShareParentContextPostProcessors(boolean shareParentContextPostProcessors) {
        isShareParentContextPostProcessors = shareParentContextPostProcessors;
    }

    public List<SofaPostProcessorShareFilter> getSofaPostProcessorShareFilters() {
        return sofaPostProcessorShareFilters;
    }

    public void setSofaPostProcessorShareFilters(List<SofaPostProcessorShareFilter> sofaPostProcessorShareFilters) {
        this.sofaPostProcessorShareFilters = sofaPostProcessorShareFilters;
    }

    public Map<String, Object> getRegisterSingletonMap() {
        return registerSingletonMap;
    }

    public Map<String, BeanDefinition> getRegisterBeanDefinitionMap() {
        return registerBeanDefinitionMap;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
            "beanFactory must be ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
