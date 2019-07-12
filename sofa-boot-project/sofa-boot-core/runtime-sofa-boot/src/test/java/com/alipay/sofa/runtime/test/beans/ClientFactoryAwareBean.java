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
package com.alipay.sofa.runtime.test.beans;

import org.springframework.beans.factory.InitializingBean;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
public class ClientFactoryAwareBean implements ClientFactoryAware, InitializingBean {

    private ClientFactory clientFactory;

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    @Override
    public void afterPropertiesSet() {
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new DefaultSampleService(ClientFactory.class.getName()));
        serviceParam.setInterfaceType(SampleService.class);
        serviceParam.setUniqueId("clientFactory");
        clientFactory.getClient(ServiceClient.class).service(serviceParam);
    }
}