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
 * @author chengming
 * @version PolarisRegistryConfigurator.java, v 0.1 2024年02月27日 3:52 PM chengming
 */
public class PolarisRegistryConfigurator implements RegistryConfigureProcessor {

    public PolarisRegistryConfigurator() {
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String polarisAddress = RegistryParseUtil.parseAddress(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_POLARIS);
        Map<String, String> map = RegistryParseUtil.parseParam(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_POLARIS);

        return new RegistryConfig().setAddress(polarisAddress).setParameters(map)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_POLARIS);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_POLARIS;
    }
}
