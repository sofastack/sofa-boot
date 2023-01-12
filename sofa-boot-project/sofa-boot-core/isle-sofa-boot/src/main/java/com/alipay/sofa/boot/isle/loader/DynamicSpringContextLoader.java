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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.context.ContextRefreshPostProcessor;
import com.alipay.sofa.boot.context.SofaDefaultListableBeanFactory;
import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.boot.context.SofaSpringContextSupport;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.loader.singleton.SingletonSofaPostProcessor;
import com.alipay.sofa.boot.log.SofaLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author linfengqi  2011-7-26
 */
public class DynamicSpringContextLoader implements SpringContextLoader, InitializingBean {

    protected final ConfigurableApplicationContext rootApplicationContext;

    private boolean allowBeanOverriding;

    private List<String> activeProfiles = new ArrayList<>();

    private List<ContextRefreshPostProcessor> contextRefreshPostProcessors = new ArrayList<>();

    private boolean publishEventToParent;

    public DynamicSpringContextLoader(ApplicationContext rootApplicationContext) {
        this.rootApplicationContext = (ConfigurableApplicationContext) rootApplicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rootApplicationContext, "rootApplicationContext must not be null");
        Assert.isInstanceOf(ConfigurableApplicationContext.class, "rootApplicationContext must be ConfigurableApplicationContext");
    }

    @Override
    public void loadSpringContext(DeploymentDescriptor deployment,
                                  ApplicationRuntimeModel application) {
        ClassLoader classLoader = deployment.getClassLoader();

        SofaDefaultListableBeanFactory beanFactory = SofaSpringContextSupport.createBeanFactory(classLoader, this::newInstanceBeanFactory);

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
                        SofaLogger.warn("Module [{}]'s Spring-Parent [{}] is Null!",
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
        Map<String, BeanDefinition> processors = (Map<String, BeanDefinition>) rootApplicationContext
                .getBean(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        for (Map.Entry<String, BeanDefinition> entry : processors.entrySet()) {
            if (!beanFactory.containsBeanDefinition(entry.getKey())) {
                Class<?> type = rootApplicationContext.getType(entry.getKey());
                if (type != null
                        && AnnotationUtils.findAnnotation(type, SingletonSofaPostProcessor.class) != null) {
                    // 复用单例
                    beanFactory.registerSingleton(entry.getKey(),
                            rootApplicationContext.getBean(entry.getKey()));
                } else {
                    // 注册 BeanDefinition
                    beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
                }
            }
        }
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
}
