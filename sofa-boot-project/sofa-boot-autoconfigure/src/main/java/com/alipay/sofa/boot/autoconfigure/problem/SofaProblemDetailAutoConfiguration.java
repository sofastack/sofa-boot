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
package com.alipay.sofa.boot.autoconfigure.problem;

import com.alipay.sofa.boot.autoconfigure.rpc.SofaRpcAutoConfiguration;
import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ProblemDetail;

import javax.ws.rs.ext.ExceptionMapper;

/**
 * Auto-configuration for SOFABoot problem detail support.
 *
 * @author OpenAI
 */
@AutoConfiguration(after = SofaRpcAutoConfiguration.class)
@ConditionalOnClass(ProblemDetail.class)
@ConditionalOnProperty(prefix = "sofa.boot.problem-detail", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SofaProblemDetailProperties.class)
public class SofaProblemDetailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SofaProblemDetailFactory sofaProblemDetailFactory(SofaProblemDetailProperties properties,
                                                             ObjectProvider<MessageSource> messageSource,
                                                             Environment environment) {
        return new SofaProblemDetailFactory(properties, messageSource.getIfAvailable(), environment);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletProblemDetailConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SofaProblemDetailExceptionHandler sofaProblemDetailExceptionHandler(SofaProblemDetailFactory factory) {
            return new SofaProblemDetailExceptionHandler(factory);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ ExceptionMapper.class, JAXRSProviderManager.class })
    static class JaxrsProblemDetailConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SofaRpcExceptionProblemDetailExceptionMapper sofaRpcExceptionProblemDetailExceptionMapper(SofaProblemDetailFactory factory) {
            SofaRpcExceptionProblemDetailExceptionMapper mapper = new SofaRpcExceptionProblemDetailExceptionMapper(
                factory);
            JAXRSProviderManager.registerCustomProviderInstance(mapper);
            return mapper;
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaRpcRuntimeExceptionProblemDetailExceptionMapper sofaRpcRuntimeExceptionProblemDetailExceptionMapper(SofaProblemDetailFactory factory) {
            SofaRpcRuntimeExceptionProblemDetailExceptionMapper mapper = new SofaRpcRuntimeExceptionProblemDetailExceptionMapper(
                factory);
            JAXRSProviderManager.registerCustomProviderInstance(mapper);
            return mapper;
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaBootRpcRuntimeExceptionProblemDetailExceptionMapper sofaBootRpcRuntimeExceptionProblemDetailExceptionMapper(SofaProblemDetailFactory factory) {
            SofaBootRpcRuntimeExceptionProblemDetailExceptionMapper mapper = new SofaBootRpcRuntimeExceptionProblemDetailExceptionMapper(
                factory);
            JAXRSProviderManager.registerCustomProviderInstance(mapper);
            return mapper;
        }
    }
}
