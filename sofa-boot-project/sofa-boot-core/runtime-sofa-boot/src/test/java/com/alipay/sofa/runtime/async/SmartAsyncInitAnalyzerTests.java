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
package com.alipay.sofa.runtime.async;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_DISABLED_ATTRIBUTE;
import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SmartAsyncInitAnalyzer}.
 *
 * @author OpenAI
 */
public class SmartAsyncInitAnalyzerTests {

    private final SmartAsyncInitAnalyzer analyzer = new SmartAsyncInitAnalyzer();

    @Test
    void conservativeModeOnlyAcceptsStatelessBeanWithoutDependencies() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        register(beanFactory, "candidate", StatelessBean.class, "init");
        register(beanFactory, "noInit", StatelessBean.class, null);
        register(beanFactory, "stateful", StatefulBean.class, "init");
        register(beanFactory, "constructorDependency", ConstructorDependencyBean.class, "init");
        register(beanFactory, "configurationProperties", PropertiesBean.class, "init");

        List<String> candidates = analyzer.analyzeAsyncCandidates(beanFactory,
            AsyncInitAutoMode.CONSERVATIVE);

        assertThat(candidates).containsExactly("candidate");
    }

    @Test
    void aggressiveModeRejectsPropertyAndRegisteredDependencies() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        register(beanFactory, "candidate", ImmutableBean.class, "init");
        RootBeanDefinition propertyDependency = register(beanFactory, "propertyDependency",
            StatelessBean.class, "init");
        propertyDependency.getPropertyValues().add("dependency",
            new RuntimeBeanReference("dependency"));
        register(beanFactory, "registeredDependency", StatelessBean.class, "init");
        beanFactory.registerDependentBean("dependency", "registeredDependency");

        List<String> candidates = analyzer.analyzeAsyncCandidates(beanFactory,
            AsyncInitAutoMode.AGGRESSIVE);

        assertThat(candidates).containsExactly("candidate");
    }

    @Test
    void skipsExplicitAsyncAndExplicitDisabledBeans() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        RootBeanDefinition explicitAsync = register(beanFactory, "explicitAsync",
            StatelessBean.class, "init");
        explicitAsync.setAttribute(ASYNC_INIT_METHOD_NAME, "init");
        RootBeanDefinition disabled = register(beanFactory, "disabled", StatelessBean.class, "init");
        disabled.setAttribute(ASYNC_INIT_DISABLED_ATTRIBUTE, true);

        List<String> candidates = analyzer.analyzeAsyncCandidates(beanFactory,
            AsyncInitAutoMode.CONSERVATIVE);

        assertThat(candidates).isEmpty();
    }

    @Test
    void offModeReturnsNoCandidates() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        register(beanFactory, "candidate", StatelessBean.class, "init");

        assertThat(analyzer.analyzeAsyncCandidates(beanFactory, AsyncInitAutoMode.OFF)).isEmpty();
    }

    private RootBeanDefinition register(DefaultListableBeanFactory beanFactory, String beanName,
                                        Class<?> beanClass, String initMethodName) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
        beanDefinition.setInitMethodName(initMethodName);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return beanDefinition;
    }

    static class StatelessBean {

        public void init() {
        }
    }

    static class StatefulBean {

        private String value;

        public void init() {
        }
    }

    static class ConstructorDependencyBean {

        ConstructorDependencyBean(StatelessBean dependency) {
        }

        public void init() {
        }
    }

    static class ImmutableBean {

        private final int value = 1;

        public void init() {
        }
    }

    @ConfigurationProperties("test")
    static class PropertiesBean {

        public void init() {
        }
    }
}
