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
package com.alipay.sofa.boot.actuator.autoconfigure.diagnostic;

import com.alipay.sofa.boot.actuator.diagnostic.SofaDiagnosticEndpoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SofaDiagnosticEndpoint}.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticEndpointAutoConfiguration.java, v 0.1 2026年04月01日 xiaosiyuan Exp $
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@ConditionalOnClass({ SofaRuntimeContext.class, RpcRuntimeContext.class })
@ConditionalOnBean(SofaRuntimeContext.class)
@ConditionalOnAvailableEndpoint(endpoint = SofaDiagnosticEndpoint.class)
public class SofaDiagnosticEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "management.endpoint.sofa-diagnostic", name = "sensitive", havingValue = "false")
    public SofaDiagnosticEndpoint publicSofaDiagnosticEndpoint(SofaRuntimeContext sofaRuntimeContext,
                                                               ApplicationContext applicationContext) {
        return createEndpoint(sofaRuntimeContext, applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SecurityFilterChain.class)
    @ConditionalOnProperty(prefix = "management.endpoint.sofa-diagnostic", name = "sensitive", havingValue = "true", matchIfMissing = true)
    public SofaDiagnosticEndpoint secureSofaDiagnosticEndpoint(SofaRuntimeContext sofaRuntimeContext,
                                                               ApplicationContext applicationContext) {
        return createEndpoint(sofaRuntimeContext, applicationContext);
    }

    private SofaDiagnosticEndpoint createEndpoint(SofaRuntimeContext sofaRuntimeContext,
                                                  ApplicationContext applicationContext) {
        return new SofaDiagnosticEndpoint(sofaRuntimeContext, ThreadPoolGovernor.getInstance(),
            applicationContext);
    }
}
