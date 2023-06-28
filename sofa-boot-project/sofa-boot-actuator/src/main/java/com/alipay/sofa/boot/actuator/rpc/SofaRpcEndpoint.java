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
package com.alipay.sofa.boot.actuator.rpc;

import com.alipay.sofa.rpc.bootstrap.ConsumerBootstrap;
import com.alipay.sofa.rpc.bootstrap.ProviderBootstrap;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link Endpoint @Endpoint} to expose details of sofa rpc information.
 *
 * @author huzijie
 * @version SofaRpcEndpoint.java, v 0.1 2023年04月23日 5:38 PM huzijie Exp $
 */
@Endpoint(id = "rpc")
public class SofaRpcEndpoint {

    @ReadOperation
    public RpcServicesDescriptor rpcServices() {
        List<ProviderDescriptor> providerDescriptors = getProviderDescriptors();
        List<ConsumerDescriptor> consumerDescriptors = getConsumerDescriptors();
        List<RegistryDescriptor> registryDescriptors = getRegistryDescriptors();
        return new RpcServicesDescriptor(providerDescriptors, consumerDescriptors,
            registryDescriptors);
    }

    @SuppressWarnings("rawtypes")
    protected List<ProviderDescriptor> getProviderDescriptors() {
        // collect providerDescriptors
        List<ProviderBootstrap> providerBootstraps = RpcRuntimeContext.getProviderConfigs();
        return providerBootstraps.stream()
                .map(ProviderBootstrap::getProviderConfig)
                .map(this::createProviderDescriptor)
                .filter(Objects::nonNull).toList();
    }

    @SuppressWarnings("rawtypes")
    protected List<ConsumerDescriptor> getConsumerDescriptors() {
        // collect consumerDescriptors
        List<ConsumerBootstrap> consumerBootstraps = RpcRuntimeContext.getConsumerConfigs();
        return consumerBootstraps.stream()
                .map(ConsumerBootstrap::getConsumerConfig)
                .map(this::createConsumerDescriptor)
                .filter(Objects::nonNull).toList();
    }

    protected List<RegistryDescriptor> getRegistryDescriptors() {
        // collect registryDescriptors
        List<RegistryConfig> registryConfigs = RegistryFactory.getRegistryConfigs();
        return registryConfigs.stream()
                .map(this::createRegistryDescriptor)
                .filter(Objects::nonNull).toList();
    }

    @SuppressWarnings("rawtypes")
    protected ProviderDescriptor createProviderDescriptor(ProviderConfig providerConfig) {
        String interfaceId = providerConfig.getInterfaceId();
        String uniqueId = providerConfig.getUniqueId();
        List<ServerConfig> serverConfigs = providerConfig.getServer();
        List<String> protocols = serverConfigs.stream().map(ServerConfig::getProtocol).filter(Objects::nonNull).toList();
        List<RegistryConfig> registryConfigs = providerConfig.getRegistry();
        List<String> registries = registryConfigs.stream().map(RegistryConfig::getProtocol).filter(Objects::nonNull).toList();
        String serialization = providerConfig.getSerialization();
        boolean register = providerConfig.isRegister();
        String targetClassName = Optional.ofNullable(providerConfig.getRef())
                .map(object -> AopProxyUtils.ultimateTargetClass(object).getName()).orElse(null);
        return new ProviderDescriptor(interfaceId, uniqueId, protocols, registries, serialization, register, targetClassName);
    }

    @SuppressWarnings("rawtypes")
    protected ConsumerDescriptor createConsumerDescriptor(ConsumerConfig consumerConfig) {
        String interfaceId = consumerConfig.getInterfaceId();
        String uniqueId = consumerConfig.getUniqueId();
        String protocol = consumerConfig.getProtocol();
        List<RegistryConfig> registryConfigs = consumerConfig.getRegistry();
        List<String> registries = registryConfigs.stream().map(RegistryConfig::getProtocol).filter(Objects::nonNull).toList();
        String directUrl = consumerConfig.getDirectUrl();
        String invokeType = consumerConfig.getInvokeType();
        String serialization = consumerConfig.getSerialization();
        boolean subscribe = consumerConfig.isSubscribe();
        int timeout = consumerConfig.getTimeout();
        int retries = consumerConfig.getRetries();
        return new ConsumerDescriptor(interfaceId, uniqueId, protocol, registries, serialization, directUrl, invokeType, subscribe, timeout, retries);
    }

