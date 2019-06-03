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
package com.alipay.sofa.rpc.boot.runtime.adapter.helper;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingMethodInfo;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.client.ProviderInfoAttrs;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConfigUniqueNameGenerator;
import com.alipay.sofa.rpc.config.MethodConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.config.UserThreadPoolManager;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.server.UserThreadPool;
import com.alipay.sofa.runtime.spi.binding.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ProviderConfig 工厂。
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ProviderConfigHelper {
    @Autowired
    private ServerConfigContainer   serverConfigContainer;
    @Autowired
    private RegistryConfigContainer registryConfigContainer;
    @Value("${" + SofaBootRpcConfigConstants.APP_NAME + "}")
    private String                  appName;

    /**
     * 获取 ProviderConfig
     *
     * @param contract the Contract
     * @param binding  the RpcBinding
     * @param target   服务实例
     * @return the ProviderConfig
     * @throws SofaBootRpcRuntimeException
     */
    public ProviderConfig getProviderConfig(Contract contract, RpcBinding binding, Object target)
        throws SofaBootRpcRuntimeException {
        RpcBindingParam param = binding.getRpcBindingParam();

        String id = binding.getBeanId();
        String interfaceId = contract.getInterfaceType().getName();
        Object ref = target;
        String uniqueId = contract.getUniqueId();
        Integer timeout = param.getTimeout();
        Integer weight = param.getWeight();
        Integer warmupTime = param.getWarmUpTime();
        Integer warmupWeight = param.getWarmUpWeight();
        UserThreadPool threadPool = param.getUserThreadPool();
        String serialization = param.getSerialization();
        List<Filter> filters = param.getFilters();
        List<MethodConfig> methodConfigs = convertToMethodConfig(param.getMethodInfos());

        ServerConfig serverConfig = serverConfigContainer.getServerConfig(binding.getBindingType().getType());

        ProviderConfig providerConfig = new ProviderConfig();
        if (StringUtils.hasText(appName)) {
            providerConfig.setApplication(new ApplicationConfig().setAppName(appName));
        }
        if (StringUtils.hasText(id)) {
            providerConfig.setId(id);
        }
        if (StringUtils.hasText(interfaceId)) {
            providerConfig.setInterfaceId(interfaceId);
        }
        if (ref != null) {
            providerConfig.setRef(ref);
        }
        if (StringUtils.hasText(uniqueId)) {
            providerConfig.setUniqueId(uniqueId);
        }
        if (timeout != null) {
            providerConfig.setTimeout(timeout);
        }
        if (weight != null) {
            providerConfig.setWeight(weight);
        }
        if (warmupTime != null) {
            providerConfig.setParameter(ProviderInfoAttrs.ATTR_WARMUP_TIME, String.valueOf(warmupTime));
        }
        if (warmupWeight != null) {
            providerConfig.setParameter(ProviderInfoAttrs.ATTR_WARMUP_WEIGHT, String.valueOf(warmupWeight));
        }
        if (!CollectionUtils.isEmpty(filters)) {
            providerConfig.setFilterRef(filters);
        }
        if (!CollectionUtils.isEmpty(methodConfigs)) {
            providerConfig.setMethods(methodConfigs);
        }
        if (threadPool != null) {
            UserThreadPoolManager.registerUserThread(ConfigUniqueNameGenerator.getUniqueName(providerConfig),
                threadPool);
        }

        providerConfig.setServer(serverConfig);

        String protocol = binding.getBindingType().getType();
        providerConfig.setBootstrap(protocol);

        if (StringUtils.hasText(serialization)) {
            providerConfig.setSerialization(serialization);
        }

        if (param.getParameters() != null) {
            providerConfig.setParameters(param.getParameters());
        }

        if (param.getRegistrys() != null && param.getRegistrys().size() > 0) {
            List<String> registrys = param.getRegistrys();
            for (String registryAlias : registrys) {
                RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig(registryAlias);
                providerConfig.setRegistry(registryConfig);
            }
        }
        else if (registryConfigContainer.isMeshEnabled(protocol)) {
            RegistryConfig registryConfig = registryConfigContainer
                .getRegistryConfig(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MESH);
            providerConfig.setRegistry(registryConfig);
        }
        else {
            RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig();

            providerConfig.setRegistry(registryConfig);

        }

        providerConfig.setRegister(false);

        return providerConfig;
    }

    private List<MethodConfig> convertToMethodConfig(List<RpcBindingMethodInfo> methodInfos) {
        List<MethodConfig> methodConfigs = new ArrayList<MethodConfig>();

        if (!CollectionUtils.isEmpty(methodInfos)) {

            for (RpcBindingMethodInfo info : methodInfos) {

                String name = info.getName();
                Integer timeout = info.getTimeout();

                MethodConfig methodConfig = new MethodConfig();
                methodConfig.setName(name);
                if (timeout != null) {
                    methodConfig.setTimeout(timeout);
                }

                methodConfigs.add(methodConfig);
            }

        }

        return methodConfigs;
    }
}