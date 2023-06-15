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
package com.alipay.sofa.boot.autoconfigure.rpc;

import com.alipay.sofa.rpc.common.SofaOptions;
import com.google.common.base.CaseFormat;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties to configure sofa rpc.
 *
 * @author khotyn
 */
@ConfigurationProperties("sofa.boot.rpc")
public class SofaBootRpcProperties implements EnvironmentAware {

    public static final String  PREFIX     = "sofa.boot.rpc";

    public static final String  OLD_PREFIX = "com.alipay.sofa.rpc";

    private Environment         environment;

    /**
     * whether enable auto publish service when application start.
     */
    private boolean             enableAutoPublish;

    /**
     * whether regulation effective (是否开启单机故障剔除功能)
     */
    private String              aftRegulationEffective;
    /**
     * whether regulation effective (是否开启降级)
     */
    private String              aftDegradeEffective;
    /**
     * aft time window of caculation (时间窗口)
     */
    private String              aftTimeWindow;
    /**
     * aft least invoke times in window (最小调用次数)
     */
    private String              aftLeastWindowCount;
    /**
     * aft least exception rate multiple than average exception rate (最小异常率)
     */
    private String              aftLeastWindowExceptionRateMultiple;

    /**
     * aft weight was degraded  by this rate (降级速率)
     */
    private String              aftWeightDegradeRate;
    /**
     * aft weight was recovered  by this rate (恢复速率)
     */
    private String              aftWeightRecoverRate;
    /**
     * the least weight that aft could degrade one provider (降级最小权重)
     */
    private String              aftDegradeLeastWeight;

    /**
     * the max ip numbers that aft could degrade (最大降级 ip)
     */
    private String              aftDegradeMaxIpCount;
    /* fault-tolerance end */

    /* Bolt start*/

    /**
     * the port of bolt (bolt 端口)
     */
    private String              boltPort;

    /**
     * the core thread pool size of bolt （bolt 核心线程数）
     */
    private String              boltThreadPoolCoreSize;

    /**
     * the max thread pool size of bolt （bolt 最大线程数）
     */
    private String              boltThreadPoolMaxSize;

    /**
     * the queue size of bolt server（bolt 线程池队列）
     */
    private String              boltThreadPoolQueueSize;

    /**
     * the max accept size of bolt (bolt 服务端允许客户端建立的连接数)
     */
    private String              boltAcceptsSize;

    /**
     * process in bolt io worker thread (bolt 服务端业务处理是否直接在worker中处理)
     */
    private Boolean             boltProcessInIoThread;

    /**
     * export a port to show swagger
     */
    private Boolean             enableSwagger;

    /**
     * Location of remote mock server . If specified ,auto enable mock.
     */
    private String              mockUrl;
    /* Bolt end*/

    /* H2c start*/
    /**
     * the port of http2 (http2 端口)
     */
    private String              h2cPort;

    /**
     * the core thread pool size of http2 （http2 核心线程数）
     */
    private String              h2cThreadPoolCoreSize;

    /**
     * the max thread pool size of http2 （http2 最大线程数）
     */
    private String              h2cThreadPoolMaxSize;

    /**
     * the queue size of http2 server（http2 线程池队列）
     */
    private String              h2cThreadPoolQueueSize;

    /**
     * the max accept size of http2 (http2 服务端允许客户端建立的连接数)
     */
    private String              h2cAcceptsSize;
    /* Bolt end*/

    /* rest start*/
    /**
     * rest host name (rest 绑定的 hostname)
     */
    private String              restHostname;
    /**
     * the port of rest (rest 端口)
     */
    private String              restPort;
    /**
     * the io thread size of rest io (rest io 线程数)
     */
    private String              restIoThreadSize;

    /**
     * rest context path (rest context path)
     */
    private String              restContextPath;

    /**
     * cors settings
     */
    private String              restAllowedOrigins;
    /**
     * the max thread pool size of rest （rest 最大线程数）
     */
    private String              restThreadPoolMaxSize;

