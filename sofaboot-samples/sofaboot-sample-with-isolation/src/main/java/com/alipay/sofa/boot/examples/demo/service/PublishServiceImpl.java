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
package com.alipay.sofa.boot.examples.demo.service;

import com.alipay.sofa.boot.examples.demo.service.facade.PublishService;
import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class PublishServiceImpl implements PublishService {

    @Override
    public void publish() {
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setPort(9696);

        ProviderConfig<SampleService> providerConfig = new ProviderConfig<SampleService>()
            .setInterfaceId(SampleService.class.getName()).setRef(new SampleServiceImpl())
            .setServer(serverConfig);

        providerConfig.export();
    }

}