    protected RegistryDescriptor createRegistryDescriptor(RegistryConfig registryConfig) {
        String protocol = registryConfig.getProtocol();
        String address = registryConfig.getAddress();
        String index = registryConfig.getIndex();
        return new RegistryDescriptor(protocol, address, index);
    }

    /**
     * Description of an application's rpc services.
     */
    @JsonPropertyOrder({ "provider", "consumer", "registry" })
    public static class RpcServicesDescriptor implements OperationResponseBody {

        private final List<ProviderDescriptor> providers;

        private final List<ConsumerDescriptor> consumers;

        private final List<RegistryDescriptor> registries;

        private RpcServicesDescriptor(List<ProviderDescriptor> providerDescriptors,
                                      List<ConsumerDescriptor> consumerDescriptors,
                                      List<RegistryDescriptor> registryDescriptors) {
            this.providers = providerDescriptors;
            this.consumers = consumerDescriptors;
            this.registries = registryDescriptors;
        }

        public List<ProviderDescriptor> getProvider() {
            return this.providers;
        }

        public List<ConsumerDescriptor> getConsumer() {
            return this.consumers;
        }

        public List<RegistryDescriptor> getRegistry() {
            return registries;
        }

    }

    /**
     * Base class for descriptions of a provider
     */
    public static class ProviderDescriptor {

        protected final String              interfaceId;

        protected final String              uniqueId;

        protected final List<String>        protocols;

        protected final List<String>        registries;

        protected final String              serialization;

        protected final boolean             register;

        protected final String              targetClassName;

        protected final Map<String, Object> extraInfos = new HashMap<>();

        public ProviderDescriptor(String interfaceId, String uniqueId, List<String> protocols,
                                  List<String> registries, String serialization, boolean register,
                                  String targetClassName) {
            this.interfaceId = interfaceId;
            this.uniqueId = uniqueId;
            this.protocols = protocols;
            this.registries = registries;
            this.serialization = serialization;
            this.register = register;
            this.targetClassName = targetClassName;
        }

        public String getInterfaceId() {
            return interfaceId;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public List<String> getProtocols() {
            return protocols;
        }

        public List<String> getRegistries() {
            return registries;
        }

        public String getSerialization() {
            return serialization;
        }

        public boolean isRegister() {
            return register;
        }

        public String getTargetClassName() {
            return targetClassName;
        }

        public Map<String, Object> getExtraInfos() {
            return extraInfos;
        }
    }

    /**
     * Base class for descriptions of a consumer
     */
    public static class ConsumerDescriptor {

        protected final String              interfaceId;

        protected final String              uniqueId;

        protected final String              protocol;

        protected final List<String>        registries;

        protected final String              serialization;

        protected final String              directUrl;

        protected final String              invokeType;

        protected final boolean             subscribe;

        protected final int                 timeout;

        protected final int                 retries;

        protected final Map<String, Object> extraInfos = new HashMap<>();

        public ConsumerDescriptor(String interfaceId, String uniqueId, String protocol,
                                  List<String> registries, String serialization, String directUrl,
                                  String invokeType, boolean subscribe, int timeout, int retries) {
            this.interfaceId = interfaceId;
            this.uniqueId = uniqueId;
            this.protocol = protocol;
            this.registries = registries;
            this.serialization = serialization;
            this.directUrl = directUrl;
            this.invokeType = invokeType;
            this.subscribe = subscribe;
            this.timeout = timeout;
            this.retries = retries;
        }

        public String getInterfaceId() {
            return interfaceId;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public String getProtocol() {
            return protocol;
        }

        public List<String> getRegistries() {
            return registries;
        }

        public String getSerialization() {
            return serialization;
        }

        public String getDirectUrl() {
            return directUrl;
        }

        public String getInvokeType() {
            return invokeType;
        }

        public boolean isSubscribe() {
            return subscribe;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getRetries() {
            return retries;
        }

        public Map<String, Object> getExtraInfos() {
            return extraInfos;
        }
    }

    /**
     * Base class for descriptions of a registry
     */
    public static class RegistryDescriptor {

        protected final String protocol;

        protected final String address;

        protected final String index;

        public RegistryDescriptor(String protocol, String address, String index) {
            this.protocol = protocol;
            this.address = address;
            this.index = index;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getAddress() {
            return address;
        }

        public String getIndex() {
            return index;
        }
    }
}
