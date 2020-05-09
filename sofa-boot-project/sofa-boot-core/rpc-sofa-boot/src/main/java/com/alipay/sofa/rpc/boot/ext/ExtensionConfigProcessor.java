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
package com.alipay.sofa.rpc.boot.ext;

import com.alipay.sofa.rpc.boot.config.ExtensionProperties;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderConfigProcessor;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhaowang
 * @version : ExtensionConfigProcessor.java, v 0.1 2020年05月09日 2:12 下午 zhaowang Exp $
 */
public class ExtensionConfigProcessor implements ConsumerConfigProcessor, ProviderConfigProcessor {

    @Autowired
    private ExtensionProperties properties;

    @Override
    public void processorConsumer(ConsumerConfig consumerConfig) {
        String addressHolder = properties.getAddressHolder();
        if (StringUtils.isNotBlank(addressHolder)) {
            consumerConfig.setAddressHolder(addressHolder);
        }

        String cluster = properties.getCluster();
        if (StringUtils.isNotBlank(cluster)) {
            consumerConfig.setCluster(cluster);
        }

        String connectionHolder = properties.getConnectionHolder();
        if (StringUtils.isNotBlank(connectionHolder)) {
            consumerConfig.setConnectionHolder(connectionHolder);
        }

        String loadBalancer = properties.getLoadBalancer();
        if (StringUtils.isNotBlank(loadBalancer)) {
            consumerConfig.setLoadBalancer(loadBalancer);
        }

        String proxy = properties.getProxy();
        if (StringUtils.isNotBlank(proxy)) {
            consumerConfig.setProxy(proxy);
        }
    }

    @Override
    public void processorProvider(ProviderConfig providerConfig) {
        String proxy = properties.getProxy();
        if (StringUtils.isNotBlank(proxy)) {
            providerConfig.setProxy(proxy);
        }
    }
}