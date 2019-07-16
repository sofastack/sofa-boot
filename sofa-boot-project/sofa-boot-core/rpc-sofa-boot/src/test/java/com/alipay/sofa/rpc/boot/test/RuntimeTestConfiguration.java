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
package com.alipay.sofa.rpc.boot.test;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
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
import com.alipay.sofa.runtime.spring.AsyncProxyBeanPostProcessor;
import com.alipay.sofa.runtime.spring.RuntimeContextBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.async.AsyncTaskExecutionListener;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@Configuration
public class RuntimeTestConfiguration {
    @Bean
    public static BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory
            .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
        return bindingConverterFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public static BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
        return bindingAdapterFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeContext sofaRuntimeContext(Environment environment,
                                                        BindingConverterFactory bindingConverterFactory,
                                                        BindingAdapterFactory bindingAdapterFactory) {
        String appName = environment.getProperty(SofaBootConstants.APP_NAME_KEY);
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(appName,
            RuntimeTestConfiguration.class.getClassLoader(), clientFactoryInternal);
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

    @Bean
    public static RuntimeContextBeanFactoryPostProcessor runtimeContextBeanFactoryPostProcessor(BindingAdapterFactory bindingAdapterFactory,
                                                                                                BindingConverterFactory bindingConverterFactory,
                                                                                                SofaRuntimeContext sofaRuntimeContext) {
        return new RuntimeContextBeanFactoryPostProcessor(bindingAdapterFactory,
            bindingConverterFactory, sofaRuntimeContext);
    }

    @Bean
    public static ServiceBeanFactoryPostProcessor serviceBeanFactoryPostProcessor(SofaRuntimeContext sofaRuntimeContext,
                                                                                  BindingConverterFactory bindingConverterFactory) {
        return new ServiceBeanFactoryPostProcessor(sofaRuntimeContext, bindingConverterFactory);
    }

    @Bean
    public AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor() {
        return new AsyncProxyBeanPostProcessor();
    }

    @Bean
    public AsyncTaskExecutionListener asyncTaskExecutionListener() {
        return new AsyncTaskExecutionListener();
    }

    public static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}