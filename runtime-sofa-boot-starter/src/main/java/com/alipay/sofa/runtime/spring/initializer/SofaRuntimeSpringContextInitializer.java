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

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.infra.constants.SofaBootInfraConstants;
import com.alipay.sofa.infra.log.space.SofaBootLogSpaceIsolationInit;
import com.alipay.sofa.infra.utils.SOFABootEnvUtils;
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
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import com.alipay.sofa.runtime.spi.log.SofaRuntimeLoggerFactory;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.*;
import com.alipay.sofa.runtime.spring.bean.SofaParameterNameDiscoverer;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuanbei 18/3/13
 */
public class SofaRuntimeSpringContextInitializer
                                                implements
                                                ApplicationContextInitializer<ConfigurableApplicationContext> {

    /**
     * Objects shared by multi spring application context
     */
    private static BindingConverterFactory bindingConverterFactory;
    private static BindingAdapterFactory   bindingAdapterFactory;
    private static SofaRuntimeContext      sofaRuntimeContext;
    private static AtomicBoolean           isInitiated = new AtomicBoolean(false);

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

        beanFactory.registerSingleton(
            ReferenceAnnotationBeanPostProcessor.class.getCanonicalName(),
            new ReferenceAnnotationBeanPostProcessor(applicationContext, sofaRuntimeContext,
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
                .setParameterNameDiscoverer(new SofaParameterNameDiscoverer(applicationContext
                    .getEnvironment()));
        }
    }

    public static void setIsInitiated(boolean isInitiated) {
        SofaRuntimeSpringContextInitializer.isInitiated.set(isInitiated);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        if (isInitiated.compareAndSet(false, true)) {
            bindingConverterFactory = bindingConverterFactory();
            bindingAdapterFactory = bindingAdapterFactory();
            sofaRuntimeContext = sofaRuntimeContext(
                environment.getProperty(SofaBootInfraConstants.APP_NAME_KEY),
                bindingConverterFactory, bindingAdapterFactory);
        }
        initApplicationContext(applicationContext);

        if (SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }
        SofaLogger.info("SOFABoot Runtime Starting!");
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
