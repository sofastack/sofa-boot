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
package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpointWebExtension;
import com.alipay.sofa.boot.actuator.health.ReadinessHttpCodeStatusMapper;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link ReadinessEndpoint}.
 *
 * @author huzijie
 * @version HealthEndpointAutoConfiguration.java, v 0.1 2022年12月29日 4:52 PM huzijie Exp $
 */
@AutoConfiguration(before = HealthEndpointAutoConfiguration.class, after = ReadinessAutoConfiguration.class)
@ConditionalOnBean(ReadinessCheckListener.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class ReadinessEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ReadinessCheckListener.class)
    public ReadinessEndpoint readinessEndpoint(ReadinessCheckListener readinessCheckListener) {
        return new ReadinessEndpoint(readinessCheckListener);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpointWebExtension.class)
    public ReadinessEndpointWebExtension readinessEndpointWebExtension(ReadinessEndpoint readinessEndpoint,
                                                                       HttpCodeStatusMapper sofaHttpCodeStatusMapper) {
        return new ReadinessEndpointWebExtension(readinessEndpoint, sofaHttpCodeStatusMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpCodeStatusMapper readinessHttpCodeStatusMapper(HealthProperties healthProperties) {
        return new ReadinessHttpCodeStatusMapper(healthProperties.getStatus().getHttpMapping());
    }
}
