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

import com.alipay.sofa.runtime.api.annotation.SofaAsyncInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AsyncInitBeanFactoryPostProcessor}.
 *
 * @author huzijie
 * @version AsyncInitBeanFactoryPostProcessorTests.java, v 0.1 2023年04月10日 4:29 PM huzijie Exp $
 */
public class AsyncInitBeanFactoryPostProcessorTests {

    private GenericApplicationContext genericApplicationContext;

    @BeforeEach
    public void setUp() {
        genericApplicationContext = new AnnotationConfigApplicationContext();
        genericApplicationContext.registerBean(AsyncInitBeanFactoryPostProcessor.class);
    }

    @Test
    public void parseAnnotationOnClass() {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(NormalClass.class);
        rootBeanDefinition.setInitMethodName("init");
        genericApplicationContext.registerBeanDefinition("bean", rootBeanDefinition);
        genericApplicationContext.refresh();

        assertThat(rootBeanDefinition.getAttribute(ASYNC_INIT_METHOD_NAME)).isEqualTo("init");
    }

    @Test
    public void parseAnnotationOnMethod() {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(NormalMethodClass.class);
        genericApplicationContext.registerBeanDefinition("bean", rootBeanDefinition);
        genericApplicationContext.refresh();

        BeanDefinition beanDefinition = genericApplicationContext.getBeanFactory()
            .getBeanDefinition("normalClass");

        assertThat(beanDefinition.getAttribute(ASYNC_INIT_METHOD_NAME)).isEqualTo("init");
    }

    @Test
    public void parseAnnotationOnClassWithFalse() {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(NormalFalseClass.class);
        rootBeanDefinition.setInitMethodName("init");
        genericApplicationContext.registerBeanDefinition("bean", rootBeanDefinition);
        genericApplicationContext.refresh();

        assertThat(rootBeanDefinition.getAttribute(ASYNC_INIT_METHOD_NAME)).isNull();
    }

    @SofaAsyncInit
    static class NormalClass {

        public void init() {

        }
    }

    @SofaAsyncInit(value = false)
    static class NormalFalseClass {

        public void init() {

        }
    }

    @Component
    static class NormalMethodClass {

        @Bean(initMethod = "init")
        @SofaAsyncInit
        public NormalClass normalClass() {
            return new NormalClass();
        }
    }
}
