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
package com.alipay.sofa.rpc.boot.runtime.adapter;

import com.alipay.sofa.rpc.boot.container.SpringBridge;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.log.LogCodes;
import com.alipay.sofa.rpc.registry.Registry;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

import java.util.List;

/**
 * 根据 {@link RpcBindingParam} 生效上层配置到 SOFARPC 组件中。
 *
 * @author <a href="mailto:caojie.cj@antfin.com">CaoJie</a>
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public abstract class RpcBindingAdapter implements BindingAdapter<RpcBinding> {

    /**
     * pre out binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    @Override
    public void preOutBinding(Object contract, RpcBinding binding, Object target, SofaRuntimeContext sofaRuntimeContext) {
        String uniqueName = SpringBridge.getProviderConfigContainer().createUniqueName((Contract) contract, binding);
        ProviderConfig providerConfig = SpringBridge.getProviderConfigHelper().getProviderConfig((Contract) contract,
            binding, target);
        try {
            SpringBridge.getProviderConfigContainer().addProviderConfig(uniqueName, providerConfig);
        } catch (Exception e) {
            throw new ServiceRuntimeException(LogCodes.getLog(LogCodes.ERROR_PROXY_PUBLISH_FAIL), e);
        }
    }

    /**
     * out binding, out binding means provide service
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     * @return binding result
     */
    @Override
    public Object outBinding(Object contract, RpcBinding binding, Object target, SofaRuntimeContext sofaRuntimeContext) {

        String uniqueName = SpringBridge.getProviderConfigContainer().createUniqueName((Contract) contract, binding);
        ProviderConfig providerConfig = SpringBridge.getProviderConfigContainer().getProviderConfig(uniqueName);

        if (providerConfig == null) {
            throw new ServiceRuntimeException(LogCodes.getLog(LogCodes.INFO_SERVICE_METADATA_IS_NULL, uniqueName));
        }

        try {
            providerConfig.export();
        } catch (Exception e) {
            throw new ServiceRuntimeException(LogCodes.getLog(LogCodes.ERROR_PROXY_PUBLISH_FAIL), e);
        }

        if (SpringBridge.getProviderConfigContainer().isAllowPublish()) {
            providerConfig.setRegister(true);
            List<RegistryConfig> registrys = providerConfig.getRegistry();
            for (RegistryConfig registryConfig : registrys) {
                Registry registry = RegistryFactory.getRegistry(registryConfig);
                registry.init();
                registry.start();
                registry.register(providerConfig);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * pre unout binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    @Override
    public void preUnoutBinding(Object contract, RpcBinding binding, Object target,
                                SofaRuntimeContext sofaRuntimeContext) {
        ProviderConfig providerConfig = SpringBridge.getProviderConfigHelper().getProviderConfig((Contract) contract,
            binding,
            target);
        try {
            providerConfig.unExport();
        } catch (Exception e) {
            throw new ServiceRuntimeException(
                LogCodes.getLog(LogCodes.ERROR_PROXY_PRE_UNPUBLISH_FAIL), e);
        }
    }

    /**
     * post unout binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    @Override
    public void postUnoutBinding(Object contract, RpcBinding binding, Object target,
                                 SofaRuntimeContext sofaRuntimeContext) {
        ProviderConfig metadata = SpringBridge.getProviderConfigHelper().getProviderConfig((Contract) contract,
            binding, target);
        try {
            String key = SpringBridge.getProviderConfigContainer().createUniqueName((Contract) contract, binding);
            List<ServerConfig> servers = SpringBridge.getProviderConfigContainer().getProviderConfig(key).getServer();
            for (ServerConfig server : servers) {
                server.getServer().unRegisterProcessor(metadata, false);
            }
            SpringBridge.getProviderConfigContainer().removeProviderConfig(key);
        } catch (Exception e) {
            throw new ServiceRuntimeException(
                LogCodes.getLog(LogCodes.ERROR_PROXY_POST_UNPUBLISH_FAIL), e);
        }
    }

    /**
     * in binding, in binding means reference service
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param sofaRuntimeContext sofa runtime context
     */
    @Override
    public Object inBinding(Object contract, RpcBinding binding, SofaRuntimeContext sofaRuntimeContext) {
        ConsumerConfig consumerConfig = SpringBridge.getConsumerConfigHelper().getConsumerConfig((Contract) contract,
            binding);
        SpringBridge.getConsumerConfigContainer().addConsumerConfig(binding, consumerConfig);

        try {
            Object result = consumerConfig.refer();
            binding.setConsumerConfig(consumerConfig);
            return result;
        } catch (Exception e) {
            throw new ServiceRuntimeException(LogCodes.getLog(LogCodes.ERROR_PROXY_CONSUME_FAIL), e);
        }
    }

    /**
     * undo in binding
     *
     * @param contract
     * @param binding
     * @param sofaRuntimeContext
     */
    @Override
    public void unInBinding(Object contract, RpcBinding binding, SofaRuntimeContext sofaRuntimeContext) {
        try {
            SpringBridge.getConsumerConfigContainer().removeAndUnReferConsumerConfig(binding);
        } catch (Exception e) {
            throw new ServiceRuntimeException(
                LogCodes.getLog(LogCodes.ERROR_PROXY_UNCOSUME_FAIL), e);
        }
    }

    /**
     * get binding class
     *
     * @return binding class
     */
    @Override
    public Class<RpcBinding> getBindingClass() {
        return RpcBinding.class;
    }

}