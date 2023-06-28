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
package com.alipay.sofa.boot.context.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaPostProcessorShareManager}.
 *
 * @author huzijie
 * @version SofaPostProcessorShareManagerTests.java, v 0.1 2023年02月01日 12:24 PM huzijie Exp $
 */
public class SofaPostProcessorShareManagerTests {

    @Test
    public void shareContextPostProcessors() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("manager", SofaPostProcessorShareManager.class);
        context.registerBean("beanPostProcessor", NormalBeanPostProcessor.class);
        context.registerBean("beanFactoryPostProcessor", NormalBeanFactoryPostProcessor.class);
        context.refresh();
        SofaPostProcessorShareManager manager = context
            .getBean(SofaPostProcessorShareManager.class);
        assertThat(manager).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanPostProcessor")).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanFactoryPostProcessor"))
            .isNotNull();
        assertThat(manager.getRegisterSingletonMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanFactoryPostProcessor")).isNull();
    }

    @Test
    public void unShareContextPostProcessorsWithAnnotation() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("manager", SofaPostProcessorShareManager.class);
        context.registerBean("beanPostProcessor", NoShareBeanPostProcessor.class);
        context.registerBean("beanFactoryPostProcessor", NoShareBeanFactoryPostProcessor.class);
        context.refresh();
        SofaPostProcessorShareManager manager = context
            .getBean(SofaPostProcessorShareManager.class);
        assertThat(manager).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanFactoryPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanFactoryPostProcessor")).isNull();
    }

    @Test
    public void shareSingletonContextPostProcessorsWithAnnotation() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("manager", SofaPostProcessorShareManager.class);
        context.registerBean("beanPostProcessor", SingletonBeanPostProcessor.class);
        context.registerBean("beanFactoryPostProcessor", SingletonBeanFactoryPostProcessor.class);
        context.refresh();
        SofaPostProcessorShareManager manager = context
            .getBean(SofaPostProcessorShareManager.class);
        assertThat(manager).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanFactoryPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanPostProcessor")).isNotNull();
        assertThat(manager.getRegisterSingletonMap().get("beanFactoryPostProcessor")).isNotNull();
    }

    @Test
    public void unShareContextPostProcessorsWithFilter() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        RootBeanDefinition beanDefinition = new RootBeanDefinition(
            SofaPostProcessorShareManager.class);
        beanDefinition.setAutowireMode(2);
        context.registerBeanDefinition("manager", beanDefinition);
        context.registerBean("sofaPostProcessorShareFilters",
            UnShareSofaPostProcessorShareFilter.class);
        context.registerBean("beanPostProcessor", NormalBeanPostProcessor.class);
        context.registerBean("beanFactoryPostProcessor", NormalBeanFactoryPostProcessor.class);
        context.refresh();
        SofaPostProcessorShareManager manager = context
            .getBean(SofaPostProcessorShareManager.class);
        assertThat(manager).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanFactoryPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanFactoryPostProcessor")).isNull();
    }

    @Test
    public void singletonContextPostProcessorsWithFilter() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        RootBeanDefinition beanDefinition = new RootBeanDefinition(
            SofaPostProcessorShareManager.class);
        beanDefinition.setAutowireMode(2);
        context.registerBeanDefinition("manager", beanDefinition);
        context.registerBean("sofaPostProcessorShareFilters",
            SingletonSofaPostProcessorShareFilter.class);
        context.registerBean("beanPostProcessor", NormalBeanPostProcessor.class);
        context.registerBean("beanFactoryPostProcessor", NormalBeanFactoryPostProcessor.class);
        context.refresh();
        SofaPostProcessorShareManager manager = context
            .getBean(SofaPostProcessorShareManager.class);
        assertThat(manager).isNotNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanPostProcessor")).isNull();
        assertThat(manager.getRegisterBeanDefinitionMap().get("beanFactoryPostProcessor")).isNull();
        assertThat(manager.getRegisterSingletonMap().get("beanPostProcessor")).isNotNull();
        assertThat(manager.getRegisterSingletonMap().get("beanFactoryPostProcessor")).isNotNull();
    }

    static class NormalBeanPostProcessor implements BeanPostProcessor {
    }

    static class NormalBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                       throws BeansException {

        }
    }

    @UnshareSofaPostProcessor
    static class NoShareBeanPostProcessor implements BeanPostProcessor {
    }

    @UnshareSofaPostProcessor
    static class NoShareBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                       throws BeansException {

        }
    }

    @SingletonSofaPostProcessor
    static class SingletonBeanPostProcessor implements BeanPostProcessor {
    }

    @SingletonSofaPostProcessor
    static class SingletonBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                       throws BeansException {

        }
    }

    static class UnShareSofaPostProcessorShareFilter implements SofaPostProcessorShareFilter {

        @Override
        public boolean skipShareByClass(Class<?> clazz) {
            return clazz == NormalBeanPostProcessor.class;
        }

        @Override
        public boolean skipShareByBeanName(String beanName) {
            return beanName.equals("beanFactoryPostProcessor");
        }
    }

    static class SingletonSofaPostProcessorShareFilter implements SofaPostProcessorShareFilter {

        @Override
        public boolean useSingletonByClass(Class<?> clazz) {
            return clazz == NormalBeanPostProcessor.class;
        }

        @Override
        public boolean useSingletonByBeanName(String beanName) {
            return beanName.equals("beanFactoryPostProcessor");
        }
    }
}
