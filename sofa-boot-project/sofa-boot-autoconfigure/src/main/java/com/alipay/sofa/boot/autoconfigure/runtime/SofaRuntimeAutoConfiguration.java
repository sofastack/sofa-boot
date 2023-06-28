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

import com.alipay.sofa.boot.autoconfigure.condition.ConditionalOnSwitch;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.common.thread.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
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
import com.alipay.sofa.runtime.spring.RuntimeContextBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for sofa runtime.
 *
 * @author xuanbei 18/3/17
 */
@AutoConfiguration
@ConditionalOnClass(SofaRuntimeContext.class)
@EnableConfigurationProperties(SofaRuntimeProperties.class)
public class SofaRuntimeAutoConfiguration {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(SofaRuntimeAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeManager sofaRuntimeManager(Environment environment,
                                                        BindingConverterFactory bindingConverterFactory,
                                                        BindingAdapterFactory bindingAdapterFactory,
                                                        ObjectProvider<DynamicServiceProxyManager> dynamicServiceProxyManager) {
        ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
        SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
            environment.getProperty(SofaBootConstants.APP_NAME_KEY), Thread.currentThread()
                .getContextClassLoader(), clientFactoryInternal);
        clientFactoryInternal.registerClient(ReferenceClient.class, new ReferenceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext(), bindingConverterFactory,
            bindingAdapterFactory));
        clientFactoryInternal.registerClient(ServiceClient.class, new ServiceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext(), bindingConverterFactory,
            bindingAdapterFactory));

        // use bind to inject properties, avoid bean early init problem.
        SofaRuntimeContext sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        dynamicServiceProxyManager.ifUnique(sofaRuntimeContext::setServiceProxyManager);
        Binder binder = Binder.get(environment);
        SofaRuntimeContext.Properties properties = sofaRuntimeContext.getProperties();
        Bindable<SofaRuntimeContext.Properties> bindable = Bindable.of(SofaRuntimeContext.Properties.class)
                .withExistingValue(properties);
        binder.bind("sofa.boot.runtime", bindable);

        return sofaRuntimeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public static SofaRuntimeContext sofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager) {
        return sofaRuntimeManager.getSofaRuntimeContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public static BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory.addBindingConverters(new HashSet<>(SpringFactoriesLoader
            .loadFactories(BindingConverter.class, null)));
        return bindingConverterFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public static BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(new HashSet<>(SpringFactoriesLoader.loadFactories(
            BindingAdapter.class, null)));
        return bindingAdapterFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public static RuntimeContextBeanFactoryPostProcessor runtimeContextBeanFactoryPostProcessor() {
        return new RuntimeContextBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static ServiceBeanFactoryPostProcessor serviceBeanFactoryPostProcessor() {
        return new ServiceBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static ProxyBeanFactoryPostProcessor proxyBeanFactoryPostProcessor() {
        return new ProxyBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ComponentContextRefreshInterceptor componentContextRefreshInterceptor(SofaRuntimeManager sofaRuntimeManager) {
        return new ComponentContextRefreshInterceptor(sofaRuntimeManager);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSwitch(value = "runtimeAsyncInit")
    static class AsyncInitConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public static AsyncInitMethodManager asyncInitMethodManager() {
            return new AsyncInitMethodManager();
        }

        @Bean(AsyncInitMethodManager.ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME)
        @ConditionalOnMissingBean(name = AsyncInitMethodManager.ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME)
        public Supplier<ThreadPoolExecutor> asyncInitMethodExecutor(SofaRuntimeProperties sofaRuntimeProperties) {
            return ()-> {
                int coreSize = sofaRuntimeProperties.getAsyncInitExecutorCoreSize();
                int maxSize = sofaRuntimeProperties.getAsyncInitExecutorMaxSize();
                Assert.isTrue(coreSize >= 0, "executorCoreSize must no less than zero");
                Assert.isTrue(maxSize >= 0, "executorMaxSize must no less than zero");

                LOGGER.info("create async-init-bean thread pool, corePoolSize: {}, maxPoolSize: {}.",
                        coreSize, maxSize);
                return new SofaThreadPoolExecutor(coreSize, maxSize, 30, TimeUnit.SECONDS,
                        new SynchronousQueue<>(), new NamedThreadFactory("async-init-bean"),
                        new ThreadPoolExecutor.CallerRunsPolicy(), "async-init-bean",
                        SofaBootConstants.SOFA_BOOT_SPACE_NAME);
            };
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
    }
}
