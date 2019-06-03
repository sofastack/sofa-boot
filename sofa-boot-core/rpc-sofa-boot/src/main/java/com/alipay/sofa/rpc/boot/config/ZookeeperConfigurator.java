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
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;

import java.util.Map;

/**
 * zookeeper 配置
 * <p>
 * 配置格式: com.alipay.sofa.rpc.registry.address=zookeeper://xxx:2181?k1=v1
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ZookeeperConfigurator implements RegistryConfigureProcessor {

    public ZookeeperConfigurator() {
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String zkAddress = RegistryParseUtil.parseAddress(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER);
        Map<String, String> map = RegistryParseUtil.parseParam(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER);

        String file = map.get("file");

        if (StringUtils.isEmpty(file)) {
            file = SofaBootRpcConfigConstants.REGISTRY_FILE_PATH_DEFAULT;
        }

        return new RegistryConfig().setAddress(zkAddress).setFile(file)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER)
            .setParameters(map);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER;
    }
}