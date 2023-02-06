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
package com.alipay.sofa.boot.autoconfigure.rpc;

import com.alipay.sofa.rpc.boot.swagger.BoltSwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.boot.swagger.SwaggerServiceApplicationListener;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import io.swagger.models.Swagger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger Configuration
 *
 * @author yuanxuan
 * @version : SwaggerConfiguration.java, v 0.1 2023年01月31日 17:35 yuanxuan Exp $
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Swagger.class)
public class SwaggerConfiguration {

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.rest-swagger", havingValue = "true")
    @ConditionalOnBean(SofaRuntimeManager.class)
    public ApplicationListener<ApplicationStartedEvent> swaggerServiceApplicationListener(SofaRuntimeManager sofaRuntimeManager) {
        return new SwaggerServiceApplicationListener(sofaRuntimeManager);
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.enable-swagger", havingValue = "true")
    @ConditionalOnBean(SofaRuntimeManager.class)
    public ApplicationListener<ApplicationStartedEvent> boltSwaggerServiceApplicationListener(SofaRuntimeManager sofaRuntimeManager) {
        return new BoltSwaggerServiceApplicationListener(sofaRuntimeManager);
    }
}
