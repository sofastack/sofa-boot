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
package com.alipay.sofa.runtime.spring.listener;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.alipay.sofa.infra.constants.SofaBootInfraConstants;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.service.client.ReferenceClientImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.*;
import com.alipay.sofa.runtime.spring.bean.SofaParameterNameDiscoverer;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;

/**
 * Prepare application context for refreshing.
 *
 * @author qilong.zql
 * @since 3.1.0
 */
public class SofaRuntimeApplicationListener implements
                                           ApplicationListener<ApplicationPreparedEvent> {
    /**
     * Objects shared by multi spring application context
     */
    private static BindingConverterFactory bindingConverterFactory;
    private static BindingAdapterFactory   bindingAdapterFactory;
    private static SofaRuntimeContext      sofaRuntimeContext;
    private AtomicBoolean                  isInitiated = new AtomicBoolean(false);

    public static void initApplicationContext(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        applicationContext.addBeanFactoryPostProcessor(new ServiceBeanFactoryPostProcessor(
            applicationContext, sofaRuntimeContext, bindingConverterFactory));
        beanFactory.registerSingleton(
            SofaRuntimeFrameworkConstants.BINDING_CONVERTER_FACTORY_BEAN_ID,
            bindingConverterFactory);
        beanFactory.registerSingleton(
            SofaRuntimeFrameworkConstants.BINDING_ADAPTER_FACTORY_BEAN_ID, bindingAdapterFactory);
        beanFactory.registerSingleton(SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
            sofaRuntimeContext);

        beanFactory.registerSingleton(ServiceAnnotationBeanPostProcessor.class.getCanonicalName(),
            new ServiceAnnotationBeanPostProcessor(applicationContext, sofaRuntimeContext,
                bindingAdapterFactory, bindingConverterFactory));
        beanFactory.registerSingleton(ClientFactoryBeanPostProcessor.class.getCanonicalName(),
            new ClientFactoryBeanPostProcessor(sofaRuntimeContext.getClientFactory()));
        beanFactory
            .registerSingleton(
                ApplicationShutdownCallbackPostProcessor.class.getCanonicalName(),
                new ApplicationShutdownCallbackPostProcessor(sofaRuntimeContext
                    .getSofaRuntimeManager()));
        beanFactory.registerSingleton(SofaRuntimeContextAwareProcessor.class.getCanonicalName(),
            new SofaRuntimeContextAwareProcessor(sofaRuntimeContext));

        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            ((AbstractAutowireCapableBeanFactory) beanFactory)
                .setParameterNameDiscoverer(new SofaParameterNameDiscoverer());
        }
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        if (isInitiated.compareAndSet(false, true)) {
            bindingConverterFactory = bindingConverterFactory();
            bindingAdapterFactory = bindingAdapterFactory();
            sofaRuntimeContext = sofaRuntimeContext(
                environment.getProperty(SofaBootInfraConstants.APP_NAME_KEY),
                bindingConverterFactory, bindingAdapterFactory);
        }
        initApplicationContext(applicationContext);
    }

    private BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory
            .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
        return bindingConverterFactory;
    }

    private BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
        return bindingAdapterFactory;
    }

    private SofaRuntimeContext sofaRuntimeContext(String appName,
                                                  BindingConverterFactory bindingConverterFactory,
                                                  BindingAdapterFactory bindingAdapterFactory) {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(appName,
            SofaRuntimeAutoConfiguration.class.getClassLoader(), clientFactoryInternal);
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ReferenceClient.class,
            new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ServiceClient.class,
            new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        return sofaRuntimeManager.getSofaRuntimeContext();
    }

    private <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}