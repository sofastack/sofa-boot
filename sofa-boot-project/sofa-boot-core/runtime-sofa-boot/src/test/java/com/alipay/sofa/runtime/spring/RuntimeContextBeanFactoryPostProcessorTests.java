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
package com.alipay.sofa.runtime.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RuntimeContextBeanFactoryPostProcessor}.
 *
 * @author huzijie
 * @version RuntimeContextBeanFactoryPostProcessorTests.java, v 0.1 2023年04月19日 4:54 PM huzijie Exp $
 */
public class RuntimeContextBeanFactoryPostProcessorTests extends SofaRuntimeManagerTestBase {

    private GenericApplicationContext genericApplicationContext;

    @BeforeEach
    public void setUp() {
        genericApplicationContext = new AnnotationConfigApplicationContext();
        genericApplicationContext.registerBean(RuntimeContextBeanFactoryPostProcessor.class);
        genericApplicationContext.getBeanFactory().registerSingleton("sofaRuntimeManager",
            sofaRuntimeManager);
        genericApplicationContext.getBeanFactory().registerSingleton("bindingConverterFactory",
            bindingConverterFactory);
        genericApplicationContext.getBeanFactory().registerSingleton("bindingAdapterFactory",
            bindingAdapterFactory);

    }

    @Test
    public void parseAnnotationOnClass() {
        genericApplicationContext.refresh();
        Collection<BeanPostProcessor> beanPostProcessors = ((AbstractAutowireCapableBeanFactory)
                genericApplicationContext.getAutowireCapableBeanFactory()).getBeanPostProcessors();
        Collection<String> beanPostProcessorClassNames = beanPostProcessors.stream().map(beanPostProcessor -> beanPostProcessor.getClass().getName()).toList();
        assertThat(beanPostProcessorClassNames).contains(SofaRuntimeAwareProcessor.class.getName());
        assertThat(beanPostProcessorClassNames).contains(ClientFactoryAnnotationBeanPostProcessor.class.getName());
        assertThat(beanPostProcessorClassNames).contains(ReferenceAnnotationBeanPostProcessor.class.getName());
    }
}
