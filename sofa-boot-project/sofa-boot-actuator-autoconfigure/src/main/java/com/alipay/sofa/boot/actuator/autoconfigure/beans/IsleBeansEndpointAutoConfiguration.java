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
package com.alipay.sofa.boot.actuator.autoconfigure.beans;

import com.alipay.sofa.boot.actuator.beans.IsleBeansEndpoint;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huzijie
 * @version IsleBeansEndpointAutoConfiguration.java, v 0.1 2022年03月17日 11:03 AM huzijie Exp $
 */
@AutoConfigureBefore(BeansEndpointAutoConfiguration.class)
@AutoConfigureAfter(SofaModuleAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailableEndpoint(endpoint = BeansEndpoint.class)
@ConditionalOnClass(ApplicationRuntimeModel.class)
@ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
public class IsleBeansEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ApplicationRuntimeModel.class)
    public BeansEndpoint beansEndpoint(ConfigurableApplicationContext applicationContext,
                                       ApplicationRuntimeModel applicationRuntimeModel) {
        return new IsleBeansEndpoint(applicationContext, applicationRuntimeModel);
    }

}
