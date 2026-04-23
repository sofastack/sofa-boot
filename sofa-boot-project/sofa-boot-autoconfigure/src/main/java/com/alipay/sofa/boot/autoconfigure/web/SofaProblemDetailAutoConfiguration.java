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
package com.alipay.sofa.boot.autoconfigure.web;

import com.alipay.sofa.boot.autoconfigure.rpc.SofaRpcAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Auto-configuration for SOFA ProblemDetail exception handling.
 */
@AutoConfiguration(after = { SofaRuntimeAutoConfiguration.class, SofaRpcAutoConfiguration.class })
@ConditionalOnClass({ ProblemDetail.class, ResponseEntityExceptionHandler.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = SofaProblemDetailProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SofaProblemDetailProperties.class)
public class SofaProblemDetailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ResponseEntityExceptionHandler.class, ignored = {
            SofaRuntimeProblemDetailExceptionHandler.class,
            SofaRpcProblemDetailExceptionHandler.class })
    public SofaProblemDetailExceptionHandler sofaProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                                                               Environment environment) {
        return new SofaProblemDetailExceptionHandler(properties, environment);
    }

    @Bean
    @ConditionalOnClass(name = "com.alipay.sofa.runtime.api.ServiceRuntimeException")
    @ConditionalOnMissingBean(SofaRuntimeProblemDetailExceptionHandler.class)
    public SofaRuntimeProblemDetailExceptionHandler sofaRuntimeProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                                                                             Environment environment) {
        return new SofaRuntimeProblemDetailExceptionHandler(properties, environment);
    }

    @Bean
    @ConditionalOnClass(name = { "com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException",
            "com.alipay.sofa.rpc.core.exception.SofaRpcException",
            "com.alipay.sofa.rpc.core.exception.RpcErrorType" })
    @ConditionalOnMissingBean(SofaRpcProblemDetailExceptionHandler.class)
    public SofaRpcProblemDetailExceptionHandler sofaRpcProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                                                                     Environment environment) {
        return new SofaRpcProblemDetailExceptionHandler(properties, environment);
    }
}
