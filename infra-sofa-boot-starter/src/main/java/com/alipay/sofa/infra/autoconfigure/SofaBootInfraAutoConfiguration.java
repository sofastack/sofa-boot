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
package com.alipay.sofa.infra.autoconfigure;

import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpoint;
import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpointMvcAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * @author yangguanchao
 */
@Configuration
public class SofaBootInfraAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "com.alipay.sofa.versions", name = "enabled", matchIfMissing = true)
    public SofaBootVersionEndpoint sofaBootVersionEndpoint() {
        return new SofaBootVersionEndpoint();
    }

    @Bean
    @ConditionalOnBean(SofaBootVersionEndpoint.class)
    public SofaBootVersionEndpointMvcAdapter sofaBootVersionEndpointMvcAdapter(SofaBootVersionEndpoint sofaBootVersionEndpoint) {
        return new SofaBootVersionEndpointMvcAdapter(sofaBootVersionEndpoint);
    }
}
