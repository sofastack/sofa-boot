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

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.infra.constants.SofaBootInfraConstants;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.service.client.ReferenceClientImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.ApplicationShutdownCallbackPostProcessor;
import com.alipay.sofa.runtime.spring.ClientFactoryBeanPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor;
import com.alipay.sofa.runtime.spring.SofaRuntimeContextAwareProcessor;
import com.alipay.sofa.runtime.spring.callback.CloseApplicationContextCallBack;
import com.alipay.sofa.runtime.spring.config.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.spring.health.DefaultRuntimeHealthChecker;
import com.alipay.sofa.runtime.spring.health.MultiApplicationHealthIndicator;
import com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker;
import com.alipay.sofa.runtime.spring.health.SofaComponentHealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author xuanbei 18/3/17
 */
@Configuration
@EnableConfigurationProperties(SofaRuntimeConfigurationProperties.class)
public class SofaRuntimeAutoConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static BindingConverterFactory bindingConverterFactory() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory
            .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
        return bindingConverterFactory;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static BindingAdapterFactory bindingAdapterFactory() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
        return bindingAdapterFactory;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static SofaRuntimeContext sofaRuntimeContext(@Value("${"
                                                               + SofaBootInfraConstants.APP_NAME_KEY
                                                               + "}") String appName,
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

    @Bean
    public static ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor(BindingAdapterFactory bindingAdapterFactory,
                                                                                        BindingConverterFactory bindingConverterFactory) {
        return new ServiceAnnotationBeanPostProcessor(bindingAdapterFactory,
            bindingConverterFactory);
    }

    @Bean
    public static ClientFactoryBeanPostProcessor clientFactoryBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext) {
        return new ClientFactoryBeanPostProcessor(sofaRuntimeContext.getClientFactory());
    }

    @Bean
    public static ApplicationShutdownCallbackPostProcessor applicationShutdownCallbackPostProcessor(SofaRuntimeContext sofaRuntimeContext) {
        return new ApplicationShutdownCallbackPostProcessor(
            sofaRuntimeContext.getSofaRuntimeManager());
    }

    @Bean
    public static SofaRuntimeContextAwareProcessor sofaRuntimeContextAwareProcessor(SofaRuntimeContext sofaRuntimeContext) {
        return new SofaRuntimeContextAwareProcessor(sofaRuntimeContext);
    }

    @Bean
    public CloseApplicationContextCallBack closeApplicationContextCallBack() {
        return new CloseApplicationContextCallBack();
    }

    private static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class DefaultRuntimeHealthCheckerConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public DefaultRuntimeHealthChecker defaultRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new DefaultRuntimeHealthChecker(sofaRuntimeContext);
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class, Biz.class })
    public static class MultiApplicationHealthIndicatorConfiguration {
        @Bean
        public MultiApplicationHealthIndicator multiApplicationHealthIndicator() {
            return new MultiApplicationHealthIndicator();
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthIndicator.class })
    @ConditionalOnMissingClass({ "com.alipay.sofa.healthcheck.core.HealthChecker" })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class SofaRuntimeHealthIndicatorConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public SofaComponentHealthIndicator sofaComponentHealthIndicator(SofaRuntimeContext sofaRuntimeContext) {
            return new SofaComponentHealthIndicator(sofaRuntimeContext);
        }
    }

    @Configuration
    @ConditionalOnClass({ HealthChecker.class })
    @AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
    public static class SofaModuleHealthCheckerConfiguration {
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public SofaComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new SofaComponentHealthChecker(sofaRuntimeContext);
        }
    }
}
