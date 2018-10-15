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

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.context.SofaModuleApplicationContext;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
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
        SofaModuleProperties sofaModuleProperties = rootApplicationContext
            .getBean(SofaModuleFrameworkConstants.SOFA_MODULE_PROPERTIES_BEAN_ID,
                SofaModuleProperties.class);
        BeanLoadCostBeanFactory beanFactory = new BeanLoadCostBeanFactory(
            sofaModuleProperties.getBeanLoadCost());
        beanFactory.setParameterNameDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        beanFactory
            .setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());

        GenericApplicationContext ctx = sofaModuleProperties.isPublishEventToParent() ? new GenericApplicationContext(
            beanFactory) : new SofaModuleApplicationContext(beanFactory);
        String activeProfiles = sofaModuleProperties.getActiveProfiles();
        if (StringUtils.hasText(activeProfiles)) {
            String[] profiles = activeProfiles
                .split(SofaModuleFrameworkConstants.PROFILE_SEPARATOR);
            ctx.getEnvironment().setActiveProfiles(profiles);
        }
        setUpParentSpringContext(ctx, deployment, application);
        final ClassLoader moduleClassLoader = deployment.getClassLoader();
        ctx.setClassLoader(moduleClassLoader);
        CachedIntrospectionResults.acceptClassLoader(moduleClassLoader);

        // set allowBeanDefinitionOverriding
        ctx.setAllowBeanDefinitionOverriding(rootApplicationContext.getBean(
            SofaModuleProperties.class).isAllowBeanDefinitionOverriding());

        ctx.getBeanFactory().setBeanClassLoader(moduleClassLoader);
        ctx.getBeanFactory().addPropertyEditorRegistrar(new PropertyEditorRegistrar() {

            public void registerCustomEditors(PropertyEditorRegistry registry) {
                registry.registerCustomEditor(Class.class, new ClassEditor(moduleClassLoader));
                registry.registerCustomEditor(Class[].class,
                    new ClassArrayEditor(moduleClassLoader));
            }
        });
        deployment.setApplicationContext(ctx);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(ctx);
        beanDefinitionReader.setValidating(true);
        beanDefinitionReader.setNamespaceAware(true);
        beanDefinitionReader
            .setBeanClassLoader(deployment.getApplicationContext().getClassLoader());
        beanDefinitionReader.setResourceLoader(ctx);
        loadBeanDefinitions(deployment, beanDefinitionReader);
        addPostProcessors(beanFactory);
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
        } else {
            applicationContext.setParent(this.rootApplicationContext);
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
                        SofaLogger.warn("Module [{0}]'s Spring-Parent [{1}] is Null!",
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
            .getBean(SofaModuleFrameworkConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        for (Map.Entry<String, BeanDefinition> entry : processors.entrySet()) {
            if (!beanFactory.containsBeanDefinition(entry.getKey())) {
                beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        }
    }
}