    /**
     * the max request size of per request (rest 最大请求大小)
     */
    private String              restMaxRequestSize;

    /**
     * whether allow rest telnet (是否允许 rest telnet)
     */
    private String              restTelnet;
    /**
     * whether rest server is daemon (是否hold住端口，true的话随主线程退出而退出)
     */
    private String              restDaemon;
    private boolean             restSwagger;
    /* rest end */

    /* dubbo  start*/

    /**
     * the port of dubbo (dubbo 端口)
     */
    private String              dubboPort;

    /**
     * the io thread size of dubbo io (dubbo io 线程数)
     */
    private String              dubboIoThreadSize;

    /**
     * the max thread pool size of dubbo （dubbo 最大线程数）
     */
    private String              dubboThreadPoolMaxSize;

    /**
     * the max accept size of dubbo (dubbo 服务端允许客户端建立的连接数)
     */
    private String              dubboAcceptsSize;
    /* dubbo  end*/

    /* http start*/
    /**
     * the port of http (http 端口)
     */
    private String              httpPort;

    /**
     * the core thread pool size of http （http 核心线程数）
     */
    private String              httpThreadPoolCoreSize;

    /**
     * the max thread pool size of http （http 最大线程数）
     */
    private String              httpThreadPoolMaxSize;

    /**
     * the queue size of http server（http 线程池队列）
     */
    private String              httpThreadPoolQueueSize;

    /**
     * the max accept size of http (http 服务端允许客户端建立的连接数)
     */
    private String              httpAcceptsSize;
    /* http end*/

    /* triple start*/

    /**
     * the port of triple (triple 端口)
     */
    private String              triplePort;

    /**
     * the core thread pool size of triple （triple 核心线程数）
     */
    private String              tripleThreadPoolCoreSize;

    /**
     * the max thread pool size of triple （triple 最大线程数）
     */
    private String              tripleThreadPoolMaxSize;

    /**
     * the queue size of triple server（triple 线程池队列）
     */
    private String              tripleThreadPoolQueueSize;

    /**
     * the max accept size of triple (triple 服务端允许客户端建立的连接数)
     */
    private String              tripleAcceptsSize;
    /* triple end*/

    /* registry */
    /**
     * registry address of rpc server （注册中心的地址）
     */
    private String              registryAddress;

    /**
     * virtual host for service publish（服务发布虚拟host）
     */
    private String              virtualHost;

    /**
     * virtual port for service publish（服务发布虚拟端口）
     */
    private String              virtualPort;

    /**
     * ip range which used in multi network interfaces （多网卡 ip 范围）
     */
    private String              enabledIpRange;

    /**
     * this bind network interface in multi network interfaces （绑定网卡来选择ip）
     */
    private String              bindNetworkInterface;

    /**
     * bound host (绑定host)
     */
    private String              boundHost;

    /**
     * disable lookout （是否关闭lookout ）
     */
    private String              lookoutCollectDisable;

    /**
     * multi registries （多注册中心）
     */
    private Map<String, String> registries = new HashMap<String, String>();

    /**
     * enable mesh，can be protocol,like bolt,mesh,all,now we only support bolt （是否开启mesh支持，目前只支持bolt）
     */
    private String              enableMesh;

    /**
     * the reference limit numbers of the same interface could be referred (允许客户端对同一个服务生成的引用代理数量，默认为3)
     */
    private String              consumerRepeatedReferenceLimit;

    private String              hystrixEnable;

    private String              defaultTracer;

    /**
     * dynamic config setting
     */
    private String              dynamicConfig;

    public boolean isEnableAutoPublish() {
        return enableAutoPublish;
    }

    public void setEnableAutoPublish(boolean enableAutoPublish) {
        this.enableAutoPublish = enableAutoPublish;
    }

    public String getAftRegulationEffective() {
        return ObjectUtils.isEmpty(aftRegulationEffective) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftRegulationEffective;
    }

    public void setAftRegulationEffective(String aftRegulationEffective) {
        this.aftRegulationEffective = aftRegulationEffective;
    }

