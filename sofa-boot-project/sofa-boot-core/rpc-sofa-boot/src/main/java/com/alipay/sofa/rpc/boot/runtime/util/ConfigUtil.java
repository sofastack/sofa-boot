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
package com.alipay.sofa.rpc.boot.runtime.util;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.config.AbstractInterfaceConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;

import java.util.List;

/**
 * Utility method for registry operation for ProviderConfig and ConsumerConfig.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/15
 */
public class ConfigUtil {
    public static void setRegistry(RpcBindingParam param,
                                   RegistryConfigContainer registryConfigContainer,
                                   AbstractInterfaceConfig<?, ?> config, String protocol) {
        if (param.getRegistrys() != null && param.getRegistrys().size() > 0) {
            List<String> registries = param.getRegistrys();
            for (String registryAlias : registries) {
                RegistryConfig registryConfig = registryConfigContainer
                    .getRegistryConfig(registryAlias);
                config.setRegistry(registryConfig);
            }
        } else if (registryConfigContainer.isMeshEnabled(protocol)) {
            RegistryConfig registryConfig = registryConfigContainer
                .getRegistryConfig(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MESH);
            config.setRegistry(registryConfig);
        } else {
            RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig();
            config.setRegistry(registryConfig);
        }
    }
}
