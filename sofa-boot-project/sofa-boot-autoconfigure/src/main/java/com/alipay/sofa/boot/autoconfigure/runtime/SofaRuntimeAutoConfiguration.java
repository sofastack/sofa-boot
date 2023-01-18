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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.ServiceLoaderUtils;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.async.AsyncInitMethodManager;
import com.alipay.sofa.runtime.context.ComponentContextRefreshInterceptor;
import com.alipay.sofa.runtime.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.impl.StandardSofaRuntimeManager;
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
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;
import com.alipay.sofa.runtime.spring.AsyncInitBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.AsyncProxyBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ClientFactoryAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ReferenceAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeAwareProcessor;
import com.alipay.sofa.runtime.startup.ComponentBeanStatCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for sofa runtime.
 *
 * @author xuanbei 18/3/17
 */
@AutoConfiguration
@EnableConfigurationProperties(SofaRuntimeProperties.class)
@ConditionalOnClass(SofaRuntimeContext.class)
public class SofaRuntimeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeManager sofaRuntimeManager(Environment environment,
                                                        BindingConverterFactory bindingConverterFactory,
                                                        BindingAdapterFactory bindingAdapterFactory) {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
                environment.getProperty(SofaBootConstants.APP_NAME_KEY),
                Thread.currentThread().getContextClassLoader(),
                clientFactoryInternal);
        clientFactoryInternal.registerClient(ReferenceClient.class,
            new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        clientFactoryInternal.registerClient(ServiceClient.class,
            new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        return sofaRuntimeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeContext sofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager,
                                                        ObjectProvider<DynamicServiceProxyManager> dynamicServiceProxyManager,
                                                        SofaRuntimeProperties sofaRuntimeProperties) {
        SofaRuntimeContext sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        dynamicServiceProxyManager.ifUnique(sofaRuntimeContext::setServiceProxyManager);
        sofaRuntimeContext.getProperties().setSkipJvmReferenceHealthCheck(sofaRuntimeProperties.isSkipJvmReferenceHealthCheck());
        sofaRuntimeContext.getProperties().setSkipExtensionHealthCheck(sofaRuntimeProperties.isSkipExtensionHealthCheck());
        sofaRuntimeContext.getProperties().setDisableJvmFirst(sofaRuntimeProperties.isDisableJvmFirst());
        sofaRuntimeContext.getProperties().setExtensionFailureInsulating(sofaRuntimeProperties.isExtensionFailureInsulating());
        sofaRuntimeContext.getProperties().setJvmFilterEnable(sofaRuntimeProperties.isJvmFilterEnable());
        sofaRuntimeContext.getProperties().setSkipAllComponentShutdown(sofaRuntimeProperties.isSkipAllComponentShutdown());
        sofaRuntimeContext.getProperties().setSkipCommonComponentShutdown(sofaRuntimeProperties.isSkipCommonComponentShutdown());
        sofaRuntimeContext.getProperties().setServiceInterfaceTypeCheck(sofaRuntimeProperties.isServiceInterfaceTypeCheck());
        return sofaRuntimeContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public static BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory.addBindingConverters(ServiceLoaderUtils.getClassesByServiceLoader(BindingConverter.class));
        return bindingConverterFactory;
    }


    @Bean
    @ConditionalOnMissingBean
    public static BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(ServiceLoaderUtils.getClassesByServiceLoader(BindingAdapter.class));
        return bindingAdapterFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncInitMethodManager asyncInitMethodManager() {
        return new AsyncInitMethodManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor(AsyncInitMethodManager asyncInitMethodManager) {
        return new AsyncProxyBeanPostProcessor(asyncInitMethodManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public static AsyncInitBeanFactoryPostProcessor asyncInitBeanFactoryPostProcessor() {
        return new AsyncInitBeanFactoryPostProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public static ServiceBeanFactoryPostProcessor serviceBeanFactoryPostProcessor() {
        return new ServiceBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor(SofaRuntimeManager sofaRuntimeManager,
                                                                                            BindingConverterFactory bindingConverterFactory,
                                                                                            BindingAdapterFactory bindingAdapterFactory) {
        return new ReferenceAnnotationBeanPostProcessor(sofaRuntimeManager.getSofaRuntimeContext(), bindingAdapterFactory, bindingConverterFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.proxy.bean", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static ProxyBeanFactoryPostProcessor proxyBeanFactoryPostProcessor() {
        return new ProxyBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeAwareProcessor sofaRuntimeContextAwareProcessor(SofaRuntimeManager sofaRuntimeManager) {
        return new SofaRuntimeAwareProcessor(sofaRuntimeManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public static ClientFactoryAnnotationBeanPostProcessor clientFactoryAnnotationBeanPostProcessor(SofaRuntimeManager sofaRuntimeManager) {
        return new ClientFactoryAnnotationBeanPostProcessor(sofaRuntimeManager.getClientFactoryInternal());
    }

    @Bean
    @ConditionalOnMissingBean
    public ComponentBeanStatCustomizer componentBeanStatCustomizer() {
        return new ComponentBeanStatCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ComponentContextRefreshInterceptor componentContextRefreshInterceptor(SofaRuntimeManager sofaRuntimeManager) {
        return new ComponentContextRefreshInterceptor(sofaRuntimeManager);
    }
}
