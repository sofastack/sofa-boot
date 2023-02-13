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
package com.alipay.sofa.boot.actuator.autoconfigure.rpc;

import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.rpc.RpcAfterHealthCheckCallback;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link RpcAfterHealthCheckCallback}.
 *
 * @author huzijie
 * @version RpcActuatorAutoConfiguration.java, v 0.1 2023年02月08日 4:45 PM huzijie Exp $
 */
@AutoConfiguration
@ConditionalOnClass(ProviderConfigContainer.class)
@ConditionalOnBean(ProviderConfigContainer.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class RpcActuatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RpcAfterHealthCheckCallback rpcAfterHealthCheckCallback(RpcStartApplicationListener rpcStartApplicationListener) {
        return new RpcAfterHealthCheckCallback(rpcStartApplicationListener);
    }
}
