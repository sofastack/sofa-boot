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
package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import com.alipay.sofa.boot.actuator.startup.BeanCostBeanPostProcessor;
import com.alipay.sofa.boot.actuator.startup.StartupContextRefreshedListener;
import com.alipay.sofa.boot.actuator.startup.StartupEndPoint;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for startup components.
 *
 * @author Zhijie
 * @since 2020/7/8
 */
@AutoConfiguration
@EnableConfigurationProperties(StartupProperties.class)
@ConditionalOnAvailableEndpoint(endpoint = StartupEndPoint.class)
public class StartupAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StartupReporter sofaStartupReporter(Environment environment) {
        return new StartupReporter(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public static BeanCostBeanPostProcessor beanCostBeanPostProcessor(StartupProperties startupProperties) {
        return new BeanCostBeanPostProcessor(startupProperties.getBeanInitCostThreshold(),
            startupProperties.isSkipSofaBean());
    }

    @Bean
    @ConditionalOnMissingBean
    public StartupContextRefreshedListener startupContextRefreshedListener() {
        return new StartupContextRefreshedListener();
    }

}
