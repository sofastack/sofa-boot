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
package com.alipay.sofa.isle.loader;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.startup.BeanStatExtension;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.runtime.context.SofaApplicationContext;
import com.alipay.sofa.runtime.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.util.SofaSpringContextUtil;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 *
 * @author linfengqi  2011-7-26
 */
public class DynamicSpringContextLoader implements SpringContextLoader {
    protected final ConfigurableApplicationContext rootApplicationContext;

    public DynamicSpringContextLoader(ApplicationContext applicationContext) {
        this.rootApplicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void loadSpringContext(DeploymentDescriptor deployment,
                                  ApplicationRuntimeModel application) throws Exception {
        SofaModuleProperties sofaModuleProperties = rootApplicationContext.getBean(SofaModuleProperties.class);
        String moduleName= deployment.getModuleName();
        ClassLoader moduleClassLoader = deployment.getClassLoader();
        CachedIntrospectionResults.acceptClassLoader(moduleClassLoader);

        // 创建上下文
        DefaultListableBeanFactory beanFactory = SofaSpringContextUtil.createBeanFactory(moduleClassLoader,
                () -> createBeanFactory(sofaModuleProperties.getBeanLoadCost(), moduleName));
        GenericApplicationContext ctx = SofaSpringContextUtil.createApplicationContext(
                sofaModuleProperties.isAllowBeanDefinitionOverriding(),
                moduleName, moduleClassLoader, () -> createApplicationContext(sofaModuleProperties, beanFactory));
        XmlBeanDefinitionReader beanDefinitionReader = SofaSpringContextUtil.createBeanDefinitionReader(moduleClassLoader,
                ctx, () -> createXmlBeanDefinitionReader(ctx));

        // 设置 SOFA 模块相关的内容
        String activeProfiles = sofaModuleProperties.getActiveProfiles();
        if (StringUtils.hasText(activeProfiles)) {
            String[] profiles = activeProfiles.split(SofaBootConstants.PROFILE_SEPARATOR);
            ctx.getEnvironment().setActiveProfiles(profiles);
        }
        setUpParentSpringContext(ctx, deployment, application);
        deployment.setApplicationContext(ctx);

        // 加载 bean 定义，添加 BPP
        loadBeanDefinitions(deployment, beanDefinitionReader);
        addPostProcessors(beanFactory);
    }

    protected DefaultListableBeanFactory createBeanFactory(long beanLoadCost, String factoryId) {
        return new BeanLoadCostBeanFactory(beanLoadCost, factoryId, getBeanStatExtension());
    }

    protected XmlBeanDefinitionReader createXmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(registry);
        beanDefinitionReader.setValidating(true);
        return beanDefinitionReader;
    }

    protected GenericApplicationContext createApplicationContext(SofaModuleProperties sofaModuleProperties,
                                                                 DefaultListableBeanFactory beanFactory) {
        return sofaModuleProperties.isPublishEventToParent() ? new GenericApplicationContext(
            beanFactory) : new SofaApplicationContext(beanFactory);
    }

    protected void loadBeanDefinitions(DeploymentDescriptor deployment,
                                       XmlBeanDefinitionReader beanDefinitionReader) {
        for (Map.Entry<String, Resource> entry : deployment.getSpringResources().entrySet()) {
            String fileName = entry.getKey();
            beanDefinitionReader.loadBeanDefinitions(entry.getValue());
            deployment.addInstalledSpringXml(fileName);
        }
    }

    private void setUpParentSpringContext(GenericApplicationContext applicationContext,
                                          DeploymentDescriptor deployment,
                                          ApplicationRuntimeModel application) {
        ConfigurableApplicationContext parentSpringContext = getSpringParent(deployment,
            application);
        if (parentSpringContext != null) {
            applicationContext.setParent(parentSpringContext);
            applicationContext.getEnvironment().setConversionService(
                parentSpringContext.getEnvironment().getConversionService());
        } else {
            applicationContext.setParent(this.rootApplicationContext);
            applicationContext.getEnvironment().setConversionService(
                this.rootApplicationContext.getEnvironment().getConversionService());
        }
    }

    private ConfigurableApplicationContext getSpringParent(DeploymentDescriptor deployment,
                                                           ApplicationRuntimeModel application) {
        ConfigurableApplicationContext parentSpringContext = null;
        if (deployment.getSpringParent() != null) {
            String springParent = deployment.getSpringParent();

            if (StringUtils.hasText(springParent)) {
                DeploymentDescriptor parent = application.getSpringPoweredDeployment(springParent);
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
        return parentSpringContext;
    }

    @SuppressWarnings("unchecked")
    private void addPostProcessors(DefaultListableBeanFactory beanFactory) {
        Map<String, BeanDefinition> processors = (Map<String, BeanDefinition>) rootApplicationContext
            .getBean(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        for (Map.Entry<String, BeanDefinition> entry : processors.entrySet()) {
            if (!beanFactory.containsBeanDefinition(entry.getKey())) {
                beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        }
    }

    private BeanStatExtension getBeanStatExtension() {
        try {
            return this.rootApplicationContext.getBean(BeanStatExtension.class);
        } catch (Throwable e) {
            return null;
        }
    }
}
