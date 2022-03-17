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

import com.alipay.sofa.boot.actuator.components.SofaBootComponentsEndPoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huzijie
 * @version ComponentsEndpointAutoConfiguration.java, v 0.1 2022年03月17日 3:56 PM huzijie Exp $
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailableEndpoint(endpoint = SofaBootComponentsEndPoint.class)
@ConditionalOnClass(SofaFramework.class)
@AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
public class ComponentsEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SofaBootComponentsEndPoint sofaBootComponentsEndPoint(SofaRuntimeContext sofaRuntimeContext) {
        return new SofaBootComponentsEndPoint(sofaRuntimeContext);
    }

}
