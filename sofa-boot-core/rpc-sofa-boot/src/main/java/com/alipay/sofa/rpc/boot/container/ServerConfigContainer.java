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

import com.alipay.sofa.rpc.boot.common.NetworkAddressUtil;
import com.alipay.sofa.rpc.boot.common.RpcThreadPoolMonitor;
import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.server.bolt.BoltServer;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ServiceConfig 工厂
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ServerConfigContainer {

    private static final Logger       LOGGER              = SofaBootRpcLoggerFactory
                                                              .getLogger(ServerConfigContainer.class);

    private SofaBootRpcProperties     sofaBootRpcProperties;
    /**
     * bolt ServerConfig
     */
    private volatile ServerConfig     boltServerConfig;
    private final Object              BOLT_LOCK           = new Object();

    /**
     * rest ServerConfig
     */
    private volatile ServerConfig     restServerConfig;
    private final Object              REST_LOCK           = new Object();

    /**
     * dubbo ServerConfig
     */
    private volatile ServerConfig     dubboServerConfig;
    private final Object              DUBBO_LOCK          = new Object();

    /**
     * h2c ServerConfig
     */
    private volatile ServerConfig     h2cServerConfig;
    private final Object              H2C_LOCK            = new Object();

    //custom server configs
    private Map<String, ServerConfig> customServerConfigs = new ConcurrentHashMap<String, ServerConfig>();

    public ServerConfigContainer(SofaBootRpcProperties sofaBootRpcProperties) {
        this.sofaBootRpcProperties = sofaBootRpcProperties;

        NetworkAddressUtil.caculate(sofaBootRpcProperties.getEnabledIpRange(),
            sofaBootRpcProperties.getBindNetworkInterface());
    }

    /**
     * 开启所有 ServerConfig 对应的 Server
     */
    public void startServers() {
        if (boltServerConfig != null) {
            boltServerConfig.buildIfAbsent().start();

            BoltServer server = (BoltServer) boltServerConfig.getServer();
            ThreadPoolExecutor threadPoolExecutor = server.getBizThreadPool();

            if (threadPoolExecutor != null) {
                new RpcThreadPoolMonitor(threadPoolExecutor).start();
            } else {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("the business threadpool can not be get");
                }
            }
        }

        if (restServerConfig != null) {
            restServerConfig.buildIfAbsent().start();
        }

        if (h2cServerConfig != null) {
            h2cServerConfig.buildIfAbsent().start();
        }

        for (Map.Entry<String, ServerConfig> entry : customServerConfigs.entrySet()) {
            final ServerConfig serverConfig = entry.getValue();
            if (serverConfig != null) {
                serverConfig.buildIfAbsent().start();
            }
        }

    }

    /**
     * 获取 ServerConfig
     *
     * @param protocol 协议
     * @return the ServerConfig
     */
    public ServerConfig getServerConfig(String protocol) {

        if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_BOLT)) {
            if (boltServerConfig == null) {
                synchronized (BOLT_LOCK) {
                    if (boltServerConfig == null) {
                        boltServerConfig = createBoltServerConfig();
                    }
                }
            }

            return boltServerConfig;
        } else if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_REST)) {
            if (restServerConfig == null) {
                synchronized (REST_LOCK) {
                    if (restServerConfig == null) {
                        restServerConfig = createRestServerConfig();
                    }
                }
            }

            return restServerConfig;
        } else if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO)) {

            if (dubboServerConfig == null) {
                synchronized (DUBBO_LOCK) {
                    if (dubboServerConfig == null) {
                        dubboServerConfig = createDubboServerConfig();
                    }
                }
            }

            return dubboServerConfig;
        } else if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_H2C)) {

            if (h2cServerConfig == null) {
                synchronized (H2C_LOCK) {
                    if (h2cServerConfig == null) {
                        h2cServerConfig = createH2cServerConfig();
                    }
                }
            }

            return h2cServerConfig;
        } else if (customServerConfigs.get(protocol) != null) {
            return customServerConfigs.get(protocol);
        } else {
            throw new SofaBootRpcRuntimeException("protocol [" + protocol + "] is not supported");
        }

    }

    /**
     * some common server config whether protocol
     *
     * @param serverConfig
     */
    private void addCommonServerConfig(ServerConfig serverConfig) {

        String boundHostStr = sofaBootRpcProperties.getBoundHost();
        String virtualHostStr = sofaBootRpcProperties.getVirtualHost();
        String virtualPortStr = sofaBootRpcProperties.getVirtualPort();

        //this will filter by networkface and iprange
        serverConfig.setVirtualHost(NetworkAddressUtil.getLocalIP());
        serverConfig.setBoundHost(NetworkAddressUtil.getLocalBindIP());

        //if has more accurate settings, use this.
        if (StringUtils.hasText(boundHostStr)) {
            serverConfig.setBoundHost(boundHostStr);
        }

        if (StringUtils.hasText(virtualHostStr)) {
            serverConfig.setVirtualHost(virtualHostStr);
        }

        if (StringUtils.hasText(virtualPortStr)) {
            serverConfig.setVirtualPort(Integer.parseInt(virtualPortStr));
        }
    }

    /**
     * 创建 h2c ServerConfig。rest 的 配置不需要外层 starter 设置默认值。
     *
     * @return H2c 的服务端配置信息
     */
    ServerConfig createH2cServerConfig() {
        String portStr = sofaBootRpcProperties.getH2cPort();
        String h2cThreadPoolCoreSizeStr = sofaBootRpcProperties.getH2cThreadPoolCoreSize();
        String h2cThreadPoolMaxSizeStr = sofaBootRpcProperties.getH2cThreadPoolMaxSize();
        String acceptsSizeStr = sofaBootRpcProperties.getH2cAcceptsSize();
        String h2cThreadPoolQueueSizeStr = sofaBootRpcProperties.getH2cThreadPoolQueueSize();

        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(portStr)) {
            serverConfig.setPort(Integer.parseInt(portStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.H2C_PORT_DEFAULT);
        }

        if (StringUtils.hasText(h2cThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(h2cThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(h2cThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(h2cThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(acceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(acceptsSizeStr));
        }

        if (StringUtils.hasText(h2cThreadPoolQueueSizeStr)) {
            serverConfig.setQueues(Integer.parseInt(h2cThreadPoolQueueSizeStr));
        }

        serverConfig.setAutoStart(false);
        return serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_H2C);
    }

    /**
     * 创建 bolt ServerConfig。rest 的 配置不需要外层 starter 设置默认值。
     *
     * @return Bolt 的服务端配置信息
     */
    ServerConfig createBoltServerConfig() {
        String portStr = sofaBootRpcProperties.getBoltPort();
        String boltThreadPoolCoreSizeStr = sofaBootRpcProperties.getBoltThreadPoolCoreSize();
        String boltThreadPoolMaxSizeStr = sofaBootRpcProperties.getBoltThreadPoolMaxSize();
        String acceptsSizeStr = sofaBootRpcProperties.getBoltAcceptsSize();
        String boltThreadPoolQueueSizeStr = sofaBootRpcProperties.getBoltThreadPoolQueueSize();

        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(portStr)) {
            serverConfig.setPort(Integer.parseInt(portStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT);
        }

        if (StringUtils.hasText(boltThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(boltThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(boltThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(boltThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(acceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(acceptsSizeStr));
        }

        if (StringUtils.hasText(boltThreadPoolQueueSizeStr)) {
            serverConfig.setQueues(Integer.parseInt(boltThreadPoolQueueSizeStr));
        }

        serverConfig.setAutoStart(false);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_BOLT);

        addCommonServerConfig(serverConfig);

        return serverConfig;
    }

    /**
     * 创建 rest ServerConfig。rest 的 配置需要外层 starter 设置默认值。
     *
     * @return rest ServerConfig
     */
    ServerConfig createRestServerConfig() {
        String hostName = sofaBootRpcProperties.getRestHostname();
        String portStr = sofaBootRpcProperties.getRestPort();
        String ioThreadSizeStr = sofaBootRpcProperties.getRestIoThreadSize();
        String contextPath = sofaBootRpcProperties.getRestContextPath();
        String restThreadPoolMaxSizeStr = sofaBootRpcProperties.getRestThreadPoolMaxSize();
        String maxRequestSizeStr = sofaBootRpcProperties.getRestMaxRequestSize();
        String telnetStr = sofaBootRpcProperties.getRestTelnet();
        String daemonStr = sofaBootRpcProperties.getRestDaemon();

        String allowedOrigins = sofaBootRpcProperties.getRestAllowedOrigins();
        int port;
        int ioThreadCount;
        int restThreadPoolMaxSize;
        int maxRequestSize;
        boolean telnet;
        boolean daemon;

        if (!StringUtils.hasText(hostName)) {
            hostName = null;
        }

        if (!StringUtils.hasText(portStr)) {
            port = SofaBootRpcConfigConstants.REST_PORT_DEFAULT;
        } else {
            port = Integer.parseInt(portStr);
        }

        if (!StringUtils.hasText(ioThreadSizeStr)) {
            ioThreadCount = SofaBootRpcConfigConstants.REST_IO_THREAD_COUNT_DEFAULT;
        } else {
            ioThreadCount = Integer.parseInt(ioThreadSizeStr);
        }

        if (!StringUtils.hasText(restThreadPoolMaxSizeStr)) {
            restThreadPoolMaxSize = SofaBootRpcConfigConstants.REST_EXECUTOR_THREAD_COUNT_DEFAULT;
        } else {
            restThreadPoolMaxSize = Integer.parseInt(restThreadPoolMaxSizeStr);
        }

        if (!StringUtils.hasText(maxRequestSizeStr)) {
            maxRequestSize = SofaBootRpcConfigConstants.REST_MAX_REQUEST_SIZE_DEFAULT;
        } else {
            maxRequestSize = Integer.parseInt(maxRequestSizeStr);
        }

        if (!StringUtils.hasText(telnetStr)) {
            telnet = SofaBootRpcConfigConstants.REST_TELNET_DEFAULT;
        } else {
            telnet = Boolean.parseBoolean(telnetStr);
        }

        if (!StringUtils.hasText(daemonStr)) {
            daemon = SofaBootRpcConfigConstants.REST_DAEMON_DEFAULT;
        } else {
            daemon = Boolean.parseBoolean(daemonStr);
        }

        Map<String, String> parameters = new HashMap<String, String>();

        if (StringUtils.hasText(allowedOrigins)) {
            parameters.put(RpcConstants.ALLOWED_ORIGINS, allowedOrigins);
        }

        ServerConfig serverConfig = new ServerConfig()
            .setPort(port)
            .setIoThreads(ioThreadCount)
            .setMaxThreads(restThreadPoolMaxSize)
            .setPayload(maxRequestSize)
            .setTelnet(telnet)
            .setDaemon(daemon)
            .setParameters(parameters);

        if (!StringUtils.isEmpty(contextPath)) {
            serverConfig.setContextPath(contextPath);
        }

        serverConfig.setAutoStart(false);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_REST);
        addCommonServerConfig(serverConfig);

        serverConfig.setBoundHost(hostName);

        return serverConfig;
    }

    /**
     * 创建 dubbo ServerConfig。会设置 Dubbo 的默认端口，其余配置不会由外层 Starter 设置默认值。
     *
     * @return dubbo ServerConfig
     */
    ServerConfig createDubboServerConfig() {
        String portStr = sofaBootRpcProperties.getDubboPort();
        String ioThreadSizeStr = sofaBootRpcProperties.getDubboIoThreadSize();
        String dubboThreadPoolMaxSizeStr = sofaBootRpcProperties.getDubboThreadPoolMaxSize();
        String dubboAcceptsSizeStr = sofaBootRpcProperties.getDubboAcceptsSize();

        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(portStr)) {
            serverConfig.setPort(Integer.parseInt(portStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.DUBBO_PORT_DEFAULT);
        }

        if (StringUtils.hasText(ioThreadSizeStr)) {
            serverConfig.setIoThreads(Integer.parseInt(ioThreadSizeStr));
        }

        if (StringUtils.hasText(dubboThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(dubboThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(dubboAcceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(dubboAcceptsSizeStr));
        }

        serverConfig.setAutoStart(false);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO);

        addCommonServerConfig(serverConfig);

        return serverConfig;

    }

    /**
     * 释放所有 ServerConfig 对应的资源，并移除所有的 ServerConfig。
     */
    public void closeAllServer() {
        if (boltServerConfig != null) {
            boltServerConfig.destroy();
            boltServerConfig = null;
        }

        if (restServerConfig != null) {
            restServerConfig.destroy();
            restServerConfig = null;
        }

        if (dubboServerConfig != null) {
            dubboServerConfig.destroy();
            dubboServerConfig = null;
        }

        if (h2cServerConfig != null) {
            h2cServerConfig.destroy();
            h2cServerConfig = null;
        }

        for (Map.Entry<String, ServerConfig> entry : customServerConfigs.entrySet()) {
            final ServerConfig serverConfig = entry.getValue();
            if (serverConfig != null) {
                serverConfig.destroy();
            }
        }

        customServerConfigs.clear();
    }

    /**
     * allow user register serverConfig
     *
     * @param protocol
     * @param serverConfig
     * @return
     */
    public boolean registerCustomServerConfig(String protocol, ServerConfig serverConfig) {

        if (customServerConfigs.containsKey(protocol)) {
            return false;
        } else {
            customServerConfigs.put(protocol, serverConfig);
            return true;
        }
    }

    /**
     * allow user register serverConfig
     *
     * @param protocol
     * @return
     */
    public boolean unRegisterCustomServerConfig(String protocol) {
        customServerConfigs.remove(protocol);
        return true;
    }
}