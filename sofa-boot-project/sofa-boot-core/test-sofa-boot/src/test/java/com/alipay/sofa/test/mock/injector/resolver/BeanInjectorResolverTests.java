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
package com.alipay.sofa.test.mock.injector.resolver;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.IsleDeploymentModel;
import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.definition.MockDefinition;
import com.alipay.sofa.test.mock.injector.definition.QualifierDefinition;
import com.alipay.sofa.test.mock.injector.definition.SpyDefinition;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import com.alipay.sofa.test.mock.injector.example.RealExampleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BeanInjectorResolver}.
 *
 * @author huzijie
 * @version BeanInjectorResolverTests.java, v 0.1 2023年08月21日 5:35 PM huzijie Exp $
 */
public class BeanInjectorResolverTests {

    @Test
    public void targetModuleExist() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TargetClass.class);
        IsleDeploymentModel isleDeploymentModel = mock(IsleDeploymentModel.class);
        when(isleDeploymentModel.getModuleApplicationContextMap()).thenReturn(
            Map.of("testModule", applicationContext));
        applicationContext.getBeanFactory().registerSingleton(
            ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME, isleDeploymentModel);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            "targetClass", null, "testModule", "exampleService", null, null, false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetClass targetClass = applicationContext.getBean(TargetClass.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void targetModuleNotExist() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), "targetClass", null, "testModule", "exampleService",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("Unable to find target module [testModule] when resolve injector");
    }

    @Test
    public void findTargetBeanByName() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            "targetClass", null, null, "exampleService", null, null, false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetClass targetClass = applicationContext.getBean(TargetClass.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void findTargetBeanByNameButNoBeanExist() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), "noExistBean", null, null, "exampleService",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("Unable to create bean injector to bean [noExistBean] target bean not exist");
    }

    @Test
    public void findTargetBeanByClass() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            null, ResolvableType.forClass(TargetClass.class), null, "exampleService", null, null,
            false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetClass targetClass = applicationContext.getBean(TargetClass.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void findTargetBeanByClassButNoBeanExist() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), null, ResolvableType.forClass(ExampleService.class), null, "exampleService",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("expected a single matching bean to injector but no bean found");
    }

    @Test
    public void findTargetBeanByClassButNoBeanExistCausedByScope() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(ScopeTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), null, ResolvableType.forClass(ExampleService.class), null, "exampleService",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("expected a single matching bean to injector but no bean found");
    }

    @Test
    public void findTargetBeanByClassButMultiBeanFound() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(MultiTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), null, ResolvableType.forClass(TargetClass.class), null, "exampleService",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("expected a single matching bean to injector but found [targetClassA, targetClassB]");
    }

    @Test
    public void findTargetBeanByClassWithQualifier() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            MultiTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            null, ResolvableType.forClass(TargetClass.class), null, "exampleService", null, null,
            false, null, QualifierDefinition.forElement(ReflectionUtils.findField(
                QualifierClass.class, "targetClassField")));
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetClass targetClass = applicationContext.getBean("targetClassA", TargetClass.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void findTargetBeanByClassWithPrimary() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            OnePrimaryTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            null, ResolvableType.forClass(TargetClass.class), null, "exampleService", null, null,
            false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetClass targetClass = applicationContext.getBean("targetClassA", TargetClass.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void findTargetBeanByClassButMultiPrimaryBeanFound() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(MultiPrimaryTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), null, ResolvableType.forClass(TargetClass.class), null, "exampleService",
                null, null, false, null, null);
        assertThatException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .isInstanceOf(NoUniqueBeanDefinitionException.class)
                .withMessageContaining("more than one 'primary' bean found among candidates: [[targetClassA, targetClassB]]");
    }

    @Test
    public void targetFieldCannotBeFound() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition =  new MockDefinition(
                ResolvableType.forClass(ExampleService.class), "targetClass", null, null, "exampleServiceA",
                null, null, false, null, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("Unable to inject target field to bean targetClass, can not find field exampleServiceA in class com.alipay.sofa.test.mock.injector.resolver.BeanInjectorResolverTests$TargetClass");
    }

    @Test
    public void jdkProxyBeanInject() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            JdkProxyTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            "targetClass", null, null, "exampleService", null, null, false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetInterface targetClass = applicationContext.getBean(TargetInterface.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void cglibProxyBeanInject() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            CglibProxyTargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new MockDefinition(ResolvableType.forClass(ExampleService.class),
            "targetClass", null, null, "exampleService", null, null, false, null, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        TargetInterface targetClass = applicationContext.getBean(TargetInterface.class);
        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isMock()).isTrue();
    }

    @Test
    public void spyTargetBeanWhenFieldIsNull() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);

        Definition definition = new SpyDefinition(ResolvableType.forClass(ExampleService.class),
                "targetClass", null, null, "exampleService", null, false, null);
        assertThatIllegalStateException().isThrownBy(() -> beanInjectorResolver.resolveStub(definition))
                .withMessageContaining("Unable to create spy to inject target field private com.alipay.sofa.test.mock.injector.example.ExampleService com.alipay.sofa.test.mock.injector.resolver.BeanInjectorResolverTests$TargetClass.exampleService when origin value is null");
    }

    @Test
    public void spyTargetBean() {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TargetClass.class);
        BeanInjectorResolver beanInjectorResolver = new BeanInjectorResolver(applicationContext);
        TargetClass targetClass = applicationContext.getBean(TargetClass.class);
        targetClass.setExampleService(new RealExampleService("test"));

        Definition definition = new SpyDefinition(ResolvableType.forClass(ExampleService.class),
            "targetClass", null, null, "exampleService", null, false, null);
        BeanInjectorStub stub = beanInjectorResolver.resolveStub(definition);
        stub.inject();

        assertThat(Mockito.mockingDetails(targetClass.getExampleService()).isSpy()).isTrue();
    }

    interface TargetInterface {

        ExampleService getExampleService();

    }

    @Configuration(value = "targetClass", proxyBeanMethods = false)
    static class TargetClass implements TargetInterface {

        private ExampleService exampleService;

        public ExampleService getExampleService() {
            return exampleService;
        }

        public void setExampleService(ExampleService exampleService) {
            this.exampleService = exampleService;
        }
    }

    @Configuration
    static class MultiTargetClass {

        @Bean
        public TargetClass targetClassA() {
            return new TargetClass();
        }

        @Bean
        public TargetClass targetClassB() {
            return new TargetClass();
        }
    }

    static class QualifierClass {

        @Qualifier("targetClassA")
        private TargetClass targetClassField;
    }

    @Configuration
    static class ScopeTargetClass {

        @Bean
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public TargetClass targetClass() {
            return new TargetClass();
        }
    }

    @Configuration
    static class OnePrimaryTargetClass {

        @Bean
        @Primary
        public TargetClass targetClassA() {
            return new TargetClass();
        }

        @Bean
        public TargetClass targetClassB() {
            return new TargetClass();
        }
    }

    @Configuration
    static class MultiPrimaryTargetClass {

        @Bean
        @Primary
        public TargetClass targetClassA() {
            return new TargetClass();
        }

        @Bean
        @Primary
        public TargetClass targetClassB() {
            return new TargetClass();
        }
    }

    @Configuration
    static class JdkProxyTargetClass {

        @Bean
        public TargetInterface targetClass() {
            return new TargetClass();
        }

        @Bean
        public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
            BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
            beanNameAutoProxyCreator.setBeanNames("targetClass");
            beanNameAutoProxyCreator.setProxyTargetClass(false);
            return beanNameAutoProxyCreator;
        }
    }

    @Configuration
    static class CglibProxyTargetClass {

        @Bean
        public TargetInterface targetClass() {
            return new TargetClass();
        }

        @Bean
        public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
            BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
            beanNameAutoProxyCreator.setBeanNames("targetClass");
            beanNameAutoProxyCreator.setProxyTargetClass(true);
            return beanNameAutoProxyCreator;
        }
    }
}
