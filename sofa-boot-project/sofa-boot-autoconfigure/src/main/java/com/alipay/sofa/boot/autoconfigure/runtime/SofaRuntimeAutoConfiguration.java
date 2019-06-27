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
import com.alipay.sofa.runtime.spring.AsyncProxyBeanPostProcessor;
import com.alipay.sofa.runtime.spring.async.AsyncTaskExecutionListener;
import com.alipay.sofa.runtime.spring.initializer.RuntimeContextInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.runtime.spring.aware.DefaultRuntimeShutdownAware;

/**
 * @author xuanbei 18/3/17
 */
@Configuration
@EnableConfigurationProperties(SofaRuntimeConfigurationProperties.class)
@ConditionalOnClass(SofaFramework.class)
public class SofaRuntimeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DefaultRuntimeShutdownAware defaultRuntimeShutdownAware() {
        return new DefaultRuntimeShutdownAware();
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor() {
        return new AsyncProxyBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncTaskExecutionListener asyncTaskExecutionListener() {
        return new AsyncTaskExecutionListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeContextInitializer runtimeContextInitializer() {
        return new RuntimeContextInitializer();
    }
}
