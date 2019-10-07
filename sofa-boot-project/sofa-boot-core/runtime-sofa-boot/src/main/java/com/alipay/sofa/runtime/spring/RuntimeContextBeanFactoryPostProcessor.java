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
package com.alipay.sofa.runtime.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alipay.sofa.runtime.ext.client.ExtensionClientImpl;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.bean.SofaParameterNameDiscoverer;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RuntimeContextBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
                                                   ApplicationContextAware {
    private BindingAdapterFactory   bindingAdapterFactory;
    private BindingConverterFactory bindingConverterFactory;
    private SofaRuntimeContext      sofaRuntimeContext;
    private ApplicationContext      applicationContext;

    public RuntimeContextBeanFactoryPostProcessor(BindingAdapterFactory bindingAdapterFactory,
                                                  BindingConverterFactory bindingConverterFactory,
                                                  SofaRuntimeContext sofaRuntimeContext) {
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.bindingConverterFactory = bindingConverterFactory;
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        // work on all bean
        beanFactory.addBeanPostProcessor(new SofaRuntimeContextAwareProcessor(sofaRuntimeContext));
        beanFactory.addBeanPostProcessor(new ClientFactoryBeanPostProcessor(sofaRuntimeContext
            .getClientFactory()));
        beanFactory.addBeanPostProcessor(new ExtensionClientBeanPostProcessor(
            new ExtensionClientImpl(sofaRuntimeContext)));
        beanFactory
            .addBeanPostProcessor(new ReferenceAnnotationBeanPostProcessor(applicationContext,
                sofaRuntimeContext, bindingAdapterFactory, bindingConverterFactory));

        // work on all bean on the beginning, then reorder after all bean post processors being registered
        RuntimeShutdownAwarePostProcessor runtimeShutdownAwarePostProcessor = new RuntimeShutdownAwarePostProcessor(
            sofaRuntimeContext.getSofaRuntimeManager());
        beanFactory.addBeanPostProcessor(runtimeShutdownAwarePostProcessor);
        beanFactory.registerSingleton(RuntimeShutdownAwarePostProcessor.class.getCanonicalName(),
            runtimeShutdownAwarePostProcessor);

        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            ((AbstractAutowireCapableBeanFactory) beanFactory)
                .setParameterNameDiscoverer(new SofaParameterNameDiscoverer(applicationContext
                    .getEnvironment()));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}