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
package com.alipay.sofa.boot.context;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to create {@link SofaDefaultListableBeanFactory} and {@link SofaGenericApplicationContext}.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 * @since 4.0.0
 */
public class SofaSpringContextSupport {

    public static SofaDefaultListableBeanFactory createBeanFactory(ClassLoader beanClassLoader,
                                                                   Supplier<SofaDefaultListableBeanFactory> supplier) {
        SofaDefaultListableBeanFactory beanFactory = supplier.get();
        if (!(beanFactory.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
        }
        beanFactory.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
        beanFactory.setBeanClassLoader(beanClassLoader);
        beanFactory.addPropertyEditorRegistrar(registry -> {
            registry.registerCustomEditor(Class.class, new ClassEditor(beanClassLoader));
            registry.registerCustomEditor(Class[].class, new ClassArrayEditor(beanClassLoader));
        });
        CachedIntrospectionResults.acceptClassLoader(beanClassLoader);
        return beanFactory;
    }

    public static SofaGenericApplicationContext createApplicationContext(SofaDefaultListableBeanFactory beanFactory,
                                                                         Function<SofaDefaultListableBeanFactory, SofaGenericApplicationContext> function) {
        SofaGenericApplicationContext ctx = function.apply(beanFactory);
        ctx.setClassLoader(beanFactory.getBeanClassLoader());
        return ctx;
    }
}
