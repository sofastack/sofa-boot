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
package com.alipay.sofa.actuator.autoconfigure.test;

import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorProperties;
import org.springframework.boot.actuate.health.HealthStatusHttpMapper;
import org.springframework.boot.actuate.health.HealthWebEndpointResponseMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huzijie
 * @version TestHealthCheckConfiguration.java, v 0.1 2021年01月05日 12:02 下午 huzijie Exp $
 */
@Configuration
@EnableConfigurationProperties(value = { HealthIndicatorProperties.class,
                                        HealthEndpointProperties.class })
public class TestHealthCheckConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HealthStatusHttpMapper createHealthStatusHttpMapper(HealthIndicatorProperties healthIndicatorProperties) {
        HealthStatusHttpMapper statusHttpMapper = new HealthStatusHttpMapper();
        if (healthIndicatorProperties.getHttpMapping() != null) {
            statusHttpMapper.addStatusMapping(healthIndicatorProperties.getHttpMapping());
        }
        return statusHttpMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthWebEndpointResponseMapper healthWebEndpointResponseMapper(HealthStatusHttpMapper statusHttpMapper,
                                                                           HealthEndpointProperties properties) {
        return new HealthWebEndpointResponseMapper(statusHttpMapper, properties.getShowDetails(),
            properties.getRoles());
    }
}