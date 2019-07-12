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
package com.alipay.sofa.isle.sample;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;

/**
 * @author xuanbei 18/5/5
 */
public class PublishServiceWithClient implements ClientFactoryAware {
    private ClientFactory clientFactory;

    public void init() {
        ServiceClient serviceClient = clientFactory.getClient(ServiceClient.class);
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new SampleJvmServiceImpl(
            "Hello, jvm service service client implementation."));
        serviceParam.setInterfaceType(SampleJvmService.class);
        serviceParam.setUniqueId("serviceClientImpl");
        serviceClient.service(serviceParam);
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
