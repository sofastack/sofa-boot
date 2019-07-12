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
package com.alipay.sofa.runtime.spring.parser;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.runtime.spring.async.AsyncInitBeanHolder;

/**
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncInitBeanDefinitionDecorator implements BeanDefinitionDecorator,
                                             SofaBootTagNameSupport {

    private static final String BEAN_LOAD_COST_FACTORY_CLASS = "com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory";
    private static final String GET_MODULE_NAME_METHOD       = "getModuleName";

    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition,
                                         ParserContext parserContext) {
        if (!Boolean.TRUE.toString().equalsIgnoreCase(((Attr) node).getValue())) {
            return definition;
        }

        String moduleName = getModuleName(parserContext);
        if (moduleName != null && moduleName.trim().length() > 0) {
            AsyncInitBeanHolder.registerAsyncInitBean(moduleName, definition.getBeanName(),
                ((AbstractBeanDefinition) definition.getBeanDefinition()).getInitMethodName());
        }
        return definition;
    }

    @Override
    public String supportTagName() {
        return "async-init";
    }

    private String getModuleName(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        if (registry instanceof AbstractApplicationContext) {
            BeanFactory beanFactory = ((AbstractApplicationContext) registry).getBeanFactory();
            if (isBeanLoadCostBeanFactory(beanFactory.getClass())) {
                return getModuleNameFromBeanFactory(beanFactory);
            }
        }

        if (isBeanLoadCostBeanFactory(registry.getClass())) {
            return getModuleNameFromBeanFactory(registry);
        }

        return SofaBootConstants.ROOT_APPLICATION_CONTEXT;
    }

    public static boolean isBeanLoadCostBeanFactory(Class factoryClass) {
        if (factoryClass == null) {
            return false;
        }
        if (BEAN_LOAD_COST_FACTORY_CLASS.equals(factoryClass.getName())) {
            return true;
        }

        if (!Object.class.equals(factoryClass)) {
            return isBeanLoadCostBeanFactory(factoryClass.getSuperclass());
        }

        return false;
    }

    public static String getModuleNameFromBeanFactory(Object factory) {
        try {
            return (String) factory.getClass().getMethod(GET_MODULE_NAME_METHOD).invoke(factory);
        } catch (Throwable e) {
            return SofaBootConstants.ROOT_APPLICATION_CONTEXT;
        }
    }
}