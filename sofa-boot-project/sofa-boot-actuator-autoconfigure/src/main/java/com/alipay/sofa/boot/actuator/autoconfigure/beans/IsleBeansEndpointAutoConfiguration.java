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
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAvailableCondition;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link BeansEndpoint}.
 *
 * @author huzijie
 * @version IsleBeansEndpointAutoConfiguration.java, v 0.1 2022年03月17日 11:03 AM huzijie Exp $
 */
@AutoConfiguration(before = BeansEndpointAutoConfiguration.class, after = SofaModuleAutoConfiguration.class)
@Conditional(SofaModuleAvailableCondition.class)
@ConditionalOnBean(ApplicationRuntimeModel.class)
@ConditionalOnAvailableEndpoint(endpoint = BeansEndpoint.class)
public class IsleBeansEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BeansEndpoint beansEndpoint(ConfigurableApplicationContext applicationContext,
                                       ApplicationRuntimeModel applicationRuntimeModel) {
        return new IsleBeansEndpoint(applicationContext, applicationRuntimeModel);
    }

}
