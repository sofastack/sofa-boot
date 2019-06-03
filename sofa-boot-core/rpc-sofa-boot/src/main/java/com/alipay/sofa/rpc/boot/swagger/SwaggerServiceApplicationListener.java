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

package com.alipay.sofa.rpc.boot.swagger;

import com.alipay.sofa.rpc.boot.runtime.param.RestBindingParam;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

public class SwaggerServiceApplicationListener implements ApplicationListener<ApplicationStartedEvent>,
                                              ClientFactoryAware {
    private ClientFactory clientFactory;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<BindingParam> bindingParams = new ArrayList<>();
        bindingParams.add(new RestBindingParam());

        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInterfaceType(SwaggerService.class);
        serviceParam.setInstance(new SwaggerServiceImpl());
        serviceParam.setBindingParams(bindingParams);

        ServiceClient serviceClient = clientFactory.getClient(ServiceClient.class);
        serviceClient.service(serviceParam);
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
