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
package com.alipay.sofa.runtime.spring.configuration;

import com.alipay.sofa.infra.constants.CommonMiddlewareConstants;
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
import com.alipay.sofa.runtime.spring.ClientFactoryBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeContextAwareProcessor;
import com.alipay.sofa.runtime.spring.config.SofaRuntimeProperties;
import com.alipay.sofa.runtime.spring.health.ComponentHealthChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author xuanbei 18/3/17
 */
@Configuration
@ComponentScan(value = { "com.alipay.sofa.runtime.spring" })
public class SofaRuntimeAutoConfiguration {
    @Bean
    public SofaRuntimeProperties sofaRuntimeProperties() {
        return new SofaRuntimeProperties();
    }

    @Bean
    public BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory
            .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
        return bindingConverterFactory;
    }

    @Bean
    public BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
        return bindingAdapterFactory;
    }

    @Bean
    public SofaRuntimeContext sofaRuntimeContext(@Value("${"
                                                        + CommonMiddlewareConstants.APP_NAME_KEY
                                                        + "}") String appName,
                                                 BindingConverterFactory bindingConverterFactory,
                                                 BindingAdapterFactory bindingAdapterFactory,
                                                 SofaRuntimeProperties sofaRuntimeProperties) {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(appName,
            SofaRuntimeAutoConfiguration.class.getClassLoader(), clientFactoryInternal);
        sofaRuntimeManager.getSofaRuntimeContext();
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ReferenceClient.class,
            new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory, sofaRuntimeProperties));
        sofaRuntimeManager.getComponentManager().registerComponentClient(
            ServiceClient.class,
            new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                bindingConverterFactory, bindingAdapterFactory));
        return sofaRuntimeManager.getSofaRuntimeContext();

    }

    @Bean
    public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext,
                                                                                 SofaRuntimeProperties sofaRuntimeProperties,
                                                                                 BindingAdapterFactory bindingAdapterFactory) {
        return new ServiceAnnotationBeanPostProcessor(sofaRuntimeContext, sofaRuntimeProperties,
            bindingAdapterFactory);
    }

    @Bean
    public ClientFactoryBeanPostProcessor clientFactoryBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext) {
        return new ClientFactoryBeanPostProcessor(sofaRuntimeContext.getClientFactory());
    }

    @Bean
    public SofaRuntimeContextAwareProcessor sofaRuntimeContextAwareProcessor(SofaRuntimeContext sofaRuntimeContext) {
        return new SofaRuntimeContextAwareProcessor(sofaRuntimeContext);
    }

    @Bean
    public ComponentHealthChecker componentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        return new ComponentHealthChecker(sofaRuntimeContext);
    }

    private static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}
