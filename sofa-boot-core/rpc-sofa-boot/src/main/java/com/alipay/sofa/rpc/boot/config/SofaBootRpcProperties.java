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
package com.alipay.sofa.rpc.boot.config;

import com.alipay.sofa.rpc.common.SofaOptions;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author khotyn
 */
@ConfigurationProperties(SofaBootRpcProperties.PREFIX)
public class SofaBootRpcProperties {
    public static final String  PREFIX     = "com.alipay.sofa.rpc";

    @Autowired
    private Environment         environment;

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
    // has no use
    /**
     * the core thread pool size of rest （rest 核心线程数）
     */
    private String              restThreadPoolCoreSize;
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
    //has no use
    /**
     * the core thread pool size of dubbo （dubbo 核心线程数）
     */
    private String              dubboThreadPoolCoreSize;

    /**
     * the max thread pool size of dubbo （dubbo 最大线程数）
     */
    private String              dubboThreadPoolMaxSize;
    //has no use
    /**
     * the queue size of dubbo server（dubbo 线程池队列）
     */
    private String              dubboThreadPoolQueueSize;
    /**
     * the max accept size of dubbo (dubbo 服务端允许客户端建立的连接数)
     */
    private String              dubboAcceptsSize;
    /* dubbo  end*/

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

    public String getAftRegulationEffective() {
        return StringUtils.isEmpty(aftRegulationEffective) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftRegulationEffective;
    }

    public void setAftRegulationEffective(String aftRegulationEffective) {
        this.aftRegulationEffective = aftRegulationEffective;
    }

    public String getAftDegradeEffective() {
        return StringUtils.isEmpty(aftDegradeEffective) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeEffective;
    }

    public void setAftDegradeEffective(String aftDegradeEffective) {
        this.aftDegradeEffective = aftDegradeEffective;
    }

    public String getAftTimeWindow() {
        return StringUtils.isEmpty(aftTimeWindow) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftTimeWindow;
    }

    public void setAftTimeWindow(String aftTimeWindow) {
        this.aftTimeWindow = aftTimeWindow;
    }

    public String getAftLeastWindowCount() {
        return StringUtils.isEmpty(aftLeastWindowCount) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftLeastWindowCount;
    }

    public void setAftLeastWindowCount(String aftLeastWindowCount) {
        this.aftLeastWindowCount = aftLeastWindowCount;
    }

    public String getAftLeastWindowExceptionRateMultiple() {
        return StringUtils.isEmpty(aftLeastWindowExceptionRateMultiple)
            ? getDotString(new Object() {
            }.getClass().getEnclosingMethod().getName())
            : aftLeastWindowExceptionRateMultiple;
    }

    public void setAftLeastWindowExceptionRateMultiple(String aftLeastWindowExceptionRateMultiple) {
        this.aftLeastWindowExceptionRateMultiple = aftLeastWindowExceptionRateMultiple;
    }

    public String getAftWeightDegradeRate() {
        return StringUtils.isEmpty(aftWeightDegradeRate) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftWeightDegradeRate;
    }

    public void setAftWeightDegradeRate(String aftWeightDegradeRate) {
        this.aftWeightDegradeRate = aftWeightDegradeRate;
    }

    public String getAftWeightRecoverRate() {
        return StringUtils.isEmpty(aftWeightRecoverRate) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftWeightRecoverRate;
    }

    public void setAftWeightRecoverRate(String aftWeightRecoverRate) {
        this.aftWeightRecoverRate = aftWeightRecoverRate;
    }

    public String getAftDegradeLeastWeight() {
        return StringUtils.isEmpty(aftDegradeLeastWeight) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeLeastWeight;
    }

    public void setAftDegradeLeastWeight(String aftDegradeLeastWeight) {
        this.aftDegradeLeastWeight = aftDegradeLeastWeight;
    }

    public String getAftDegradeMaxIpCount() {
        return StringUtils.isEmpty(aftDegradeMaxIpCount) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : aftDegradeMaxIpCount;
    }

    public void setAftDegradeMaxIpCount(String aftDegradeMaxIpCount) {
        this.aftDegradeMaxIpCount = aftDegradeMaxIpCount;
    }

