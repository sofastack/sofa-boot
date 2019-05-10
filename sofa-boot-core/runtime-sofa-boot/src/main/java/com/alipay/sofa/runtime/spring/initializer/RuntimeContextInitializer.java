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
package com.alipay.sofa.runtime.spring.initializer;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.event.RuntimeShutdownCallback;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.ext.client.ExtensionClientImpl;
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
import com.alipay.sofa.runtime.spring.ClientFactoryBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ExtensionClientBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ReferenceAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.RuntimeShutdownCallbackPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeContextAwareProcessor;
import com.alipay.sofa.runtime.spring.bean.SofaParameterNameDiscoverer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
public class RuntimeContextInitializer implements RuntimeShutdownCallback {

    /**
     * Objects shared by multi spring application context
     */
    private static BindingConverterFactory bindingConverterFactory;
    private static BindingAdapterFactory   bindingAdapterFactory;
    private static SofaRuntimeContext      sofaRuntimeContext;

    public static BindingConverterFactory bindingConverterFactory() {
        if (bindingConverterFactory == null) {
            BindingConverterFactory temp = new BindingConverterFactoryImpl();
            temp.addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
            bindingConverterFactory = temp;
        }
        return bindingConverterFactory;
    }

    public static BindingAdapterFactory bindingAdapterFactory() {
        if (bindingAdapterFactory == null) {
            BindingAdapterFactory temp = new BindingAdapterFactoryImpl();
            temp.addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
            bindingAdapterFactory = temp;
        }
        return bindingAdapterFactory;
    }

    public static SofaRuntimeContext sofaRuntimeContext(String appName) {
        if (sofaRuntimeContext == null) {
            ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
            SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(appName,
                RuntimeContextInitializer.class.getClassLoader(), clientFactoryInternal);
            sofaRuntimeManager.getComponentManager().registerComponentClient(
                ReferenceClient.class,
                new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                    bindingConverterFactory(), bindingAdapterFactory()));
            sofaRuntimeManager.getComponentManager().registerComponentClient(
                ServiceClient.class,
                new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                    bindingConverterFactory(), bindingAdapterFactory()));
            SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
            sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        }
        return sofaRuntimeContext;
    }

    public static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }

    public static void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        applicationContext.addBeanFactoryPostProcessor(new ServiceBeanFactoryPostProcessor(
            applicationContext, sofaRuntimeContext, bindingConverterFactory));
        beanFactory.registerSingleton(SofaBootConstants.BINDING_CONVERTER_FACTORY_BEAN_ID,
            bindingConverterFactory);
        beanFactory.registerSingleton(SofaBootConstants.BINDING_ADAPTER_FACTORY_BEAN_ID,
            bindingAdapterFactory);
        beanFactory.registerSingleton(SofaBootConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
            sofaRuntimeContext);

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
        RuntimeShutdownCallbackPostProcessor runtimeShutdownCallbackPostProcessor = new RuntimeShutdownCallbackPostProcessor(
            sofaRuntimeContext.getSofaRuntimeManager());
        beanFactory.addBeanPostProcessor(runtimeShutdownCallbackPostProcessor);
        beanFactory.registerSingleton(
            RuntimeShutdownCallbackPostProcessor.class.getCanonicalName(),
            runtimeShutdownCallbackPostProcessor);

        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            ((AbstractAutowireCapableBeanFactory) beanFactory)
                .setParameterNameDiscoverer(new SofaParameterNameDiscoverer(applicationContext
                    .getEnvironment()));
        }
    }

    @Override
    public void shutdown() throws Throwable {
        if (sofaRuntimeContext != null && sofaRuntimeContext.getSofaRuntimeManager() != null) {
            SofaFramework.unRegisterSofaRuntimeManager(sofaRuntimeContext.getSofaRuntimeManager());
        }
        bindingAdapterFactory = null;
        bindingConverterFactory = null;
        sofaRuntimeContext = null;
    }
}