    public String getAftDegradeEffective() {
        return ObjectUtils.isEmpty(aftDegradeEffective) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeEffective;
    }

    public void setAftDegradeEffective(String aftDegradeEffective) {
        this.aftDegradeEffective = aftDegradeEffective;
    }

    public String getAftTimeWindow() {
        return ObjectUtils.isEmpty(aftTimeWindow) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftTimeWindow;
    }

    public void setAftTimeWindow(String aftTimeWindow) {
        this.aftTimeWindow = aftTimeWindow;
    }

    public String getAftLeastWindowCount() {
        return ObjectUtils.isEmpty(aftLeastWindowCount) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftLeastWindowCount;
    }

    public void setAftLeastWindowCount(String aftLeastWindowCount) {
        this.aftLeastWindowCount = aftLeastWindowCount;
    }

    public String getAftLeastWindowExceptionRateMultiple() {
        return ObjectUtils.isEmpty(aftLeastWindowExceptionRateMultiple) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName())
            : aftLeastWindowExceptionRateMultiple;
    }

    public void setAftLeastWindowExceptionRateMultiple(String aftLeastWindowExceptionRateMultiple) {
        this.aftLeastWindowExceptionRateMultiple = aftLeastWindowExceptionRateMultiple;
    }

    public String getAftWeightDegradeRate() {
        return ObjectUtils.isEmpty(aftWeightDegradeRate) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftWeightDegradeRate;
    }

    public void setAftWeightDegradeRate(String aftWeightDegradeRate) {
        this.aftWeightDegradeRate = aftWeightDegradeRate;
    }

    public String getAftWeightRecoverRate() {
        return ObjectUtils.isEmpty(aftWeightRecoverRate) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftWeightRecoverRate;
    }

    public void setAftWeightRecoverRate(String aftWeightRecoverRate) {
        this.aftWeightRecoverRate = aftWeightRecoverRate;
    }

    public String getAftDegradeLeastWeight() {
        return ObjectUtils.isEmpty(aftDegradeLeastWeight) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeLeastWeight;
    }

    public void setAftDegradeLeastWeight(String aftDegradeLeastWeight) {
        this.aftDegradeLeastWeight = aftDegradeLeastWeight;
    }

    public String getAftDegradeMaxIpCount() {
        return ObjectUtils.isEmpty(aftDegradeMaxIpCount) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeMaxIpCount;
    }

    public void setAftDegradeMaxIpCount(String aftDegradeMaxIpCount) {
        this.aftDegradeMaxIpCount = aftDegradeMaxIpCount;
    }

    public String getBoltPort() {

        if (environment.containsProperty(SofaOptions.CONFIG_TR_PORT)) {
            return environment.getProperty(SofaOptions.CONFIG_TR_PORT);
        }

        return ObjectUtils.isEmpty(boltPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltPort;
    }

    public void setBoltPort(String boltPort) {
        this.boltPort = boltPort;
    }

    public String getDubboIoThreadSize() {
        return ObjectUtils.isEmpty(dubboIoThreadSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboIoThreadSize;
    }

    public void setDubboIoThreadSize(String dubboIoThreadSize) {
        this.dubboIoThreadSize = dubboIoThreadSize;
    }

    public String getBoltThreadPoolCoreSize() {
        if (environment.containsProperty(SofaOptions.TR_MIN_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MIN_POOLSIZE);
        }

        return ObjectUtils.isEmpty(boltThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolCoreSize;
    }

    public void setBoltThreadPoolCoreSize(String boltThreadPoolCoreSize) {
        this.boltThreadPoolCoreSize = boltThreadPoolCoreSize;
    }

    public String getBoltThreadPoolMaxSize() {

        if (environment.containsProperty(SofaOptions.TR_MAX_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MAX_POOLSIZE);
        }

        return ObjectUtils.isEmpty(boltThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolMaxSize;
    }

    public void setBoltThreadPoolMaxSize(String boltThreadPoolMaxSize) {
        this.boltThreadPoolMaxSize = boltThreadPoolMaxSize;
    }

    public String getBoltAcceptsSize() {
        return ObjectUtils.isEmpty(boltAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltAcceptsSize;
    }

    public void setBoltAcceptsSize(String boltAcceptsSize) {
        this.boltAcceptsSize = boltAcceptsSize;
    }

    public String getRestHostname() {
        return ObjectUtils.isEmpty(restHostname) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restHostname;
    }

    public void setRestHostname(String restHostname) {
        this.restHostname = restHostname;
    }

    public String getRestPort() {
        return ObjectUtils.isEmpty(restPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public String getRestIoThreadSize() {
        return ObjectUtils.isEmpty(restIoThreadSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restIoThreadSize;
    }

    public void setRestIoThreadSize(String restIoThreadSize) {
        this.restIoThreadSize = restIoThreadSize;
    }

    public String getRestContextPath() {
        return ObjectUtils.isEmpty(restContextPath) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restContextPath;
    }

    public void setRestContextPath(String restContextPath) {
        this.restContextPath = restContextPath;
    }

    public String getRestThreadPoolMaxSize() {
        return ObjectUtils.isEmpty(restThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restThreadPoolMaxSize;
    }

    public void setRestThreadPoolMaxSize(String restThreadPoolMaxSize) {
        this.restThreadPoolMaxSize = restThreadPoolMaxSize;
    }

    public String getRestMaxRequestSize() {
        return ObjectUtils.isEmpty(restMaxRequestSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restMaxRequestSize;
    }

    public void setRestMaxRequestSize(String restMaxRequestSize) {
        this.restMaxRequestSize = restMaxRequestSize;
    }

    public String getRestTelnet() {
        return ObjectUtils.isEmpty(restTelnet) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restTelnet;
    }

    public void setRestTelnet(String restTelnet) {
        this.restTelnet = restTelnet;
    }

    public String getRestDaemon() {
        return ObjectUtils.isEmpty(restDaemon) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restDaemon;
    }

    public void setRestDaemon(String restDaemon) {
        this.restDaemon = restDaemon;
    }

    public String getDubboPort() {
        return ObjectUtils.isEmpty(dubboPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

    public String getDubboThreadPoolMaxSize() {
        return ObjectUtils.isEmpty(dubboThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboThreadPoolMaxSize;
    }

    public void setDubboThreadPoolMaxSize(String dubboThreadPoolMaxSize) {
        this.dubboThreadPoolMaxSize = dubboThreadPoolMaxSize;
    }

    public String getDubboAcceptsSize() {
        return ObjectUtils.isEmpty(dubboAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboAcceptsSize;
    }

    public void setDubboAcceptsSize(String dubboAcceptsSize) {
        this.dubboAcceptsSize = dubboAcceptsSize;
    }

    public String getRegistryAddress() {
        return ObjectUtils.isEmpty(registryAddress) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getBoltThreadPoolQueueSize() {

        if (environment.containsProperty(SofaOptions.TR_QUEUE_SIZE)) {
            return environment.getProperty(SofaOptions.TR_QUEUE_SIZE);
        }
        return ObjectUtils.isEmpty(boltThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolQueueSize;
    }

    public void setBoltThreadPoolQueueSize(String boltThreadPoolQueueSize) {
        this.boltThreadPoolQueueSize = boltThreadPoolQueueSize;
    }

    public String getVirtualHost() {
        return ObjectUtils.isEmpty(virtualHost) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getBoundHost() {
        return ObjectUtils.isEmpty(boundHost) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boundHost;
    }

    public void setBoundHost(String boundHost) {
        this.boundHost = boundHost;
    }

    public String getVirtualPort() {
        return ObjectUtils.isEmpty(virtualPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : virtualPort;
    }

    public void setVirtualPort(String virtualPort) {
        this.virtualPort = virtualPort;
    }

    public String getEnabledIpRange() {

        if (environment.containsProperty(SofaOptions.CONFIG_IP_RANGE)) {
            return environment.getProperty(SofaOptions.CONFIG_IP_RANGE);
        }

        return ObjectUtils.isEmpty(enabledIpRange) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : enabledIpRange;
    }

    public void setEnabledIpRange(String enabledIpRange) {
        this.enabledIpRange = enabledIpRange;
    }

    public String getBindNetworkInterface() {

        if (environment.containsProperty(SofaOptions.CONFIG_NI_BIND)) {
            return environment.getProperty(SofaOptions.CONFIG_NI_BIND);
        }

        return ObjectUtils.isEmpty(bindNetworkInterface) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : bindNetworkInterface;
    }

    public void setBindNetworkInterface(String bindNetworkInterface) {
        this.bindNetworkInterface = bindNetworkInterface;
    }

    public String getH2cPort() {
        return ObjectUtils.isEmpty(h2cPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cPort;
    }

    public void setH2cPort(String h2cPort) {
        this.h2cPort = h2cPort;
    }

    public String getH2cThreadPoolCoreSize() {
        return ObjectUtils.isEmpty(h2cThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolCoreSize;
    }

    public void setH2cThreadPoolCoreSize(String h2cThreadPoolCoreSize) {
        this.h2cThreadPoolCoreSize = h2cThreadPoolCoreSize;
    }

    public String getH2cThreadPoolMaxSize() {
        return ObjectUtils.isEmpty(h2cThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolMaxSize;
    }

    public void setH2cThreadPoolMaxSize(String h2cThreadPoolMaxSize) {
        this.h2cThreadPoolMaxSize = h2cThreadPoolMaxSize;
    }

    public String getH2cThreadPoolQueueSize() {
        return ObjectUtils.isEmpty(h2cThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolQueueSize;
    }

    public void setH2cThreadPoolQueueSize(String h2cThreadPoolQueueSize) {
        this.h2cThreadPoolQueueSize = h2cThreadPoolQueueSize;
    }

    public String getH2cAcceptsSize() {
        return ObjectUtils.isEmpty(h2cAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cAcceptsSize;
    }

    public void setH2cAcceptsSize(String h2cAcceptsSize) {
        this.h2cAcceptsSize = h2cAcceptsSize;
    }

    public String getHttpPort() {
        return ObjectUtils.isEmpty(httpPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getHttpThreadPoolCoreSize() {
        return ObjectUtils.isEmpty(httpThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : httpThreadPoolCoreSize;
    }

    public void setHttpThreadPoolCoreSize(String httpThreadPoolCoreSize) {
        this.httpThreadPoolCoreSize = httpThreadPoolCoreSize;
    }

    public String getHttpThreadPoolMaxSize() {
        return ObjectUtils.isEmpty(httpThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : httpThreadPoolMaxSize;
    }

    public void setHttpThreadPoolMaxSize(String httpThreadPoolMaxSize) {
        this.httpThreadPoolMaxSize = httpThreadPoolMaxSize;
    }

    public String getHttpThreadPoolQueueSize() {
        return ObjectUtils.isEmpty(httpThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : httpThreadPoolQueueSize;
    }

    public void setHttpThreadPoolQueueSize(String httpThreadPoolQueueSize) {
        this.httpThreadPoolQueueSize = httpThreadPoolQueueSize;
    }

    public String getHttpAcceptsSize() {
        return ObjectUtils.isEmpty(httpAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : httpAcceptsSize;
    }

    public void setHttpAcceptsSize(String httpAcceptsSize) {
        this.httpAcceptsSize = httpAcceptsSize;
    }

    public String getLookoutCollectDisable() {
        return ObjectUtils.isEmpty(lookoutCollectDisable) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : lookoutCollectDisable;
    }

    public void setLookoutCollectDisable(String lookoutCollectDisable) {
        this.lookoutCollectDisable = lookoutCollectDisable;
    }

    public Map<String, String> getRegistries() {
        return registries;
    }

    public void setRegistries(Map<String, String> registries) {
        this.registries = registries;
    }

    public String getEnableMesh() {
        return ObjectUtils.isEmpty(enableMesh) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : enableMesh;
    }

    public void setEnableMesh(String enableMesh) {
        this.enableMesh = enableMesh;
    }

    public String getConsumerRepeatedReferenceLimit() {
        return ObjectUtils.isEmpty(consumerRepeatedReferenceLimit) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : consumerRepeatedReferenceLimit;
    }

    public void setConsumerRepeatedReferenceLimit(String consumerRepeatedReferenceLimit) {
        this.consumerRepeatedReferenceLimit = consumerRepeatedReferenceLimit;
    }

    public String getDefaultTracer() {
        return defaultTracer;
    }

    public void setDefaultTracer(String defaultTracer) {
        this.defaultTracer = defaultTracer;
    }

    public String getHystrixEnable() {
        return ObjectUtils.isEmpty(hystrixEnable) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : hystrixEnable;
    }

    public void setHystrixEnable(String hystrixEnable) {
        this.hystrixEnable = hystrixEnable;
    }

    public String getRestAllowedOrigins() {
        return ObjectUtils.isEmpty(restAllowedOrigins) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restAllowedOrigins;
    }

    public String getTriplePort() {
        return ObjectUtils.isEmpty(triplePort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : triplePort;
    }

    public void setTriplePort(String triplePort) {
        this.triplePort = triplePort;
    }

    public String getTripleThreadPoolCoreSize() {
        return ObjectUtils.isEmpty(tripleThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : tripleThreadPoolCoreSize;
    }

    public void setTripleThreadPoolCoreSize(String tripleThreadPoolCoreSize) {
        this.tripleThreadPoolCoreSize = tripleThreadPoolCoreSize;
    }

    public String getTripleThreadPoolMaxSize() {
        return ObjectUtils.isEmpty(tripleThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : tripleThreadPoolMaxSize;
    }

    public void setTripleThreadPoolMaxSize(String tripleThreadPoolMaxSize) {
        this.tripleThreadPoolMaxSize = tripleThreadPoolMaxSize;
    }

    public String getTripleThreadPoolQueueSize() {
        return ObjectUtils.isEmpty(tripleThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : tripleThreadPoolQueueSize;
    }

    public void setTripleThreadPoolQueueSize(String tripleThreadPoolQueueSize) {
        this.tripleThreadPoolQueueSize = tripleThreadPoolQueueSize;
    }

    public String getTripleAcceptsSize() {
        return ObjectUtils.isEmpty(tripleAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : tripleAcceptsSize;
    }

    public void setTripleAcceptsSize(String tripleAcceptsSize) {
        this.tripleAcceptsSize = tripleAcceptsSize;
    }

    public void setRestAllowedOrigins(String restAllowedOrigins) {
        this.restAllowedOrigins = restAllowedOrigins;
    }

    private String getDotString(String enclosingMethodName) {
        if (environment == null) {
            return null;
        }
        String key = camelToDot(enclosingMethodName.substring(3));
        String result = environment.getProperty(PREFIX + "." + key);
        if (result != null) {
            return result;
        } else {
            return environment.getProperty(OLD_PREFIX + "." + key);
        }
    }

    public String camelToDot(String camelCaseString) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, camelCaseString).replaceAll("-",
            ".");
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isRestSwagger() {
        return restSwagger;
    }

    public void setRestSwagger(boolean restSwagger) {
        this.restSwagger = restSwagger;
    }

    public String getMockUrl() {
        return mockUrl;
    }

    public void setMockUrl(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    public String getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(String dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    public Boolean getEnableSwagger() {
        return enableSwagger;
    }

    public void setEnableSwagger(Boolean enableswagger) {
        this.enableSwagger = enableswagger;
    }

    public Boolean getBoltProcessInIoThread() {
        return boltProcessInIoThread;
    }

    public void setBoltProcessInIoThread(Boolean boltProcessInIoThread) {
        this.boltProcessInIoThread = boltProcessInIoThread;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
