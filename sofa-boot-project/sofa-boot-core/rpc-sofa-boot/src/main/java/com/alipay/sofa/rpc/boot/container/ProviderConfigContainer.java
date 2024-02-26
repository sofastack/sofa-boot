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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.common.thread.SofaScheduledThreadPoolExecutor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.registry.Registry;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import com.alipay.sofa.runtime.spi.binding.Contract;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * ProviderConfig持有者.维护编程界面级别的RPC组件。
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ProviderConfigContainer {
    private static final Logger                         LOGGER                = SofaBootRpcLoggerFactory
                                                                                  .getLogger(ProviderConfigContainer.class);

    /**
     * 是否允许发布ProviderConfig
     */
    private boolean                                     allowPublish          = false;

    private List<String>                                providerRegisterWhiteList;

    private List<String>                                providerRegisterBlackList;

    /**
     * ProviderConfig 缓存
     */
    private final ConcurrentMap<String, ProviderConfig> RPC_SERVICE_CONTAINER = new ConcurrentHashMap<String, ProviderConfig>(
                                                                                  256);

    /**
     * 用来延时发布的线程池
     */
    private SofaScheduledThreadPoolExecutor             scheduledExecutorService;

    /**
     * 用于判断是否允许延迟服务发布至注册中心的扩展点、需要所有拓展点都通过
     */
    private List<ProviderConfigDelayRegisterChecker>    providerConfigDelayRegisterCheckerList;

    /**
     * 是否开启延时加载，兼容老逻辑
     */
    private boolean                                     enableDelayRegister;

    /**
     * 增加 ProviderConfig
     *
     * @param key            唯一id
     * @param providerConfig the ProviderConfig
     */
    public void addProviderConfig(String key, ProviderConfig providerConfig) {
        if (providerConfig != null) {
            if (RPC_SERVICE_CONTAINER.containsKey(key)) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("The same services and protocols already exist.key[" + key
                                + "];protocol[" + providerConfig.getServer().get(0) + "]");
                }
            } else {
                RPC_SERVICE_CONTAINER.put(key, providerConfig);
            }
        }
    }

    private boolean allowProviderRegister(ProviderConfig providerConfig) {
        if (CollectionUtils.isEmpty(providerRegisterWhiteList)
            && CollectionUtils.isEmpty(providerRegisterBlackList)) {
            return true;
        }
        String uniqueName = createUniqueNameByProvider(providerConfig);
        if (!CollectionUtils.isEmpty(providerRegisterBlackList)
            && providerRegisterBlackList.contains(uniqueName)) {
            return false;
        }
        if (!CollectionUtils.isEmpty(providerRegisterWhiteList)
            && !providerRegisterWhiteList.contains(uniqueName)) {
            return false;
        }
        return true;
    }

    /**
     * 获取 ProviderConfig
     *
     * @param key 唯一id
     * @return the ProviderConfig
     */
    public ProviderConfig getProviderConfig(String key) {
        return RPC_SERVICE_CONTAINER.get(key);
    }

    /**
     * 移除 ProviderConfig
     *
     * @param key 唯一id
     */
    public void removeProviderConfig(String key) {
        RPC_SERVICE_CONTAINER.remove(key);
    }

    /**
     * 获取缓存的所有 ProviderConfig
     *
     * @return 所有 ProviderConfig
     */
    public Collection<ProviderConfig> getAllProviderConfig() {
        return RPC_SERVICE_CONTAINER.values();
    }

    /**
     * 发布所有 ProviderConfig 元数据信息到注册中心
     */
    public void publishAllProviderConfig() {
        for (ProviderConfig providerConfig : getAllProviderConfig()) {
            int delay = providerConfig.getDelay();
            if (enableDelayRegister && delay > 0) {
                // 根据延时时间异步去注册中心注册服务
                if (scheduledExecutorService == null) {
                    scheduledExecutorService = new SofaScheduledThreadPoolExecutor(1, "rpc-provider-delay-register",
                            SofaBootConstants.SOFA_BOOT_SPACE_NAME);
                }
                scheduledExecutorService.schedule(() -> doDelayPublishProviderConfig(providerConfig,
                        providerConfigDelayRegisterCheckerList), delay, TimeUnit.MILLISECONDS);
            } else {
                // 没有配置延时加载则直接去注册中心注册服务
                doPublishProviderConfig(providerConfig);
            }
        }
    }

    private void doDelayPublishProviderConfig(ProviderConfig providerConfig,
                                              List<ProviderConfigDelayRegisterChecker> providerConfigDelayRegisterCheckerList) {
        for (ProviderConfigDelayRegisterChecker providerConfigDelayRegisterChecker : providerConfigDelayRegisterCheckerList) {
            if (!providerConfigDelayRegisterChecker.allowRegister()) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("service publish failed, interfaceId [{}], please check.",
                        providerConfig.getInterfaceId());
                }
                return;
            }
        }
        doPublishProviderConfig(providerConfig);
    }

    private void doPublishProviderConfig(ProviderConfig providerConfig) {
        ServerConfig serverConfig = (ServerConfig) providerConfig.getServer().get(0);
        if (!serverConfig.getProtocol().equalsIgnoreCase(
            SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO)) {
            if (allowProviderRegister(providerConfig)) {
                providerConfig.setRegister(true);
            } else {
                LOGGER.info("Provider will not register: [{}]", providerConfig.buildKey());
            }

            List<RegistryConfig> registrys = providerConfig.getRegistry();
            for (RegistryConfig registryConfig : registrys) {

                Registry registry = RegistryFactory.getRegistry(registryConfig);
                registry.init();
                registry.start();

                registry.register(providerConfig);

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("service published.  interfaceId["
                                + providerConfig.getInterfaceId() + "]; protocol["
                                + serverConfig.getProtocol() + "]");
                }
            }
        }
    }

    /**
     * export所有 Dubbo 类型的 ProviderConfig
     */
    public void exportAllDubboProvideConfig() {
        for (ProviderConfig providerConfig : getAllProviderConfig()) {

            ServerConfig serverConfig = (ServerConfig) providerConfig.getServer().get(0);
            if (serverConfig.getProtocol().equalsIgnoreCase(
                SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO)) {
                providerConfig.setRegister(true);
                providerConfig.export();

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("service published.  interfaceId["
                                + providerConfig.getInterfaceId() + "]; protocol["
                                + serverConfig.getProtocol() + "]");
                }
            }
        }
    }

    /**
     * unExport所有的 ProviderConfig
     */
    public void unExportAllProviderConfig() {
        for (ProviderConfig providerConfig : getAllProviderConfig()) {
            providerConfig.unExport();
        }

    }

    /**
     * 是否允许发布 ProviderConfig 元数据信息
     *
     * @return
     */
    public boolean isAllowPublish() {
        return allowPublish;
    }

    /**
     * 设置是否允许发布 ProviderConfig 元数据信息
     *
     * @param allowPublish 是否允许发布 ProviderConfig 元数据信息
     */
    public void setAllowPublish(boolean allowPublish) {
        this.allowPublish = allowPublish;
    }

    /**
     * 创建唯一Id
     *
     * @param contract the Contract
     * @param binding  the RpcBinding
     * @return 唯一id
     */
    public String createUniqueName(Contract contract, RpcBinding binding) {
        String uniqueId = "";
        String version = ":1.0";
        String protocol = "";
        if (StringUtils.hasText(contract.getUniqueId())) {
            uniqueId = ":" + contract.getUniqueId();
        }
        if (StringUtils.hasText(contract.getProperty("version"))) {
            version = ":" + contract.getProperty("version");
        }
        if (StringUtils.hasText(binding.getBindingType().getType())) { //dubbo can not merge to bolt
            protocol = ":" + binding.getBindingType().getType();
        }

        return new StringBuilder(contract.getInterfaceType().getName()).append(version)
            .append(uniqueId).append(protocol).toString();
    }

    /**
     * Create UniqueName by interfaceId and uniqueId
     */
    private String createUniqueNameByProvider(ProviderConfig providerConfig) {
        String uniqueId = "";
        if (StringUtils.hasText(providerConfig.getUniqueId())) {
            uniqueId = ":" + providerConfig.getUniqueId();
        }
        return providerConfig.getInterfaceId() + uniqueId;
    }

    public void setProviderRegisterWhiteList(List<String> providerRegisterWhiteList) {
        this.providerRegisterWhiteList = providerRegisterWhiteList;
    }

    public void setProviderRegisterBlackList(List<String> providerRegisterBlackList) {
        this.providerRegisterBlackList = providerRegisterBlackList;
    }

    public List<String> getProviderRegisterWhiteList() {
        return providerRegisterWhiteList;
    }

    public List<String> getProviderRegisterBlackList() {
        return providerRegisterBlackList;
    }

    public void setProviderConfigDelayRegister(List<ProviderConfigDelayRegisterChecker> providerConfigDelayRegisterCheckerList) {
        this.providerConfigDelayRegisterCheckerList = providerConfigDelayRegisterCheckerList;
    }

    public List<ProviderConfigDelayRegisterChecker> getProviderConfigDelayRegisterCheckerList() {
        return providerConfigDelayRegisterCheckerList;
    }

    public void setEnableDelayRegister(boolean enableDelayRegister) {
        this.enableDelayRegister = enableDelayRegister;
    }

    public boolean isEnableDelayRegister() {
        return enableDelayRegister;
    }
}
