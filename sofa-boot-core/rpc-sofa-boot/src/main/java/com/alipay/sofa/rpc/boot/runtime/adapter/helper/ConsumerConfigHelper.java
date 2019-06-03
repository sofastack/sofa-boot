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
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingMethodInfo;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.MethodConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.core.invoke.SofaResponseCallback;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.hystrix.HystrixConstants;
import com.alipay.sofa.runtime.spi.binding.Contract;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ConsumerConfig 工厂。
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ConsumerConfigHelper {
    private final RegistryConfigContainer registryConfigContainer;
    private final String                  appName;
    private final SofaBootRpcProperties   sofaBootRpcProperties;

    public ConsumerConfigHelper(SofaBootRpcProperties sofaBootRpcProperties,
                                RegistryConfigContainer registryConfigContainer, String appName) {
        this.sofaBootRpcProperties = sofaBootRpcProperties;
        this.registryConfigContainer = registryConfigContainer;
        this.appName = appName;
    }

    /**
     * 获取 ConsumerConfig
     *
     * @param contract the Contract
     * @param binding  the RpcBinding
     * @return the ConsumerConfig
     */
    public ConsumerConfig getConsumerConfig(Contract contract, RpcBinding binding) {
        RpcBindingParam param = binding.getRpcBindingParam();

        String id = binding.getBeanId();
        String interfaceId = contract.getInterfaceType().getName();
        String uniqueId = contract.getUniqueId();

        Integer timeout = param.getTimeout();
        Integer retries = param.getRetries();
        String type = param.getType();
        Integer addressWaitTime = param.getAddressWaitTime();
        Object callbackHandler = param.getCallbackHandler();
        String genericInterface = param.getGenericInterface();
        String loadBalancer = param.getLoadBalancer();
        Boolean lazy = param.getLazy();
        Boolean check = param.getCheck();

        String serialization = param.getSerialization();
        List<Filter> filters = param.getFilters();
        List<MethodConfig> methodConfigs = convertToMethodConfig(param.getMethodInfos());
        String targetUrl = param.getTargetUrl();

        String referenceLimit = sofaBootRpcProperties.getConsumerRepeatedReferenceLimit();

        ConsumerConfig consumerConfig = new ConsumerConfig();
        if (StringUtils.hasText(appName)) {
            consumerConfig.setApplication(new ApplicationConfig().setAppName(appName));
        }
        if (StringUtils.hasText(id)) {
            consumerConfig.setId(id);
        }
        if (StringUtils.hasText(genericInterface)) {
            consumerConfig.setGeneric(true);
            consumerConfig.setInterfaceId(genericInterface);
        } else if (StringUtils.hasText(interfaceId)) {
            consumerConfig.setInterfaceId(interfaceId);
        }
        if (StringUtils.hasText(uniqueId)) {
            consumerConfig.setUniqueId(uniqueId);
        }
        if (timeout != null) {
            consumerConfig.setTimeout(timeout);
        }
        if (retries != null) {
            consumerConfig.setRetries(retries);
        }
        if (StringUtils.hasText(type)) {
            consumerConfig.setInvokeType(type);
        }
        if (addressWaitTime != null) {
            consumerConfig.setAddressWait(addressWaitTime);
        }
        if (StringUtils.hasText(loadBalancer)) {
            consumerConfig.setLoadBalancer(loadBalancer);
        }
        if (lazy != null) {
            consumerConfig.setLazy(lazy);
        }
        if (check != null) {
            consumerConfig.setCheck(check);
        }
        if (callbackHandler != null) {
            if (callbackHandler instanceof SofaResponseCallback) {
                consumerConfig.setOnReturn((SofaResponseCallback) callbackHandler);
            } else {
                throw new SofaBootRpcRuntimeException("callback handler must implement SofaResponseCallback [" +
                    callbackHandler + "]");
            }
        }
        if (!CollectionUtils.isEmpty(filters)) {
            consumerConfig.setFilterRef(filters);
        }
        if (!CollectionUtils.isEmpty(methodConfigs)) {
            consumerConfig.setMethods(methodConfigs);
        }
        if (StringUtils.hasText(targetUrl)) {
            consumerConfig.setDirectUrl(targetUrl);
            consumerConfig.setLazy(true);
            consumerConfig.setSubscribe(false);
            consumerConfig.setRegister(false);
        }
        if (StringUtils.hasText(referenceLimit)) {
            consumerConfig.setRepeatedReferLimit(Integer.valueOf(referenceLimit));
        }

        String protocol = binding.getBindingType().getType();
        consumerConfig.setBootstrap(protocol);

        if (protocol.equals(SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO)) {
            consumerConfig.setInJVM(false);
        }

        if (param.getRegistrys() != null && param.getRegistrys().size() > 0) {
            List<String> registrys = param.getRegistrys();
            for (String registryAlias : registrys) {
                RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig(registryAlias);
                consumerConfig.setRegistry(registryConfig);
            }
        } else if (registryConfigContainer.isMeshEnabled(protocol)) {
            RegistryConfig registryConfig = registryConfigContainer
                .getRegistryConfig(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MESH);
            consumerConfig.setRegistry(registryConfig);
        }
        else {
            RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig();

            consumerConfig.setRegistry(registryConfig);

        }

        if (StringUtils.hasText(serialization)) {
            consumerConfig.setSerialization(serialization);
        }

        if (Boolean.TRUE.toString().equals(sofaBootRpcProperties.getHystrixEnable())) {
            consumerConfig.setParameter(HystrixConstants.SOFA_HYSTRIX_ENABLED, Boolean.TRUE.toString());
        }

        // after sofaBootRpcProperties#getHystrixEnable for override global config
        if (param.getParameters() != null) {
            consumerConfig.setParameters(param.getParameters());
        }

        return consumerConfig.setProtocol(protocol);
    }

    private List<MethodConfig> convertToMethodConfig(List<RpcBindingMethodInfo> methodInfos) {
        List<MethodConfig> methodConfigs = new ArrayList<MethodConfig>();

        if (!CollectionUtils.isEmpty(methodInfos)) {

            for (RpcBindingMethodInfo info : methodInfos) {

                String name = info.getName();
                Integer timeout = info.getTimeout();
                Integer retries = info.getRetries();
                String type = info.getType();
                Object callbackHandler = info.getCallbackHandler();

                MethodConfig methodConfig = new MethodConfig();
                methodConfig.setName(name);
                if (timeout != null) {
                    methodConfig.setTimeout(timeout);
                }
                if (retries != null) {
                    methodConfig.setRetries(retries);
                }
                if (StringUtils.hasText(type)) {
                    methodConfig.setInvokeType(type);
                }
                if (callbackHandler != null) {
                    if (callbackHandler instanceof SofaResponseCallback) {
                        methodConfig.setOnReturn((SofaResponseCallback) callbackHandler);
                    } else {
                        throw new SofaBootRpcRuntimeException("callback handler must implement SofaResponseCallback [" +
                            callbackHandler + "]");
                    }
                }

                methodConfigs.add(methodConfig);
            }

        }

        return methodConfigs;
    }
}