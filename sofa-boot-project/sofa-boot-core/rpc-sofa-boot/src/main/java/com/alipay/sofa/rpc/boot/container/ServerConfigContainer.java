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
import com.alipay.sofa.rpc.boot.log.LoggerConstant;
import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.config.UserThreadPoolManager;
import com.alipay.sofa.rpc.log.LogCodes;
import com.alipay.sofa.rpc.server.Server;
import com.alipay.sofa.rpc.server.UserThreadPool;
import com.alipay.sofa.rpc.server.bolt.BoltServer;
import com.alipay.sofa.rpc.server.triple.TripleServer;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ServiceConfig 工厂
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ServerConfigContainer {

    private static final Logger              LOGGER                      = SofaBootRpcLoggerFactory
                                                                             .getLogger(ServerConfigContainer.class);
    /**
     * bolt ServerConfig
     */
    private volatile ServerConfig            boltServerConfig;

    private final Object                     BOLT_LOCK                   = new Object();

    /**
     * rest ServerConfig
     */
    private volatile ServerConfig            restServerConfig;

    private final Object                     REST_LOCK                   = new Object();

    /**
     * dubbo ServerConfig
     */
    private volatile ServerConfig            dubboServerConfig;

    private final Object                     DUBBO_LOCK                  = new Object();

    /**
     * h2c ServerConfig
     */
    private volatile ServerConfig            h2cServerConfig;

    private final Object                     H2C_LOCK                    = new Object();

    /**
     * http ServerConfig
     */
    private volatile ServerConfig            httpServerConfig;

    private final Object                     HTTP_LOCK                   = new Object();

    /**
     * http ServerConfig
     */
    private volatile ServerConfig            tripleServerConfig;

    private final Object                     TRIPLE_LOCK                 = new Object();

    //custom server configs
    private final Map<String, ServerConfig>  customServerConfigs         = new ConcurrentHashMap<String, ServerConfig>();

    private final RpcThreadPoolMonitor       boltThreadPoolMonitor       = new RpcThreadPoolMonitor(
                                                                             LoggerConstant.BOLT_THREAD_LOGGER_NAME);

    private final RpcThreadPoolMonitor       tripleThreadPoolMonitor     = new RpcThreadPoolMonitor(
                                                                             LoggerConstant.TRIPLE_THREAD_LOGGER_NAME);

    private final List<RpcThreadPoolMonitor> customThreadPoolMonitorList = new ArrayList<>();

    private String                           enabledIpRange;

    private String                           bindNetworkInterface;

    private String                           boundHostStr;

    private String                           virtualHostStr;

    private String                           virtualPortStr;

    /**
     * h2c configs
     */
    private String                           h2cPortStr;

    private String                           h2cThreadPoolCoreSizeStr;

    private String                           h2cThreadPoolMaxSizeStr;

    private String                           h2cAcceptsSizeStr;

    private String                           h2cThreadPoolQueueSizeStr;

    /**
     * bolt configs
     */
    private String                           boltPortStr;

    private String                           boltThreadPoolCoreSizeStr;

    private String                           boltThreadPoolMaxSizeStr;

    private String                           boltAcceptsSizeStr;

    private String                           boltThreadPoolQueueSizeStr;

    private Boolean                          boltProcessInIoThread;

    /**
     * rest configs
     */
    private String                           restHostName;

    private String                           restPortStr;

    private String                           restIoThreadSizeStr;

    private String                           restContextPath;

    private String                           restThreadPoolMaxSizeStr;

    private String                           restMaxRequestSizeStr;

    private String                           restTelnetStr;

    private String                           restDaemonStr;

    private String                           restAllowedOrigins;

    /**
     * dubbo configs
     */
    private String                           dubboPortStr;

    private String                           dubboIoThreadSizeStr;

    private String                           dubboThreadPoolMaxSizeStr;

    private String                           dubboAcceptsSizeStr;

    /**
     * http configs
     */
    private String                           httpPortStr;

    private String                           httpThreadPoolCoreSizeStr;

    private String                           httpThreadPoolMaxSizeStr;

    private String                           httpAcceptsSizeStr;

    private String                           httpThreadPoolQueueSizeStr;

    /**
     * triple configs
     */
    private String                           triplePortStr;

    private String                           tripleThreadPoolCoreSizeStr;

    private String                           tripleThreadPoolMaxSizeStr;

    private String                           tripleAcceptsSizeStr;

    private String                           tripleThreadPoolQueueSizeStr;

    /**
     * 开启所有 ServerConfig 对应的 Server
     */
    public void startServers() {
        NetworkAddressUtil.caculate(enabledIpRange, bindNetworkInterface);
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

        startCustomThreadPoolMonitor();
    }

    private void startCustomThreadPoolMonitor() {
        Set<UserThreadPool> userThreadPoolSet = UserThreadPoolManager.getUserThreadPoolSet();
        if (!userThreadPoolSet.isEmpty()) {
            Set<String> poolNames = new HashSet<>();
            for (UserThreadPool pool : userThreadPoolSet) {
                RpcThreadPoolMonitor customThreadPoolMonitor = new RpcThreadPoolMonitor(
                    LoggerConstant.CUSTOM_THREAD_LOGGER_NAME);
                customThreadPoolMonitorList.add(customThreadPoolMonitor);
                if (poolNames.contains(pool.getThreadPoolName())) {
                    //use to distinguish some UserThreadPools set same poolName
                    customThreadPoolMonitor.setPoolName(pool.getThreadPoolName() + "-"
                                                        + pool.hashCode());
                } else {
                    customThreadPoolMonitor.setPoolName(pool.getThreadPoolName());
                }
                customThreadPoolMonitor.setThreadPoolExecutor(pool.getExecutor());
                customThreadPoolMonitor.start();
                poolNames.add(pool.getThreadPoolName());
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
        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(h2cPortStr)) {
            serverConfig.setPort(Integer.parseInt(h2cPortStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.H2C_PORT_DEFAULT);
        }

        if (StringUtils.hasText(h2cThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(h2cThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(h2cThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(h2cThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(h2cAcceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(h2cAcceptsSizeStr));
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
        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(boltPortStr)) {
            serverConfig.setPort(Integer.parseInt(boltPortStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT);
        }

        if (StringUtils.hasText(boltThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(boltThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(boltThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(boltThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(boltAcceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(boltAcceptsSizeStr));
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
        int port;
        int ioThreadCount;
        int restThreadPoolMaxSize;
        int maxRequestSize;
        boolean telnet;
        boolean daemon;

        if (!StringUtils.hasText(restHostName)) {
            restHostName = null;
        }

        if (!StringUtils.hasText(restPortStr)) {
            port = SofaBootRpcConfigConstants.REST_PORT_DEFAULT;
        } else {
            port = Integer.parseInt(restPortStr);
        }

        if (!StringUtils.hasText(restIoThreadSizeStr)) {
            ioThreadCount = SofaBootRpcConfigConstants.REST_IO_THREAD_COUNT_DEFAULT;
        } else {
            ioThreadCount = Integer.parseInt(restIoThreadSizeStr);
        }

        if (!StringUtils.hasText(restThreadPoolMaxSizeStr)) {
            restThreadPoolMaxSize = SofaBootRpcConfigConstants.REST_EXECUTOR_THREAD_COUNT_DEFAULT;
        } else {
            restThreadPoolMaxSize = Integer.parseInt(restThreadPoolMaxSizeStr);
        }

        if (!StringUtils.hasText(restMaxRequestSizeStr)) {
            maxRequestSize = SofaBootRpcConfigConstants.REST_MAX_REQUEST_SIZE_DEFAULT;
        } else {
            maxRequestSize = Integer.parseInt(restMaxRequestSizeStr);
        }

        if (!StringUtils.hasText(restTelnetStr)) {
            telnet = SofaBootRpcConfigConstants.REST_TELNET_DEFAULT;
        } else {
            telnet = Boolean.parseBoolean(restTelnetStr);
        }

        if (!StringUtils.hasText(restDaemonStr)) {
            daemon = SofaBootRpcConfigConstants.REST_DAEMON_DEFAULT;
        } else {
            daemon = Boolean.parseBoolean(restDaemonStr);
        }

        Map<String, String> parameters = new HashMap<String, String>();

        if (StringUtils.hasText(restAllowedOrigins)) {
            parameters.put(RpcConstants.ALLOWED_ORIGINS, restAllowedOrigins);
        }

        ServerConfig serverConfig = new ServerConfig().setPort(port).setIoThreads(ioThreadCount)
            .setMaxThreads(restThreadPoolMaxSize).setPayload(maxRequestSize).setTelnet(telnet)
            .setDaemon(daemon).setParameters(parameters);

        if (!StringUtils.isEmpty(restContextPath)) {
            serverConfig.setContextPath(restContextPath);
        }

        serverConfig.setAutoStart(false);
        serverConfig.setProtocol(SofaBootRpcConfigConstants.RPC_PROTOCOL_REST);
        addCommonServerConfig(serverConfig);

        serverConfig.setBoundHost(restHostName);

        return serverConfig;
    }

    /**
     * 创建 dubbo ServerConfig。会设置 Dubbo 的默认端口，其余配置不会由外层 Starter 设置默认值。
     *
     * @return dubbo ServerConfig
     */
    public ServerConfig createDubboServerConfig() {
        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(dubboPortStr)) {
            serverConfig.setPort(Integer.parseInt(dubboPortStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.DUBBO_PORT_DEFAULT);
        }

        if (StringUtils.hasText(dubboIoThreadSizeStr)) {
            serverConfig.setIoThreads(Integer.parseInt(dubboIoThreadSizeStr));
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
        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(httpPortStr)) {
            serverConfig.setPort(Integer.parseInt(httpPortStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.HTTP_PORT_DEFAULT);
        }

        if (StringUtils.hasText(httpThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(httpThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(httpThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(httpThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(httpAcceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(httpAcceptsSizeStr));
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
        ServerConfig serverConfig = new ServerConfig();

        if (StringUtils.hasText(triplePortStr)) {
            serverConfig.setPort(Integer.parseInt(triplePortStr));
        } else {
            serverConfig.setPort(SofaBootRpcConfigConstants.GRPC_PORT_DEFAULT);
        }

        if (StringUtils.hasText(tripleThreadPoolMaxSizeStr)) {
            serverConfig.setMaxThreads(Integer.parseInt(tripleThreadPoolMaxSizeStr));
        }

        if (StringUtils.hasText(tripleThreadPoolCoreSizeStr)) {
            serverConfig.setCoreThreads(Integer.parseInt(tripleThreadPoolCoreSizeStr));
        }

        if (StringUtils.hasText(tripleAcceptsSizeStr)) {
            serverConfig.setAccepts(Integer.parseInt(tripleAcceptsSizeStr));
        }

        if (StringUtils.hasText(tripleThreadPoolQueueSizeStr)) {
            serverConfig.setQueues(Integer.parseInt(tripleThreadPoolQueueSizeStr));
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

        stopCustomThreadPoolMonitor();

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

    private void stopCustomThreadPoolMonitor() {
        if (!customThreadPoolMonitorList.isEmpty()) {
            for (RpcThreadPoolMonitor monitor : customThreadPoolMonitorList) {
                monitor.stop();
            }
            customThreadPoolMonitorList.clear();
        }
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

    public void setBoundHostStr(String boundHostStr) {
        this.boundHostStr = boundHostStr;
    }

    public void setVirtualHostStr(String virtualHostStr) {
        this.virtualHostStr = virtualHostStr;
    }

    public void setVirtualPortStr(String virtualPortStr) {
        this.virtualPortStr = virtualPortStr;
    }

    public void setH2cPortStr(String h2cPortStr) {
        this.h2cPortStr = h2cPortStr;
    }

    public void setH2cThreadPoolCoreSizeStr(String h2cThreadPoolCoreSizeStr) {
        this.h2cThreadPoolCoreSizeStr = h2cThreadPoolCoreSizeStr;
    }

    public void setH2cThreadPoolMaxSizeStr(String h2cThreadPoolMaxSizeStr) {
        this.h2cThreadPoolMaxSizeStr = h2cThreadPoolMaxSizeStr;
    }

    public void setH2cAcceptsSizeStr(String h2cAcceptsSizeStr) {
        this.h2cAcceptsSizeStr = h2cAcceptsSizeStr;
    }

    public void setH2cThreadPoolQueueSizeStr(String h2cThreadPoolQueueSizeStr) {
        this.h2cThreadPoolQueueSizeStr = h2cThreadPoolQueueSizeStr;
    }

    public void setBoltPortStr(String boltPortStr) {
        this.boltPortStr = boltPortStr;
    }

    public void setBoltThreadPoolCoreSizeStr(String boltThreadPoolCoreSizeStr) {
        this.boltThreadPoolCoreSizeStr = boltThreadPoolCoreSizeStr;
    }

    public void setBoltThreadPoolMaxSizeStr(String boltThreadPoolMaxSizeStr) {
        this.boltThreadPoolMaxSizeStr = boltThreadPoolMaxSizeStr;
    }

    public void setBoltAcceptsSizeStr(String boltAcceptsSizeStr) {
        this.boltAcceptsSizeStr = boltAcceptsSizeStr;
    }

    public void setBoltThreadPoolQueueSizeStr(String boltThreadPoolQueueSizeStr) {
        this.boltThreadPoolQueueSizeStr = boltThreadPoolQueueSizeStr;
    }

    public void setBoltProcessInIoThread(Boolean boltProcessInIoThread) {
        this.boltProcessInIoThread = boltProcessInIoThread;
    }

    public void setRestHostName(String restHostName) {
        this.restHostName = restHostName;
    }

    public void setRestPortStr(String restPortStr) {
        this.restPortStr = restPortStr;
    }

    public void setRestIoThreadSizeStr(String restIoThreadSizeStr) {
        this.restIoThreadSizeStr = restIoThreadSizeStr;
    }

    public void setRestContextPath(String restContextPath) {
        this.restContextPath = restContextPath;
    }

    public void setRestThreadPoolMaxSizeStr(String restThreadPoolMaxSizeStr) {
        this.restThreadPoolMaxSizeStr = restThreadPoolMaxSizeStr;
    }

    public void setRestMaxRequestSizeStr(String restMaxRequestSizeStr) {
        this.restMaxRequestSizeStr = restMaxRequestSizeStr;
    }

    public void setRestTelnetStr(String restTelnetStr) {
        this.restTelnetStr = restTelnetStr;
    }

    public void setRestDaemonStr(String restDaemonStr) {
        this.restDaemonStr = restDaemonStr;
    }

    public void setRestAllowedOrigins(String restAllowedOrigins) {
        this.restAllowedOrigins = restAllowedOrigins;
    }

    public void setDubboPortStr(String dubboPortStr) {
        this.dubboPortStr = dubboPortStr;
    }

    public void setDubboIoThreadSizeStr(String dubboIoThreadSizeStr) {
        this.dubboIoThreadSizeStr = dubboIoThreadSizeStr;
    }

    public void setDubboThreadPoolMaxSizeStr(String dubboThreadPoolMaxSizeStr) {
        this.dubboThreadPoolMaxSizeStr = dubboThreadPoolMaxSizeStr;
    }

    public void setDubboAcceptsSizeStr(String dubboAcceptsSizeStr) {
        this.dubboAcceptsSizeStr = dubboAcceptsSizeStr;
    }

    public void setHttpPortStr(String httpPortStr) {
        this.httpPortStr = httpPortStr;
    }

    public void setHttpThreadPoolCoreSizeStr(String httpThreadPoolCoreSizeStr) {
        this.httpThreadPoolCoreSizeStr = httpThreadPoolCoreSizeStr;
    }

    public void setHttpThreadPoolMaxSizeStr(String httpThreadPoolMaxSizeStr) {
        this.httpThreadPoolMaxSizeStr = httpThreadPoolMaxSizeStr;
    }

    public void setHttpAcceptsSizeStr(String httpAcceptsSizeStr) {
        this.httpAcceptsSizeStr = httpAcceptsSizeStr;
    }

    public void setHttpThreadPoolQueueSizeStr(String httpThreadPoolQueueSizeStr) {
        this.httpThreadPoolQueueSizeStr = httpThreadPoolQueueSizeStr;
    }

    public void setTriplePortStr(String triplePortStr) {
        this.triplePortStr = triplePortStr;
    }

    public void setTripleThreadPoolCoreSizeStr(String tripleThreadPoolCoreSizeStr) {
        this.tripleThreadPoolCoreSizeStr = tripleThreadPoolCoreSizeStr;
    }

    public void setTripleThreadPoolMaxSizeStr(String tripleThreadPoolMaxSizeStr) {
        this.tripleThreadPoolMaxSizeStr = tripleThreadPoolMaxSizeStr;
    }

    public void setTripleAcceptsSizeStr(String tripleAcceptsSizeStr) {
        this.tripleAcceptsSizeStr = tripleAcceptsSizeStr;
    }

    public void setTripleThreadPoolQueueSizeStr(String tripleThreadPoolQueueSizeStr) {
        this.tripleThreadPoolQueueSizeStr = tripleThreadPoolQueueSizeStr;
    }

    public void setEnabledIpRange(String enabledIpRange) {
        this.enabledIpRange = enabledIpRange;
    }

    public void setBindNetworkInterface(String bindNetworkInterface) {
        this.bindNetworkInterface = bindNetworkInterface;
    }

}
