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
package com.alipay.sofa.runtime.util;

import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.ResourceLoader;

import java.util.function.Supplier;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 28/06/2022
 */
public class SofaSpringContextUtil {
    /**
     * @param beanClassLoader 加载 bean 的类加载器
     * @param beanFactoryCreator 提供初始 BeanFactory 的创建器
     */
    public static DefaultListableBeanFactory createBeanFactory(ClassLoader beanClassLoader, Supplier<DefaultListableBeanFactory> beanFactoryCreator) {
        DefaultListableBeanFactory beanFactory = beanFactoryCreator.get();
        if (!(beanFactory.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
        }
        beanFactory.setParameterNameDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        beanFactory.setBeanClassLoader(beanClassLoader);
        beanFactory.addPropertyEditorRegistrar(registry -> {
            registry.registerCustomEditor(Class.class, new ClassEditor(beanClassLoader));
            registry.registerCustomEditor(Class[].class, new ClassArrayEditor(beanClassLoader));
        });
        return beanFactory;
    }

    /**
     * @param allowBeanOverriding 是否允许 bean 覆盖
     * @param contextId Spring 上下文的 unique id
     * @param resourceLoader 资源文件的类加载器
     * @param contextCreator 提供初始上下文的创建器
     */
    public static GenericApplicationContext createApplicationContext(boolean allowBeanOverriding,
                                                                     String contextId,
                                                                     ClassLoader resourceLoader,
                                                                     Supplier<GenericApplicationContext> contextCreator) {
        GenericApplicationContext ctx = contextCreator.get();
        ctx.setId(contextId);
        ctx.setClassLoader(resourceLoader);
        ctx.setAllowBeanDefinitionOverriding(allowBeanOverriding);
        return ctx;
    }

    /**
     * @param beanClassLoader 加载 bean 的类加载器
     * @param resourceLoader 资源加载器
     * @param definitionReaderCreator 提供初始 BeanDefinitionReader 的创建器
     */
    public static XmlBeanDefinitionReader createBeanDefinitionReader(ClassLoader beanClassLoader,
                                                                     ResourceLoader resourceLoader,
                                                                     Supplier<XmlBeanDefinitionReader> definitionReaderCreator) {
        XmlBeanDefinitionReader beanDefinitionReader = definitionReaderCreator.get();
        beanDefinitionReader.setNamespaceAware(true);
        beanDefinitionReader.setBeanClassLoader(beanClassLoader);
        beanDefinitionReader.setResourceLoader(resourceLoader);
        return beanDefinitionReader;
    }
}
