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
package com.alipay.sofa.runtime.proxy;

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * Implementation of {@link BeanDefinitionRegistryPostProcessor} to update {@link ProxyFactoryBean} to avoid early init.
 *
 * <p> This bean must implement BeanDefinitionRegistryPostProcessor and use the highest precedence, otherwise other
 * BeanDefinitionRegistryPostProcessor may trigger construct inject then cause ProxyFactoryBean early init.
 *
 * @author ruoshan
 * @since 3.12.0
 */
@SingletonSofaPostProcessor
public class ProxyBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor,
                                          PriorityOrdered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                                                                                  throws BeansException {
        boolean updateProxyBean = false;
        for (String beanName : registry.getBeanDefinitionNames()) {
            String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);
            if (registry.containsBeanDefinition(transformedBeanName)) {
                BeanDefinition beanDefinition = registry.getBeanDefinition(transformedBeanName);
                if (ProxyFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())) {
                    beanDefinition.setBeanClassName(SofaProxyFactoryBean.class.getName());
                    Object proxyInterfaces = beanDefinition.getPropertyValues().get(
                        "proxyInterfaces");
                    if (proxyInterfaces == null) {
                        proxyInterfaces = beanDefinition.getPropertyValues().get("interfaces");
                    }
                    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,
                        proxyInterfaces);
                    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1,
                        beanDefinition.getPropertyValues().get("targetName"));
                    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(2,
                        beanDefinition.getPropertyValues().get("targetClass"));
                    // must be true
                    if (registry instanceof ConfigurableListableBeanFactory) {
                        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(3,
                            registry);
                    }
                    updateProxyBean = true;
                }
            }
        }
        if (updateProxyBean && registry instanceof ConfigurableListableBeanFactory) {
            // must clear metadata cache
            ((ConfigurableListableBeanFactory) registry).clearMetadataCache();
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {

    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
