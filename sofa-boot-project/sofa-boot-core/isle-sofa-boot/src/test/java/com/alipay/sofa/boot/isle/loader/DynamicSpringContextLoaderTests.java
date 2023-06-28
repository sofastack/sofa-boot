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
package com.alipay.sofa.boot.isle.loader;

import com.alipay.sofa.boot.context.SofaDefaultListableBeanFactory;
import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.boot.context.processor.SofaPostProcessorShareManager;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.MockDeploymentDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.metrics.ApplicationStartup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DynamicSpringContextLoader}.
 *
 * @author huzijie
 * @version DynamicSpringContextLoaderTests.java, v 0.1 2023年04月07日 11:20 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class DynamicSpringContextLoaderTests {

    private DynamicSpringContextLoader           dynamicSpringContextLoader;

    private final MockDeploymentDescriptor       mockDeploymentDescriptor = new MockDeploymentDescriptor(
                                                                              "test");

    private final ConfigurableApplicationContext mockApplicationContext   = new GenericApplicationContext();

    @Mock
    private ApplicationRuntimeModel              mockApplicationRuntimeModel;

    @Mock
    private BeanDefinitionReader                 mockBeanDefinitionReader;

    @BeforeEach
    public void setUp() {
        dynamicSpringContextLoader = new DynamicSpringContextLoader(mockApplicationContext);
        dynamicSpringContextLoader.setAllowBeanOverriding(false);
        dynamicSpringContextLoader.setActiveProfiles(new ArrayList<>());
        dynamicSpringContextLoader.setPublishEventToParent(false);
        dynamicSpringContextLoader.setContextRefreshInterceptors(new ArrayList<>());
        dynamicSpringContextLoader
            .setSofaPostProcessorShareManager(new SofaPostProcessorShareManager());
    }

    @Test
    void loadSpringContext() {
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        dynamicSpringContextLoader.setActiveProfiles(List.of("test"));
        dynamicSpringContextLoader.setAllowBeanOverriding(true);
        dynamicSpringContextLoader.setPublishEventToParent(false);
        dynamicSpringContextLoader.setContextRefreshInterceptors(Collections.emptyList());
        dynamicSpringContextLoader.setSofaPostProcessorShareManager(null);
        dynamicSpringContextLoader.setStartupReporter(null);

        dynamicSpringContextLoader.loadSpringContext(mockDeploymentDescriptor, application);

        assertThat(dynamicSpringContextLoader.getContextRefreshInterceptors()).isEmpty();
        assertThat(dynamicSpringContextLoader.getSofaPostProcessorShareManager()).isNull();

        GenericApplicationContext applicationContext = (GenericApplicationContext) mockDeploymentDescriptor
            .getApplicationContext();
        assertThat(applicationContext).isInstanceOf(SofaGenericApplicationContext.class);
        assertThat(applicationContext.getId()).isEqualTo("test");
        assertThat(applicationContext.getApplicationStartup())
            .isEqualTo(ApplicationStartup.DEFAULT);
        assertThat(applicationContext.getParent()).isEqualTo(mockApplicationContext);
        assertThat(applicationContext.getEnvironment().getActiveProfiles()).containsOnly("test");

        applicationContext.refresh();
        DefaultListableBeanFactory autowireCapableBeanFactory = (DefaultListableBeanFactory) applicationContext
            .getAutowireCapableBeanFactory();
        assertThat(autowireCapableBeanFactory.isAllowBeanDefinitionOverriding()).isTrue();
        assertThat(autowireCapableBeanFactory).isInstanceOf(SofaDefaultListableBeanFactory.class);
    }

    @Test
    public void getSpringParentContext() {
        mockDeploymentDescriptor.setSpringParent("parentModuleName");
        when(mockApplicationRuntimeModel.getDeploymentByName("parentModuleName")).thenReturn(
            mockDeploymentDescriptor);
        mockDeploymentDescriptor.setApplicationContext(mockApplicationContext);
        ApplicationContext parentContext = dynamicSpringContextLoader.getSpringParentContext(
            mockDeploymentDescriptor, mockApplicationRuntimeModel);
        assertThat(parentContext).isEqualTo(mockApplicationContext);
    }

    @Test
    public void getSpringParentContextWithNullParentContext() {
        mockDeploymentDescriptor.setSpringParent("parentModuleName");
        when(mockApplicationRuntimeModel.getDeploymentByName("parentModuleName")).thenReturn(
            mockDeploymentDescriptor);
        mockDeploymentDescriptor.setApplicationContext(null);
        ApplicationContext parentContext = dynamicSpringContextLoader.getSpringParentContext(
            mockDeploymentDescriptor, mockApplicationRuntimeModel);
        assertThat(parentContext).isEqualTo(dynamicSpringContextLoader.rootApplicationContext);
    }

    @Test
    public void loadBeanDefinitions() {
        Resource mockResource = mock(Resource.class);
        mockDeploymentDescriptor.setSpringResources(Collections.singletonMap("test", mockResource));
        dynamicSpringContextLoader.loadBeanDefinitions(mockDeploymentDescriptor,
            mockBeanDefinitionReader);
        assertThat(mockDeploymentDescriptor.getInstalledSpringXml()).containsExactly("test");
    }

    @Test
    public void addPostProcessors() {
        SofaDefaultListableBeanFactory beanFactory = new SofaDefaultListableBeanFactory();
        SofaPostProcessorShareManager shareManager = mock(SofaPostProcessorShareManager.class);
        dynamicSpringContextLoader.setSofaPostProcessorShareManager(shareManager);
        assertThat(dynamicSpringContextLoader.getSofaPostProcessorShareManager()).isEqualTo(
            shareManager);
        Map<String, Object> objectMap = Map.of("beanA", new Object());
        Map<String, BeanDefinition> beanDefinitionMap = Map.of("beanB", new RootBeanDefinition());
        when(shareManager.getRegisterSingletonMap()).thenReturn(objectMap);
        when(shareManager.getRegisterBeanDefinitionMap()).thenReturn(beanDefinitionMap);
        dynamicSpringContextLoader.addPostProcessors(beanFactory);
        assertThat(beanFactory.containsBean("beanA")).isTrue();
        assertThat(beanFactory.containsBean("beanB")).isTrue();
    }

}
