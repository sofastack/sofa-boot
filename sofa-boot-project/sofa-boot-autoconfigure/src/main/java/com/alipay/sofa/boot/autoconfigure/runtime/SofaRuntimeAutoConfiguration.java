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
package com.alipay.sofa.boot.autoconfigure.runtime;

import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.proxy.ProxyBeanFactoryPostProcessor;
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
import com.alipay.sofa.runtime.spring.AsyncInitBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.AsyncProxyBeanPostProcessor;
import com.alipay.sofa.runtime.spring.RuntimeContextBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeAwareProcessor;
import com.alipay.sofa.runtime.spring.SofaShareBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.async.AsyncTaskExecutionListener;
import com.alipay.sofa.runtime.spring.share.SofaPostProcessorShareManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author xuanbei 18/3/17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SofaRuntimeConfigurationProperties.class)
@ConditionalOnClass(SofaFramework.class)
public class SofaRuntimeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AsyncTaskExecutionListener asyncTaskExecutionListener() {
        return new AsyncTaskExecutionListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor() {
        return new AsyncProxyBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncInitBeanFactoryPostProcessor asyncInitBeanFactoryPostProcessor() {
        return new AsyncInitBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
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
    public static SofaRuntimeManager sofaRuntimeManager(Environment environment,
                                                        BindingConverterFactory bindingConverterFactory,
                                                        BindingAdapterFactory bindingAdapterFactory) {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
            environment.getProperty("spring.application.name"), Thread.currentThread()
                .getContextClassLoader(), clientFactoryInternal);
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ReferenceClient.class,
            new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ServiceClient.class,
            new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        return sofaRuntimeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeContext sofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager) {
        return sofaRuntimeManager.getSofaRuntimeContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public static RuntimeContextBeanFactoryPostProcessor runtimeContextBeanFactoryPostProcessor() {
        return new RuntimeContextBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.alipay.sofa.isle.ApplicationRuntimeModel")
    @ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
    public static SofaShareBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor() {
        return new SofaShareBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.alipay.sofa.isle.ApplicationRuntimeModel")
    @ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
    public SofaPostProcessorShareManager sofaModulePostProcessorShareManager() {
        return new SofaPostProcessorShareManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public static ServiceBeanFactoryPostProcessor serviceBeanFactoryPostProcessor() {
        return new ServiceBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.proxy.bean", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static ProxyBeanFactoryPostProcessor proxyBeanFactoryPostProcessor() {
        return new ProxyBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeAwareProcessor sofaRuntimeContextAwareProcessor(SofaRuntimeManager sofaRuntimeManager) {
        return new SofaRuntimeAwareProcessor(sofaRuntimeManager);
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
