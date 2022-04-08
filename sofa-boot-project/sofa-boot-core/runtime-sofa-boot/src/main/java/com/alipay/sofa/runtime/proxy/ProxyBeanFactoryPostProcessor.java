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

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

/**
 *
 * @author ruoshan
 * @since 3.12.0
 */
public class ProxyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        boolean updateProxyBean = false;
        for (String beanName : beanFactory.getBeanNamesForType(ProxyFactoryBean.class, true, false)) {
            String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);
            if (beanFactory.containsBeanDefinition(transformedBeanName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(transformedBeanName);
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
                    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(3,
                        beanFactory);
                    updateProxyBean = true;
                }
            }
        }
        if (updateProxyBean) {
            // must clear metadata cache
            beanFactory.clearMetadataCache();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
