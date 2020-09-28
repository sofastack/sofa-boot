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
package com.alipay.sofa.rpc.boot.config;

import com.alipay.sofa.rpc.boot.common.RegistryParseUtil;
import com.alipay.sofa.rpc.config.RegistryConfig;

import java.util.Map;

/**
 * zookeeper 配置
 * <p>
 * 配置格式: com.alipay.sofa.rpc.registry.address=multicast://xxx:2181?k1=v1
 *
 * @author zhaowang
 * @version : MulticastConfigurator.java, v 0.1 2020年03月05日 11:25 上午 zhaowang Exp $
 */
public class MulticastConfigurator implements RegistryConfigureProcessor {

    public MulticastConfigurator() {
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String multicastAddress = RegistryParseUtil.parseAddress(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST);
        Map<String, String> map = RegistryParseUtil.parseParam(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST);

        return new RegistryConfig().setAddress(multicastAddress)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST).setParameters(map);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST;
    }

}