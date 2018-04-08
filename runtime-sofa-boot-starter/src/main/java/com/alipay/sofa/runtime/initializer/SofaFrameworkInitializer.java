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
package com.alipay.sofa.runtime.initializer;

import com.alipay.sofa.runtime.SofaFrameworkImpl;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.component.AppConfiguration;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.impl.AppConfigurationImpl;
import com.alipay.sofa.runtime.service.client.ReferenceClientImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingFactoryContainer;
import com.alipay.sofa.runtime.spi.SofaFrameworkHolder;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.constants.SofaConfigurationConstants;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.ClientFactoryBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeContextAwareProcessor;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuanbei 18/3/6
 */
public abstract class SofaFrameworkInitializer {

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * initialize SOFA Framework
     * @param appName
     * @param applicationContext
     */
    public static void initialize(String appName, ConfigurableApplicationContext applicationContext) {
        if (initialized.compareAndSet(false, true)) {
            addSofaRuntimeManager(appName, applicationContext);
            initBindingFactory();
            registerProcessors(appName, applicationContext);
        }
    }

    private static void initBindingFactory() {
        BindingFactoryContainer.setBindingAdapterFactory(createBindingAdapterFactory());
        BindingFactoryContainer.setBindingConverterFactory(createBindingConverterFactory());
    }

    private static void registerProcessors(String appName,
                                           ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(
            new SofaRuntimeContextAwareProcessor(SofaFrameworkHolder.getSofaFramework()
                .getSofaRuntimeContext(appName)));
        applicationContext.getBeanFactory().addBeanPostProcessor(
            new ServiceAnnotationBeanPostProcessor(SofaFrameworkHolder.getSofaFramework()
                .getSofaRuntimeContext(appName), applicationContext));
        applicationContext.getBeanFactory().addBeanPostProcessor(
            new ClientFactoryBeanPostProcessor(SofaFrameworkHolder.getSofaFramework()
                .getSofaRuntimeContext(appName).getClientFactory()));
    }

    private static void addSofaRuntimeManager(String appName,
                                              ConfigurableApplicationContext applicationContext) {
        if (!SofaFrameworkHolder.containsSofaFramework()) {
            SofaFrameworkHolder.setSofaFramework(new SofaFrameworkImpl());
        }

        SofaFrameworkImpl sofaFramework = (SofaFrameworkImpl) SofaFrameworkHolder
            .getSofaFramework();
        AppConfiguration applicationConfiguration = createAppConfigurationImpl(applicationContext);
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(appName,
            SofaFrameworkInitializer.class.getClassLoader(), applicationConfiguration,
            clientFactoryInternal);
        ComponentManager componentManager = sofaRuntimeManager.getComponentManager();

        // register service client & reference client
        componentManager.registerComponentClient(ServiceClient.class, new ServiceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext()));
        componentManager.registerComponentClient(ReferenceClient.class, new ReferenceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext()));

        sofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
    }

    private static AppConfigurationImpl createAppConfigurationImpl(ConfigurableApplicationContext applicationContext) {
        Map<String, String> properties = new HashMap<>();
        for (String configurationKey : getAllConfigurationConstants(SofaConfigurationConstants.class)) {
            String configurationValue = applicationContext.getEnvironment().getProperty(
                configurationKey);
            if (configurationValue != null) {
                properties.put(configurationKey, configurationValue);
            }
        }
        return new AppConfigurationImpl(properties);
    }

    private static List<String> getAllConfigurationConstants(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> result = new ArrayList<>();

        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                try {
                    result.add((String) field.get(clazz));
                } catch (IllegalAccessException e) {
                    // Should never happen.
                }
            }
        }

        return result;
    }

    private static BindingConverterFactory createBindingConverterFactory() {
        BindingConverterFactory sofaBootBindingConverterFactory = new BindingConverterFactoryImpl();
        sofaBootBindingConverterFactory
            .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
        return sofaBootBindingConverterFactory;
    }

    private static BindingAdapterFactory createBindingAdapterFactory() {
        BindingAdapterFactory sofaBootBindingAdapterFactory = new BindingAdapterFactoryImpl();
        sofaBootBindingAdapterFactory
            .addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
        return sofaBootBindingAdapterFactory;
    }

    private static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        if (serviceLoader == null) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}
