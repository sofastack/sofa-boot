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
import com.alipay.sofa.isle.constants.SofaIsleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.log.SofaIsleLoggerFactory;
import com.alipay.sofa.isle.spring.context.SofaIsleApplicationContext;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.isle.spring.config.SofaIsleProperties;
import org.slf4j.Logger;
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
 * @author linfengqi
 * @mail fengqi.lin@alipay.com
 * @date 2011-7-26
 */
public class DynamicSpringContextLoader implements SpringContextLoader {
    private static final Logger                  LOGGER = SofaIsleLoggerFactory
                                                            .getLogger(DynamicSpringContextLoader.class);
    private final ConfigurableApplicationContext rootApplicationContext;

    public DynamicSpringContextLoader(ApplicationContext applicationContext) {
        this.rootApplicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void loadSpringContext(DeploymentDescriptor deployment,
                                  ApplicationRuntimeModel application) throws Exception {
        BeanLoadCostBeanFactory beanFactory = new BeanLoadCostBeanFactory(rootApplicationContext
            .getBean(SofaIsleProperties.class).getBeanLoadCost());
        beanFactory.setParameterNameDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        beanFactory
            .setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());

        SofaIsleApplicationContext ctx = new SofaIsleApplicationContext(beanFactory);
        // set profile
        String sofaIsleActiveProfiles = rootApplicationContext.getBean(SofaIsleProperties.class)
            .getActiveProfiles();
        if (StringUtils.hasText(sofaIsleActiveProfiles)) {
            String[] activeProfiles = sofaIsleActiveProfiles
                .split(SofaIsleFrameworkConstants.PROFILE_SPLITTER);
            ctx.getEnvironment().setActiveProfiles(activeProfiles);
        }
        setUpParentSpringContext(ctx, deployment, application);
        final ClassLoader moduleClassLoader = deployment.getClassLoader();
        ctx.setClassLoader(moduleClassLoader);
        CachedIntrospectionResults.acceptClassLoader(moduleClassLoader);

        // set allowBeanDefinitionOverriding
        ctx.setAllowBeanDefinitionOverriding(rootApplicationContext.getBean(
            SofaIsleProperties.class).isAllowBeanDefinitionOverriding());

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

        for (Map.Entry<String, Resource> entry : deployment.getSpringResources().entrySet()) {
            String fileName = entry.getKey();
            beanDefinitionReader.loadBeanDefinitions(entry.getValue());
            deployment.addInstalledSpringXml(fileName);
        }
        addPostProcessors(beanFactory);
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

            if (springParent != null && springParent.length() > 0) {
                DeploymentDescriptor parent = application.getSpringPoweredDeployment(springParent);
                if (parent != null) {
                    parentSpringContext = (ConfigurableApplicationContext) parent
                        .getApplicationContext();
                    if (parentSpringContext == null) {
                        LOGGER.warn("Module [" + deployment.getModuleName() + "]'s Spring-Parent ["
                                    + springParent + "] is Null!");
                    }
                }
            }
        }
        return parentSpringContext;
    }

    @SuppressWarnings("unchecked")
    private void addPostProcessors(DefaultListableBeanFactory beanFactory) {
        Map<String, BeanDefinition> processors = (Map<String, BeanDefinition>) rootApplicationContext
            .getBean(SofaIsleFrameworkConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        for (Map.Entry<String, BeanDefinition> entry : processors.entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }
}
