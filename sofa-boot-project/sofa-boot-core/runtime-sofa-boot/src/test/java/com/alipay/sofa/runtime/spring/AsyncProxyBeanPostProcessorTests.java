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

import com.alipay.sofa.runtime.async.AsyncInitMethodManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AsyncProxyBeanPostProcessor}.
 *
 * @author huzijie
 * @version AsyncProxyBeanPostProcessorTests.java, v 0.1 2023年04月10日 4:44 PM huzijie Exp $
 */
public class AsyncProxyBeanPostProcessorTests {

    private GenericApplicationContext genericApplicationContext;

    @BeforeEach
    public void setUp() {
        genericApplicationContext = new AnnotationConfigApplicationContext();
    }

    @Test
    public void registerAsyncBeans() {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(AsyncClass.class);
        rootBeanDefinition.setAttribute(ASYNC_INIT_METHOD_NAME, "init");
        genericApplicationContext.registerBean(AsyncProxyBeanPostProcessor.class);
        genericApplicationContext.registerBean(AsyncInitMethodManager.class);
        genericApplicationContext.registerBeanDefinition("bean", rootBeanDefinition);
        genericApplicationContext.refresh();

        Object object = genericApplicationContext.getBean("bean");
        assertThat(AopUtils.isCglibProxy(object)).isTrue();
    }

    @Test
    public void wrapAsyncBeans() throws Exception {
        AsyncInitMethodManager asyncInitMethodManager = new AsyncInitMethodManager();
        AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor = new AsyncProxyBeanPostProcessor(
            asyncInitMethodManager);
        asyncProxyBeanPostProcessor.setBeanFactory(genericApplicationContext.getBeanFactory());
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(AsyncClass.class);
        rootBeanDefinition.setAttribute(ASYNC_INIT_METHOD_NAME, "init");
        genericApplicationContext.getBeanFactory().registerSingleton("asyncProxyBeanPostProcessor",
            asyncProxyBeanPostProcessor);
        genericApplicationContext.registerBeanDefinition("bean", rootBeanDefinition);
        genericApplicationContext.refresh();
        asyncProxyBeanPostProcessor.afterPropertiesSet();

        String initMethodName = asyncInitMethodManager.findAsyncInitMethod(
            genericApplicationContext.getBeanFactory(), "bean");
        assertThat(initMethodName).isEqualTo("init");

    }

    static class AsyncClass {

        public void init() {

        }
    }

}
