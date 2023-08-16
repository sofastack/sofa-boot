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

import com.alipay.sofa.runtime.async.AsyncInitMethodManager;
import com.alipay.sofa.runtime.context.ComponentContextRefreshInterceptor;
import com.alipay.sofa.runtime.proxy.ProxyBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.AsyncInitBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.AsyncProxyBeanPostProcessor;
import com.alipay.sofa.runtime.spring.RuntimeContextBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaRuntimeAutoConfiguration}.
 *
 * @author huzijie
 * @version SofaRuntimeAutoConfigurationTests.java, v 0.1 2023年02月01日 4:09 PM huzijie Exp $
 */
public class SofaRuntimeAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaRuntimeAutoConfiguration.class));

    @Test
    public void registerRuntimeBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(SofaRuntimeManager.class)
                        .hasSingleBean(SofaRuntimeContext.class)
                        .hasSingleBean(BindingConverterFactory.class)
                        .hasSingleBean(BindingAdapterFactory.class)
                        .hasSingleBean(AsyncInitMethodManager.class)
                        .hasSingleBean(AsyncProxyBeanPostProcessor.class)
                        .hasSingleBean(AsyncInitBeanFactoryPostProcessor.class)
                        .hasSingleBean(ServiceBeanFactoryPostProcessor.class)
                        .hasSingleBean(RuntimeContextBeanFactoryPostProcessor.class)
                        .hasSingleBean(ProxyBeanFactoryPostProcessor.class)
                        .hasSingleBean(ComponentContextRefreshInterceptor.class));
    }

    @Test
    public void noRuntimeBeansWhenSofaRuntimeContextClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SofaRuntimeContext.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaRuntimeManager.class)
                        .doesNotHaveBean(SofaRuntimeContext.class)
                        .doesNotHaveBean(BindingConverterFactory.class)
                        .doesNotHaveBean(BindingAdapterFactory.class)
                        .doesNotHaveBean(AsyncInitMethodManager.class)
                        .doesNotHaveBean(AsyncProxyBeanPostProcessor.class)
                        .doesNotHaveBean(AsyncInitBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(ServiceBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(RuntimeContextBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(ProxyBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(ComponentContextRefreshInterceptor.class));
    }

    @Test
    public void customSofaRuntimeContextProperties() {
        this.contextRunner
                .withPropertyValues("sofa.boot.runtime.skipJvmReferenceHealthCheck=true")
                .withPropertyValues("sofa.boot.runtime.skipExtensionHealthCheck=true")
                .withPropertyValues("sofa.boot.runtime.disableJvmFirst=true")
                .withPropertyValues("sofa.boot.runtime.extensionFailureInsulating=true")
                .withPropertyValues("sofa.boot.runtime.skipAllComponentShutdown=true")
                .withPropertyValues("sofa.boot.runtime.skipCommonComponentShutdown=true")
                .withPropertyValues("sofa.boot.runtime.jvmFilterEnable=true")
                .withPropertyValues("sofa.boot.runtime.serviceInterfaceTypeCheck=true")
                .withPropertyValues("sofa.boot.runtime.skipJvmReferenceHealthCheckList=com.alipay.sofa.isle.sample.facade.SampleJvmService:annotationImpl,com.alipay.sofa.isle.sample.facade.SampleJvmService")
                .withPropertyValues("sofa.boot.runtime.referenceHealthCheckMoreDetailEnable=true")
                .withPropertyValues("sofa.boot.runtime.serviceCanBeDuplicate=false")
                .run((context) -> {
                    SofaRuntimeContext.Properties properties= context.getBean(SofaRuntimeContext.class).getProperties();
                    assertThat(properties.isSkipJvmReferenceHealthCheck()).isTrue();
                    assertThat(properties.isSkipExtensionHealthCheck()).isTrue();
                    assertThat(properties.isDisableJvmFirst()).isTrue();
                    assertThat(properties.isExtensionFailureInsulating()).isTrue();
                    assertThat(properties.isSkipAllComponentShutdown()).isTrue();
                    assertThat(properties.isSkipCommonComponentShutdown()).isTrue();
                    assertThat(properties.isJvmFilterEnable()).isTrue();
                    assertThat(properties.isServiceInterfaceTypeCheck()).isTrue();
                    assertThat(properties.getSkipJvmReferenceHealthCheckList()).containsExactly("com.alipay.sofa.isle.sample.facade.SampleJvmService:annotationImpl", "com.alipay.sofa.isle.sample.facade.SampleJvmService");
                    assertThat(properties.isReferenceHealthCheckMoreDetailEnable()).isTrue();
                    assertThat(properties.isServiceCanBeDuplicate()).isFalse();
                });
    }

    @Test
    public void customAsyncInitMethodManager() {
        this.contextRunner
                .withPropertyValues("sofa.boot.runtime.asyncInitExecutorCoreSize=10")
                .withPropertyValues("sofa.boot.runtime.asyncInitExecutorMaxSize=10")
                .run((context) -> {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) context.getBean(Supplier.class,
                            AsyncInitMethodManager.ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME).get();
                    assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(10);
                    assertThat(threadPoolExecutor.getMaximumPoolSize()).isEqualTo(10);
                });
    }
}
