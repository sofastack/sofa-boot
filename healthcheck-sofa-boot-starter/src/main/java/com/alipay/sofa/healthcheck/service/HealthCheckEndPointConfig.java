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
package com.alipay.sofa.healthcheck.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liangen
 * @version $Id: EndConfig.java, v 0.1 2018年02月03日 上午12:30 liangen Exp $
 */
@Configuration
public class HealthCheckEndPointConfig {
    static final String READINESS_CHECK_ENDPOINT_NAME = "sofaboot_health_readiness";

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "com.alipay.sofa.healthcheck.readiness", name = "enabled", matchIfMissing = true)
    public SofaBootReadinessCheckEndpoint readinessCheck() {
        return new SofaBootReadinessCheckEndpoint(READINESS_CHECK_ENDPOINT_NAME, false);
    }

    @Bean
    @ConditionalOnBean(SofaBootReadinessCheckEndpoint.class)
    public SofaBootReadinessCheckMvcEndpoint sofaBootReadinessCheckMvcEndpoint(SofaBootReadinessCheckEndpoint delegate) {
        return new SofaBootReadinessCheckMvcEndpoint(delegate);
    }

}