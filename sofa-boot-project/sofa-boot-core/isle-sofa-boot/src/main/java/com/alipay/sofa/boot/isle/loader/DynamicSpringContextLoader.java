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

import com.alipay.sofa.boot.context.ContextRefreshPostProcessor;
import com.alipay.sofa.boot.context.SofaDefaultListableBeanFactory;
import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.boot.context.SofaSpringContextSupport;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.startup.BeanStatCustomizer;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implement for {@link SpringContextLoader} to create sofa application context.
 *
 * @author linfengqi  2011-7-26
 * @author huzijie
 */
public class DynamicSpringContextLoader implements SpringContextLoader, InitializingBean {

    private static final Logger LOGGER = SofaBootLoggerFactory.getLogger(DynamicSpringContextLoader.class);

    protected final ConfigurableApplicationContext rootApplicationContext;

    private boolean                                allowBeanOverriding;

    private List<String>                           activeProfiles               = new ArrayList<>();

    private List<ContextRefreshPostProcessor>      contextRefreshPostProcessors = new ArrayList<>();

    private List<BeanStatCustomizer>               beanStatCustomizers          = new ArrayList<>();

    private boolean                                publishEventToParent;

    private SofaPostProcessorShareManager          sofaPostProcessorShareManager;

    public DynamicSpringContextLoader(ApplicationContext rootApplicationContext) {
        this.rootApplicationContext = (ConfigurableApplicationContext) rootApplicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rootApplicationContext, "rootApplicationContext must not be null");
        Assert.isInstanceOf(ConfigurableApplicationContext.class,
            "rootApplicationContext must be ConfigurableApplicationContext");
    }

    @Override
    public void loadSpringContext(DeploymentDescriptor deployment,
                                  ApplicationRuntimeModel application) {
        ClassLoader classLoader = deployment.getClassLoader();

        SofaDefaultListableBeanFactory beanFactory = SofaSpringContextSupport.createBeanFactory(classLoader, this::newInstanceBeanFactory);
        if (!CollectionUtils.isEmpty(beanStatCustomizers)) {
            AnnotationAwareOrderComparator.sort(beanStatCustomizers);
            beanStatCustomizers.forEach(beanFactory::addBeanStatCustomizer);
        }

        SofaGenericApplicationContext context = SofaSpringContextSupport.createApplicationContext(beanFactory, this::newInstanceApplicationContext);

        context.setId(deployment.getModuleName());
        if (!activeProfiles.isEmpty()) {
            context.getEnvironment().setActiveProfiles(StringUtils.toStringArray(activeProfiles));
        }
        context.setAllowBeanDefinitionOverriding(allowBeanOverriding);
        context.setPublishEventToParent(publishEventToParent);

        contextRefreshPostProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
        context.setPostProcessors(contextRefreshPostProcessors);

        ConfigurableApplicationContext parentContext = getSpringParentContext(deployment, application);
        context.setParent(parentContext);
        context.getEnvironment().setConversionService(parentContext.getEnvironment().getConversionService());

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(context);
        beanDefinitionReader.setValidating(true);
        beanDefinitionReader.setNamespaceAware(true);
        beanDefinitionReader.setBeanClassLoader(classLoader);
        beanDefinitionReader.setResourceLoader(context);

        loadBeanDefinitions(deployment, beanDefinitionReader);
        deployment.setApplicationContext(context);
        addPostProcessors(beanFactory);
    }

    protected SofaDefaultListableBeanFactory newInstanceBeanFactory() {
        return new SofaDefaultListableBeanFactory();
    }

    protected SofaGenericApplicationContext newInstanceApplicationContext(SofaDefaultListableBeanFactory beanFactory) {
        return new SofaGenericApplicationContext(beanFactory);
    }

    protected ConfigurableApplicationContext getSpringParentContext(DeploymentDescriptor deployment,
                                                                    ApplicationRuntimeModel application) {
        ConfigurableApplicationContext parentSpringContext = null;
        if (deployment.getSpringParent() != null) {
            String springParent = deployment.getSpringParent();

            if (StringUtils.hasText(springParent)) {
                DeploymentDescriptor parent = application.getDeploymentByName(springParent);
                if (parent != null) {
                    parentSpringContext = (ConfigurableApplicationContext) parent
                        .getApplicationContext();
                    if (parentSpringContext == null) {
                        LOGGER.warn("Module [{}]'s Spring-Parent [{}] is Null!",
                            deployment.getModuleName(), springParent);
                    }
                }
            }
        }
        return parentSpringContext == null ? rootApplicationContext : parentSpringContext;
    }

    protected void loadBeanDefinitions(DeploymentDescriptor deployment,
                                       BeanDefinitionReader beanDefinitionReader) {
        for (Map.Entry<String, Resource> entry : deployment.getSpringResources().entrySet()) {
            String fileName = entry.getKey();
            beanDefinitionReader.loadBeanDefinitions(entry.getValue());
            deployment.addInstalledSpringXml(fileName);
        }
    }

    protected void addPostProcessors(SofaDefaultListableBeanFactory beanFactory) {
        sofaPostProcessorShareManager.getRegisterSingletonMap().forEach(beanFactory::registerSingleton);
        sofaPostProcessorShareManager.getRegisterBeanDefinitionMap().forEach(beanFactory::registerBeanDefinition);
    }

    public boolean isAllowBeanOverriding() {
        return allowBeanOverriding;
    }

    public void setAllowBeanOverriding(boolean allowBeanOverriding) {
        this.allowBeanOverriding = allowBeanOverriding;
    }

    public List<String> getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public boolean isPublishEventToParent() {
        return publishEventToParent;
    }

    public void setPublishEventToParent(boolean publishEventToParent) {
        this.publishEventToParent = publishEventToParent;
    }

    public List<ContextRefreshPostProcessor> getContextRefreshPostProcessors() {
        return contextRefreshPostProcessors;
    }

    public void setContextRefreshPostProcessors(List<ContextRefreshPostProcessor> contextRefreshPostProcessors) {
        this.contextRefreshPostProcessors = contextRefreshPostProcessors;
    }

    public SofaPostProcessorShareManager getSofaPostProcessorShareManager() {
        return sofaPostProcessorShareManager;
    }

    public void setSofaPostProcessorShareManager(SofaPostProcessorShareManager sofaPostProcessorShareManager) {
        this.sofaPostProcessorShareManager = sofaPostProcessorShareManager;
    }

    public List<BeanStatCustomizer> getBeanStatCustomizers() {
        return beanStatCustomizers;
    }

    public void setBeanStatCustomizers(List<BeanStatCustomizer> beanStatCustomizers) {
        this.beanStatCustomizers = beanStatCustomizers;
    }
}