    public String getBoltPort() {

        if (environment.containsProperty(SofaOptions.CONFIG_TR_PORT)) {
            return environment.getProperty(SofaOptions.CONFIG_TR_PORT);
        }

        return StringUtils.isEmpty(boltPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltPort;
    }

    public void setBoltPort(String boltPort) {
        this.boltPort = boltPort;
    }

    public String getDubboIoThreadSize() {
        return StringUtils.isEmpty(dubboIoThreadSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboIoThreadSize;
    }

    public void setDubboIoThreadSize(String dubboIoThreadSize) {
        this.dubboIoThreadSize = dubboIoThreadSize;
    }

    public String getBoltThreadPoolCoreSize() {

        if (environment.containsProperty(SofaOptions.TR_MIN_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MIN_POOLSIZE);
        }

        return StringUtils.isEmpty(boltThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolCoreSize;
    }

    public void setBoltThreadPoolCoreSize(String boltThreadPoolCoreSize) {
        this.boltThreadPoolCoreSize = boltThreadPoolCoreSize;
    }

    public String getBoltThreadPoolMaxSize() {

        if (environment.containsProperty(SofaOptions.TR_MAX_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MAX_POOLSIZE);
        }

        return StringUtils.isEmpty(boltThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolMaxSize;
    }

    public void setBoltThreadPoolMaxSize(String boltThreadPoolMaxSize) {
        this.boltThreadPoolMaxSize = boltThreadPoolMaxSize;
    }

    public String getBoltAcceptsSize() {
        return StringUtils.isEmpty(boltAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltAcceptsSize;
    }

    public void setBoltAcceptsSize(String boltAcceptsSize) {
        this.boltAcceptsSize = boltAcceptsSize;
    }

    public String getRestHostname() {
        return StringUtils.isEmpty(restHostname) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restHostname;
    }

    public void setRestHostname(String restHostname) {
        this.restHostname = restHostname;
    }

    public String getRestPort() {
        return StringUtils.isEmpty(restPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public String getRestIoThreadSize() {
        return StringUtils.isEmpty(restIoThreadSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restIoThreadSize;
    }

    public void setRestIoThreadSize(String restIoThreadSize) {
        this.restIoThreadSize = restIoThreadSize;
    }

    public String getRestContextPath() {
        return StringUtils.isEmpty(restContextPath) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restContextPath;
    }

    public void setRestContextPath(String restContextPath) {
        this.restContextPath = restContextPath;
    }

    public String getRestThreadPoolMaxSize() {
        return StringUtils.isEmpty(restThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restThreadPoolMaxSize;
    }

    public void setRestThreadPoolMaxSize(String restThreadPoolMaxSize) {
        this.restThreadPoolMaxSize = restThreadPoolMaxSize;
    }

    public String getRestMaxRequestSize() {
        return StringUtils.isEmpty(restMaxRequestSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restMaxRequestSize;
    }

    public void setRestMaxRequestSize(String restMaxRequestSize) {
        this.restMaxRequestSize = restMaxRequestSize;
    }

    public String getRestTelnet() {
        return StringUtils.isEmpty(restTelnet) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restTelnet;
    }

    public void setRestTelnet(String restTelnet) {
        this.restTelnet = restTelnet;
    }

    public String getRestDaemon() {
        return StringUtils.isEmpty(restDaemon) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restDaemon;
    }

    public void setRestDaemon(String restDaemon) {
        this.restDaemon = restDaemon;
    }

    public String getDubboPort() {
        return StringUtils.isEmpty(dubboPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

    public String getDubboThreadPoolMaxSize() {
        return StringUtils.isEmpty(dubboThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboThreadPoolMaxSize;
    }

    public void setDubboThreadPoolMaxSize(String dubboThreadPoolMaxSize) {
        this.dubboThreadPoolMaxSize = dubboThreadPoolMaxSize;
    }

    public String getDubboAcceptsSize() {
        return StringUtils.isEmpty(dubboAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboAcceptsSize;
    }

    public void setDubboAcceptsSize(String dubboAcceptsSize) {
        this.dubboAcceptsSize = dubboAcceptsSize;
    }

    public String getRegistryAddress() {
        return StringUtils.isEmpty(registryAddress) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getBoltThreadPoolQueueSize() {

        if (environment.containsProperty(SofaOptions.TR_QUEUE_SIZE)) {
            return environment.getProperty(SofaOptions.TR_QUEUE_SIZE);
        }
        return StringUtils.isEmpty(boltThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boltThreadPoolQueueSize;
    }

    public void setBoltThreadPoolQueueSize(String boltThreadPoolQueueSize) {
        this.boltThreadPoolQueueSize = boltThreadPoolQueueSize;
    }

    public String getDubboThreadPoolCoreSize() {
        return StringUtils.isEmpty(dubboThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboThreadPoolCoreSize;
    }

    public void setDubboThreadPoolCoreSize(String dubboThreadPoolCoreSize) {
        this.dubboThreadPoolCoreSize = dubboThreadPoolCoreSize;
    }

    public String getDubboThreadPoolQueueSize() {
        return StringUtils.isEmpty(dubboThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : dubboThreadPoolQueueSize;
    }

    public void setDubboThreadPoolQueueSize(String dubboThreadPoolQueueSize) {
        this.dubboThreadPoolQueueSize = dubboThreadPoolQueueSize;
    }

    public String getRestThreadPoolCoreSize() {
        return StringUtils.isEmpty(restThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restThreadPoolCoreSize;
    }

    public void setRestThreadPoolCoreSize(String restThreadPoolCoreSize) {
        this.restThreadPoolCoreSize = restThreadPoolCoreSize;
    }

    public String getVirtualHost() {
        return StringUtils.isEmpty(virtualHost) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getBoundHost() {
        return StringUtils.isEmpty(boundHost) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : boundHost;
    }

    public void setBoundHost(String boundHost) {
        this.boundHost = boundHost;
    }

    public String getVirtualPort() {
        return StringUtils.isEmpty(virtualPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : virtualPort;
    }

    public void setVirtualPort(String virtualPort) {
        this.virtualPort = virtualPort;
    }

    public String getEnabledIpRange() {

        if (environment.containsProperty(SofaOptions.CONFIG_IP_RANGE)) {
            return environment.getProperty(SofaOptions.CONFIG_IP_RANGE);
        }

        return StringUtils.isEmpty(enabledIpRange) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : enabledIpRange;
    }

    public void setEnabledIpRange(String enabledIpRange) {
        this.enabledIpRange = enabledIpRange;
    }

    public String getBindNetworkInterface() {

        if (environment.containsProperty(SofaOptions.CONFIG_NI_BIND)) {
            return environment.getProperty(SofaOptions.CONFIG_NI_BIND);
        }

        return StringUtils.isEmpty(bindNetworkInterface) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : bindNetworkInterface;
    }

    public void setBindNetworkInterface(String bindNetworkInterface) {
        this.bindNetworkInterface = bindNetworkInterface;
    }

    public String getH2cPort() {
        return StringUtils.isEmpty(h2cPort) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cPort;
    }

    public void setH2cPort(String h2cPort) {
        this.h2cPort = h2cPort;
    }

    public String getH2cThreadPoolCoreSize() {
        return StringUtils.isEmpty(h2cThreadPoolCoreSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolCoreSize;
    }

    public void setH2cThreadPoolCoreSize(String h2cThreadPoolCoreSize) {
        this.h2cThreadPoolCoreSize = h2cThreadPoolCoreSize;
    }

    public String getH2cThreadPoolMaxSize() {
        return StringUtils.isEmpty(h2cThreadPoolMaxSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolMaxSize;
    }

    public void setH2cThreadPoolMaxSize(String h2cThreadPoolMaxSize) {
        this.h2cThreadPoolMaxSize = h2cThreadPoolMaxSize;
    }

    public String getH2cThreadPoolQueueSize() {
        return StringUtils.isEmpty(h2cThreadPoolQueueSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cThreadPoolQueueSize;
    }

    public void setH2cThreadPoolQueueSize(String h2cThreadPoolQueueSize) {
        this.h2cThreadPoolQueueSize = h2cThreadPoolQueueSize;
    }

    public String getH2cAcceptsSize() {
        return StringUtils.isEmpty(h2cAcceptsSize) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : h2cAcceptsSize;
    }

    public void setH2cAcceptsSize(String h2cAcceptsSize) {
        this.h2cAcceptsSize = h2cAcceptsSize;
    }

    public String getLookoutCollectDisable() {
        return StringUtils.isEmpty(lookoutCollectDisable) ? getDotString(new Object() {
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
        return StringUtils.isEmpty(enableMesh) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : enableMesh;
    }

    public void setEnableMesh(String enableMesh) {
        this.enableMesh = enableMesh;
    }

    public String getConsumerRepeatedReferenceLimit() {
        return StringUtils.isEmpty(consumerRepeatedReferenceLimit) ? getDotString(new Object() {
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
        return StringUtils.isEmpty(hystrixEnable) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : hystrixEnable;
    }

    public void setHystrixEnable(String hystrixEnable) {
        this.hystrixEnable = hystrixEnable;
    }

    public String getRestAllowedOrigins() {
        return StringUtils.isEmpty(restAllowedOrigins) ? getDotString(new Object() {
        }.getClass().getEnclosingMethod().getName()) : restAllowedOrigins;
    }

    public void setRestAllowedOrigins(String restAllowedOrigins) {
        this.restAllowedOrigins = restAllowedOrigins;
    }

    private String getDotString(String enclosingMethodName) {
        if (environment == null) {
            return null;
        }
        return environment.getProperty(PREFIX + "." + camelToDot(enclosingMethodName.substring(3)));
    }

    String camelToDot(String camelCaseString) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, camelCaseString).replaceAll("-",
            ".");
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isRestSwagger() {
        return restSwagger;
    }

    public void setRestSwagger(boolean restSwagger) {
        this.restSwagger = restSwagger;
    }
}
