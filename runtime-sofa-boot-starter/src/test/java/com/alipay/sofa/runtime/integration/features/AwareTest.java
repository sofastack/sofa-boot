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
package com.alipay.sofa.runtime.integration.features;

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
@Component
public class AwareTest implements ClientFactoryAware, SofaRuntimeContextAware,
                      ApplicationContextAware {

    private ClientFactory      clientFactoryAware;

    private SofaRuntimeContext sofaRuntimeContext;

    private ApplicationContext applicationContext;

    @SofaClientFactory
    private ClientFactory      clientFactory;

    @SofaClientFactory
    private ServiceClient      serviceClient;

    @SofaClientFactory
    private ReferenceClient    referenceClient;

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactoryAware = clientFactory;
    }

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ClientFactory getClientFactoryAware() {
        return clientFactoryAware;
    }

    public SofaRuntimeContext getSofaRuntimeContext() {
        return sofaRuntimeContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public ServiceClient getServiceClient() {
        return serviceClient;
    }

    public ReferenceClient getReferenceClient() {
        return referenceClient;
    }
}