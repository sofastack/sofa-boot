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
package com.alipay.sofa.boot.actuator.autoconfigure.components;

import com.alipay.sofa.boot.actuator.components.ComponentsEndpoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link ComponentsEndpoint}.
 *
 * @author huzijie
 * @version ComponentsEndpointAutoConfiguration.java, v 0.1 2022年03月17日 3:56 PM huzijie Exp $
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@ConditionalOnClass(SofaRuntimeContext.class)
@ConditionalOnBean(SofaRuntimeContext.class)
@ConditionalOnAvailableEndpoint(endpoint = ComponentsEndpoint.class)
public class ComponentsEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SofaRuntimeContext.class)
    public ComponentsEndpoint sofaBootComponentsEndPoint(SofaRuntimeContext sofaRuntimeContext) {
        return new ComponentsEndpoint(sofaRuntimeContext);
    }

}
