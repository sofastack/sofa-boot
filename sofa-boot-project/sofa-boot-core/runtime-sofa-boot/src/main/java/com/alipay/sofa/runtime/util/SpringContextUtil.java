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

import com.alipay.sofa.boot.startup.BeanStatExtension;
import com.alipay.sofa.runtime.factory.BeanLoadCostBeanFactory;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.ResourceLoader;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 28/06/2022
 */
public class SpringContextUtil {
    /**
     * @param beanLoadCost 记录 bean 耗时的最低 threshold
     * @param factoryId BeanFactory 的 id
     * @param beanClassLoader 加载 bean 的类加载器
     * @param beanStatExtension 可以是 null
     */
    public static BeanLoadCostBeanFactory createBeanFactory(long beanLoadCost, String factoryId, ClassLoader beanClassLoader,
                                                            BeanStatExtension beanStatExtension) {
        BeanLoadCostBeanFactory beanFactory = new BeanLoadCostBeanFactory(beanLoadCost, factoryId, beanStatExtension);
        beanFactory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
        beanFactory.setParameterNameDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        beanFactory.setBeanClassLoader(beanClassLoader);
        beanFactory.addPropertyEditorRegistrar(registry -> {
            registry.registerCustomEditor(Class.class, new ClassEditor(beanClassLoader));
            registry.registerCustomEditor(Class[].class, new ClassArrayEditor(beanClassLoader));
        });
        return beanFactory;
    }

    public static BeanLoadCostBeanFactory createBeanFactory(String factoryId) {
        return createBeanFactory(100, factoryId, SpringContextUtil.class.getClassLoader(), null);
    }

    public static GenericApplicationContext createApplicationContext(boolean allowBeanOverriding,
                                                                     String contextId,
                                                                     ClassLoader resourceLoader,
                                                                     ApplicationContextCreator creator) {
        GenericApplicationContext ctx = creator.create();
        ctx.setId(contextId);
        ctx.setClassLoader(resourceLoader);
        ctx.setAllowBeanDefinitionOverriding(allowBeanOverriding);
        return ctx;
    }

    public static XmlBeanDefinitionReader createBeanDefinitionReader(ClassLoader beanClassLoader,
                                                                     ResourceLoader resourceLoader,
                                                                     BeanDefinitionReaderCreator creator) {
        XmlBeanDefinitionReader beanDefinitionReader = creator.create();
        beanDefinitionReader.setNamespaceAware(true);
        beanDefinitionReader.setBeanClassLoader(beanClassLoader);
        beanDefinitionReader.setResourceLoader(resourceLoader);
        return beanDefinitionReader;
    }
}
