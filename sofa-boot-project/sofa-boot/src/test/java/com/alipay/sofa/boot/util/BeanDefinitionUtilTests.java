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
package com.alipay.sofa.boot.util;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BeanDefinitionUtil}.
 *
 * @author huzijie
 * @version BeanDefinitionUtilTests.java, v 0.1 2023年04月03日 5:42 PM huzijie Exp $
 */
public class BeanDefinitionUtilTests {

    @Test
    public void fromBeanClass() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            SampleClass.class);
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isFalse();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Test
    public void fromBeanMethod() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TestBeanConfiguration.class);
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isTrue();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Test
    public void fromBeanDefinitionClass() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.registerBeanDefinition("beanA",
            new RootBeanDefinition(SampleClass.class));
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isFalse();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Test
    public void fromBeanDefinitionClassName() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClassName(SampleClass.class.getName());
        applicationContext.registerBeanDefinition("beanA", rootBeanDefinition);
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isFalse();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Test
    public void fromBeanDefinitionTargetType() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setTargetType(SampleClass.class);
        applicationContext.registerBeanDefinition("beanA", rootBeanDefinition);
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isFalse();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Test
    public void fromBeanDefinitionWithCglibProxy() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        ProxyFactory proxyFactory = new ProxyFactory(new SampleClass());
        Class<?> proxyClass = proxyFactory.getProxy().getClass();
        assertThat(proxyClass).isNotEqualTo(SampleClass.class);

        rootBeanDefinition.setBeanClass(proxyClass);
        applicationContext.registerBeanDefinition("beanA", rootBeanDefinition);
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition("beanA");
        assertThat(BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)).isFalse();
        assertThat(BeanDefinitionUtil.resolveBeanClassType(beanDefinition)).isEqualTo(
            SampleClass.class);
    }

    @Component("beanA")
    static class SampleClass {

    }

    @Configuration
    static class TestBeanConfiguration {

        @Bean
        public SampleClass beanA() {
            return new SampleClass();
        }
    }
}
