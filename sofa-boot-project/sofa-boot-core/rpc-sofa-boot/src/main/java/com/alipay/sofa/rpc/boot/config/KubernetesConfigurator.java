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
 * Kubernetes配置
 * <p>
 * com.alipay.sofa.rpc.registry.address=kubernetes://kubernetes.default.svc:8848?k1=v1
 */
public class KubernetesConfigurator implements RegistryConfigureProcessor {

    public KubernetesConfigurator() {
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String kubernetesAddress = RegistryParseUtil.parseAddress(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_KUBERNETES);
        Map<String, String> map = RegistryParseUtil.parseParam(address,
            SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_KUBERNETES);

        return new RegistryConfig().setAddress(kubernetesAddress).setParameters(map)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_KUBERNETES);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_KUBERNETES;
    }

}
