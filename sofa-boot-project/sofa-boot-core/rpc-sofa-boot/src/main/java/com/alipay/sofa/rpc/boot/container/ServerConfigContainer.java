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
import com.alipay.sofa.rpc.boot.log.LoggerConstant;
import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.log.LogCodes;
import com.alipay.sofa.rpc.server.Server;
import com.alipay.sofa.rpc.server.bolt.BoltServer;
import com.alipay.sofa.rpc.server.triple.TripleServer;
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

    private static final Logger       LOGGER                  = SofaBootRpcLoggerFactory
                                                                  .getLogger(ServerConfigContainer.class);

    private SofaBootRpcProperties     sofaBootRpcProperties;
    /**
     * bolt ServerConfig
     */
    private volatile ServerConfig     boltServerConfig;
    private final Object              BOLT_LOCK               = new Object();

    /**
     * rest ServerConfig
     */
    private volatile ServerConfig     restServerConfig;
    private final Object              REST_LOCK               = new Object();

    /**
     * dubbo ServerConfig
     */
    private volatile ServerConfig     dubboServerConfig;
    private final Object              DUBBO_LOCK              = new Object();

    /**
     * h2c ServerConfig
     */
    private volatile ServerConfig     h2cServerConfig;
    private final Object              H2C_LOCK                = new Object();

    /**
     * http ServerConfig
     */
    private volatile ServerConfig     httpServerConfig;
    private final Object              HTTP_LOCK               = new Object();

    /**
     * http ServerConfig
     */
    private volatile ServerConfig     tripleServerConfig;
    private final Object              TRIPLE_LOCK             = new Object();

    //custom server configs
    private Map<String, ServerConfig> customServerConfigs     = new ConcurrentHashMap<String, ServerConfig>();

    private RpcThreadPoolMonitor      boltThreadPoolMonitor   = new RpcThreadPoolMonitor(
                                                                  LoggerConstant.BOLT_THREAD_LOGGER_NAME);

    private RpcThreadPoolMonitor      tripleThreadPoolMonitor = new RpcThreadPoolMonitor(
                                                                  LoggerConstant.TRIPLE_THREAD_LOGGER_NAME);

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
                boltThreadPoolMonitor.setThreadPoolExecutor(threadPoolExecutor);
                boltThreadPoolMonitor.start();
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

        if (httpServerConfig != null) {
            httpServerConfig.buildIfAbsent().start();

            // 加入线程监测？
        }

        if (tripleServerConfig != null) {
            tripleServerConfig.buildIfAbsent().start();
            TripleServer tripleServer = (TripleServer) tripleServerConfig.getServer();
            ThreadPoolExecutor threadPoolExecutor = tripleServer.getBizThreadPool();

            if (threadPoolExecutor != null) {
                tripleThreadPoolMonitor.setThreadPoolExecutor(threadPoolExecutor);
                tripleThreadPoolMonitor.start();
            } else {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("the business threadpool can not be get");
                }
            }
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
        } else if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_HTTP)) {

            if (httpServerConfig == null) {
                synchronized (HTTP_LOCK) {
                    if (httpServerConfig == null) {
                        httpServerConfig = createHttpServerConfig();
                    }
                }
            }

            return httpServerConfig;
        } else if (protocol.equalsIgnoreCase(SofaBootRpcConfigConstants.RPC_PROTOCOL_TRIPLE)) {
            if (tripleServerConfig == null) {
                synchronized (TRIPLE_LOCK) {
                    if (tripleServerConfig == null) {
                        tripleServerConfig = createTripleServerConfig();
                    }
                }
            }
            return tripleServerConfig;
        } else if (customServerConfigs.get(protocol) != null) {
            return customServerConfigs.get(protocol);
        } else {
            throw new SofaBootRpcRuntimeException(LogCodes.getLog(
                LogCodes.ERROR_SERVER_PROTOCOL_NOT_SUPPORT, protocol));
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
        addCommonServerConfig(serverConfig);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_H2C);

        return serverConfig;
    }

    /**
     * 创建 bolt ServerConfig。rest 的 配置不需要外层 starter 设置默认值。
     *
     * @return Bolt 的服务端配置信息
     */
    public ServerConfig createBoltServerConfig() {
        String portStr = sofaBootRpcProperties.getBoltPort();
        String boltThreadPoolCoreSizeStr = sofaBootRpcProperties.getBoltThreadPoolCoreSize();
        String boltThreadPoolMaxSizeStr = sofaBootRpcProperties.getBoltThreadPoolMaxSize();
        String acceptsSizeStr = sofaBootRpcProperties.getBoltAcceptsSize();
        String boltThreadPoolQueueSizeStr = sofaBootRpcProperties.getBoltThreadPoolQueueSize();
        Boolean boltProcessInIoThread = sofaBootRpcProperties.getBoltProcessInIoThread();
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

        Map<String, String> parameters = new HashMap<>();
        if (boltProcessInIoThread != null) {
            parameters.put(RpcConstants.PROCESS_IN_IOTHREAD, boltProcessInIoThread.toString());
        }
        serverConfig.setParameters(parameters);

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
    public ServerConfig createRestServerConfig() {
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

        ServerConfig serverConfig = new ServerConfig().setPort(port).setIoThreads(ioThreadCount)
            .setMaxThreads(restThreadPoolMaxSize).setPayload(maxRequestSize).setTelnet(telnet)
            .setDaemon(daemon).setParameters(parameters);

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
    public ServerConfig createDubboServerConfig() {
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
     * 创建 http ServerConfig。rest 的 配置不需要外层 starter 设置默认值。
     *
     * @return H2c 的服务端配置信息
     */
    ServerConfig createHttpServerConfig() {
        String portStr = sofaBootRpcProperties.getHttpPort();
        String httpThreadPoolCoreSizeStr = sofaBootRpcProperties.getHttpThreadPoolCoreSize();
        String httpThreadPoolMaxSizeStr = sofaBootRpcProperties.getHttpThreadPoolMaxSize();
        String acceptsSizeStr = sofaBootRpcProperties.getHttpAcceptsSize();
        String httpThreadPoolQueueSizeStr = sofaBootRpcProperties.getHttpThreadPoolQueueSize();

        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(portStr)) {
            serverConfig.setPort(Integer.parseInt(portStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.HTTP_PORT_DEFAULT);
        }

        if (StringUtils.hasText(httpThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(httpThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(httpThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(httpThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(acceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(acceptsSizeStr));
        }

        if (StringUtils.hasText(httpThreadPoolQueueSizeStr)) {
            serverConfig.setQueues(Integer.parseInt(httpThreadPoolQueueSizeStr));
        }

        serverConfig.setAutoStart(false);

        addCommonServerConfig(serverConfig);

        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_HTTP);

        return serverConfig;
    }

    /**
     * grpc server
     *
     * @return server
     */
    private ServerConfig createTripleServerConfig() {
        String portStr = sofaBootRpcProperties.getTriplePort();
        String threadPoolCoreSizeStr = sofaBootRpcProperties.getTripleThreadPoolCoreSize();
        String threadPoolMaxSizeStr = sofaBootRpcProperties.getTripleThreadPoolMaxSize();
        String acceptsSizeStr = sofaBootRpcProperties.getTripleAcceptsSize();
        String threadPoolQueueSizeStr = sofaBootRpcProperties.getTripleThreadPoolQueueSize();

        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(portStr)) {
            serverConfig.setPort(Integer.parseInt(portStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.GRPC_PORT_DEFAULT);
        }

        if (StringUtils.hasText(threadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(threadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(threadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(threadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(acceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(acceptsSizeStr));
        }

        if (StringUtils.hasText(threadPoolQueueSizeStr)) {
            serverConfig.setQueues(Integer.parseInt(threadPoolQueueSizeStr));
        }

        serverConfig.setAutoStart(false);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_TRIPLE);
        addCommonServerConfig(serverConfig);

        return serverConfig;

    }

    /**
     * 释放所有 ServerConfig 对应的资源，并移除所有的 ServerConfig。
     */
    public void closeAllServer() {
        if (boltThreadPoolMonitor != null) {
            boltThreadPoolMonitor.stop();
        }
        if (tripleThreadPoolMonitor != null) {
            tripleThreadPoolMonitor.stop();
        }

        destroyServerConfig(boltServerConfig);
        destroyServerConfig(restServerConfig);
        destroyServerConfig(dubboServerConfig);
        destroyServerConfig(h2cServerConfig);
        destroyServerConfig(tripleServerConfig);
        for (Map.Entry<String, ServerConfig> entry : customServerConfigs.entrySet()) {
            final ServerConfig serverConfig = entry.getValue();
            destroyServerConfig(serverConfig);
        }

        boltServerConfig = null;
        restServerConfig = null;
        dubboServerConfig = null;
        h2cServerConfig = null;
        tripleServerConfig = null;
        customServerConfigs.clear();
    }

    private void destroyServerConfig(ServerConfig serverConfig) {
        if (serverConfig != null) {
            Server server = serverConfig.getServer();
            if (server != null && server.hasNoEntry()) {
                serverConfig.destroy();
            }
        }
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