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
package com.alipay.sofa.rpc.boot.container;

import com.alipay.sofa.common.config.SofaConfigs;
import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigKeys;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.log.LogCodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistryConfig 工厂
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class RegistryConfigContainer {

    private static final String                     DEFAULT_REGISTRY = "DEFAULT";

    private Map<String, RegistryConfigureProcessor> registryConfigMap;

    /**
     * for cache
     */
    private final Map<String, RegistryConfig>       registryConfigs  = new ConcurrentHashMap<String, RegistryConfig>();

    /**
     * for custom extends
     */
    private final String                            customDefaultRegistry;

    /**
     * for default address for  customDefaultRegistry
     */
    private String                                  customDefaultRegistryAddress;

    private Map<String, String>                     registries       = new HashMap<>();

    private String                                  defaultRegistryAddress;

    private String                                  meshConfig;

    private boolean                                 ignoreRegistry;

    public RegistryConfigContainer(Map<String, RegistryConfigureProcessor> registryConfigMap) {
        this.registryConfigMap = registryConfigMap;
        customDefaultRegistry = SofaConfigs.getOrDefault(SofaBootRpcConfigKeys.DEFAULT_REGISTRY);
        if (StringUtils.isNotBlank(customDefaultRegistry)) {
            customDefaultRegistryAddress = System.getProperty(customDefaultRegistry);
        }
    }

    /**
     * @param registryAlias
     * @return
     * @throws SofaBootRpcRuntimeException
     */
    public RegistryConfig getRegistryConfig(String registryAlias)
                                                                 throws SofaBootRpcRuntimeException {
        RegistryConfig registryConfig;
        String registryProtocol;
        String registryAddress;

        String currentDefaultAlias;

        if (StringUtils.isNotBlank(customDefaultRegistry)) {
            currentDefaultAlias = customDefaultRegistry;
        } else {
            currentDefaultAlias = DEFAULT_REGISTRY;
        }

        if (StringUtils.isEmpty(registryAlias)) {
            registryAlias = currentDefaultAlias;
        }

        //cloud be mesh,default,zk

        if (registryConfigs.get(registryAlias) != null) {
            return registryConfigs.get(registryAlias);
        }

        // just for new address
        if (DEFAULT_REGISTRY.equalsIgnoreCase(registryAlias)) {
            registryAddress = defaultRegistryAddress;
        } else if (registryAlias.equals(customDefaultRegistry)) {
            registryAddress = customDefaultRegistryAddress;
        } else {
            registryAddress = registries.get(registryAlias);
        }

        //for worst condition.
        if (StringUtils.isBlank(registryAddress)) {
            registryProtocol = SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL;
        } else {
            final int endIndex = registryAddress.indexOf("://");
            if (endIndex != -1) {
                registryProtocol = registryAddress.substring(0, endIndex);
            } else {
                registryProtocol = registryAlias;
            }
        }

        if (registryConfigMap.get(registryProtocol) != null) {
            RegistryConfigureProcessor registryConfigureProcessor = registryConfigMap
                .get(registryProtocol);
            registryConfig = registryConfigureProcessor.buildFromAddress(registryAddress);
            registryConfigs.put(registryAlias, registryConfig);
            //不再处理以.分隔的.
            if (ignoreRegistry) {
                registryConfig.setRegister(false);
            }
            return registryConfig;
        } else {
            throw new SofaBootRpcRuntimeException(LogCodes.getLog(
                LogCodes.ERROR_REGISTRY_NOT_SUPPORT, registryAddress));
        }
    }

    /**
     * 获取 RegistryConfig
     *
     * @return the RegistryConfig
     * @throws SofaBootRpcRuntimeException SofaBoot运行时异常
     */
    public RegistryConfig getRegistryConfig() throws SofaBootRpcRuntimeException {

        if (StringUtils.isNotBlank(customDefaultRegistry)) {
            return getRegistryConfig(customDefaultRegistry);
        } else {
            return getRegistryConfig(DEFAULT_REGISTRY);
        }
    }

    /**
     * 移除所有 RegistryConfig
     */
    public void removeAllRegistryConfig() {
        registryConfigMap.clear();
    }

    public Map<String, RegistryConfigureProcessor> getRegistryConfigMap() {
        return registryConfigMap;
    }

    public void setRegistryConfigMap(Map<String, RegistryConfigureProcessor> registryConfigMap) {
        this.registryConfigMap = registryConfigMap;
    }

    public Map<String, RegistryConfig> getRegistryConfigs() {
        return registryConfigs;
    }

    /**
     * protocol can be meshed
     *
     * @param protocol
     * @return
     */
    public boolean isMeshEnabled(String protocol) {
        if (StringUtils.isNotBlank(meshConfig) && registries != null
            && registries.get(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MESH) != null) {
            if (meshConfig.equalsIgnoreCase(SofaBootRpcConfigConstants.ENABLE_MESH_ALL)) {
                return true;
            } else {
                List<String> meshEnableProtocols = Arrays.asList(meshConfig.split(","));
                for (String meshProtocol : meshEnableProtocols) {
                    if (StringUtils.equals(meshProtocol, protocol)) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public void setRegistries(Map<String, String> registries) {
        this.registries = registries;
    }

    public void setDefaultRegistryAddress(String defaultRegistryAddress) {
        this.defaultRegistryAddress = defaultRegistryAddress;
    }

    public void setMeshConfig(String meshConfig) {
        this.meshConfig = meshConfig;
    }

    public void setIgnoreRegistry(boolean ignoreRegistry) {
        this.ignoreRegistry = ignoreRegistry;
    }
}
