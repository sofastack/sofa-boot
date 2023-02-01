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
package com.alipay.sofa.boot.autoconfigure.rpc;

import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.List;

/**
 * rest filter configuration
 *
 * @author yuanxuan
 * @version : RestFilterConfiguration.java, v 0.1 2023年02月01日 14:02 yuanxuan Exp $
 */
public class RestFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public List<ContainerRequestFilter> containerRequestFilters(List<ContainerRequestFilter> containerRequestFilters) {

        for (ContainerRequestFilter filter : containerRequestFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return containerRequestFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ContainerResponseFilter> containerResponseFilters(List<ContainerResponseFilter> containerResponseFilters) {

        for (ContainerResponseFilter filter : containerResponseFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return containerResponseFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ClientRequestFilter> clientRequestFilters(List<ClientRequestFilter> clientRequestFilters) {

        for (ClientRequestFilter filter : clientRequestFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return clientRequestFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ClientResponseFilter> clientResponseFilters(List<ClientResponseFilter> clientResponseFilters) {

        for (ClientResponseFilter filter : clientResponseFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return clientResponseFilters;
    }
}
