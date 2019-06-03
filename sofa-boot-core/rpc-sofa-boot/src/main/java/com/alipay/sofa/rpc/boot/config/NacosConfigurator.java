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

import java.util.Map;

import com.alipay.sofa.rpc.boot.common.RegistryParseUtil;
import com.alipay.sofa.rpc.config.RegistryConfig;

/**
 * Nacos 配置
 * <p>
 *     配置格式: com.alipay.sofa.rpc.registry.address=nacos://xxx:8848?k1=v1
 * </p>
 * 
 * @author jervyshi
 * @version $Id: NacosConfigurator.java, v 0.1 2018-12-03 15:43 jervyshi Exp $$
 */
public class NacosConfigurator implements RegistryConfigureProcessor {

    public NacosConfigurator() {
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String nacosAddress = RegistryParseUtil.parseAddress(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_NACOS);
        Map<String, String> map = RegistryParseUtil.parseParam(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_NACOS);

        return new RegistryConfig().setAddress(nacosAddress).setParameters(map)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_NACOS);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_NACOS;
    }
}