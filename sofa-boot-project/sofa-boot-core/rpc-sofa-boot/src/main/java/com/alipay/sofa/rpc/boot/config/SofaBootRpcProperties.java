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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.staticcompile.annotations.ContainReflection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.alipay.sofa.rpc.common.SofaOptions;
import com.google.common.base.CaseFormat;

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
        if (StringUtils.isEmpty(aftRegulationEffective)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftRegulationEffective")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftRegulationEffective;
    }

    public void setAftRegulationEffective(String aftRegulationEffective) {
        this.aftRegulationEffective = aftRegulationEffective;
    }

    public String getAftDegradeEffective() {
        if (StringUtils.isEmpty(aftDegradeEffective)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftDegradeEffective")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftDegradeEffective;
    }

    public void setAftDegradeEffective(String aftDegradeEffective) {
        this.aftDegradeEffective = aftDegradeEffective;
    }

    public String getAftTimeWindow() {
        if (StringUtils.isEmpty(aftTimeWindow)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftTimeWindow")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftTimeWindow;
    }

    public void setAftTimeWindow(String aftTimeWindow) {
        this.aftTimeWindow = aftTimeWindow;
    }

    public String getAftLeastWindowCount() {
        if (StringUtils.isEmpty(aftLeastWindowCount)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftLeastWindowCount")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftLeastWindowCount;
    }

    public void setAftLeastWindowCount(String aftLeastWindowCount) {
        this.aftLeastWindowCount = aftLeastWindowCount;
    }

    public String getAftLeastWindowExceptionRateMultiple() {
        if (StringUtils.isEmpty(aftLeastWindowExceptionRateMultiple)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftLeastWindowExceptionRateMultiple")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftLeastWindowExceptionRateMultiple;
    }

    public void setAftLeastWindowExceptionRateMultiple(String aftLeastWindowExceptionRateMultiple) {
        this.aftLeastWindowExceptionRateMultiple = aftLeastWindowExceptionRateMultiple;
    }

    public String getAftWeightDegradeRate() {
        if (StringUtils.isEmpty(aftWeightDegradeRate)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftWeightDegradeRate")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftWeightDegradeRate;
    }

    public void setAftWeightDegradeRate(String aftWeightDegradeRate) {
        this.aftWeightDegradeRate = aftWeightDegradeRate;
    }

    public String getAftWeightRecoverRate() {
        if (StringUtils.isEmpty(aftWeightRecoverRate)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftWeightRecoverRate")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftWeightRecoverRate;
    }

    public void setAftWeightRecoverRate(String aftWeightRecoverRate) {
        this.aftWeightRecoverRate = aftWeightRecoverRate;
    }

    public String getAftDegradeLeastWeight() {
        if (StringUtils.isEmpty(aftDegradeLeastWeight)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftDegradeLeastWeight")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftDegradeLeastWeight;
    }

    public void setAftDegradeLeastWeight(String aftDegradeLeastWeight) {
        this.aftDegradeLeastWeight = aftDegradeLeastWeight;
    }

    public String getAftDegradeMaxIpCount() {
        if (StringUtils.isEmpty(aftDegradeMaxIpCount)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getAftDegradeMaxIpCount")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return aftDegradeMaxIpCount;
    }

    public void setAftDegradeMaxIpCount(String aftDegradeMaxIpCount) {
        this.aftDegradeMaxIpCount = aftDegradeMaxIpCount;
    }

    public String getBoltPort() {

        if (environment.containsProperty(SofaOptions.CONFIG_TR_PORT)) {
            return environment.getProperty(SofaOptions.CONFIG_TR_PORT);
        }

        if (StringUtils.isEmpty(boltPort)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoltPort")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boltPort;
    }

    public void setBoltPort(String boltPort) {
        this.boltPort = boltPort;
    }

    public String getDubboIoThreadSize() {
        if (StringUtils.isEmpty(dubboIoThreadSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboIoThreadSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboIoThreadSize;
    }

    public void setDubboIoThreadSize(String dubboIoThreadSize) {
        this.dubboIoThreadSize = dubboIoThreadSize;
    }

    public String getBoltThreadPoolCoreSize() {

        if (environment.containsProperty(SofaOptions.TR_MIN_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MIN_POOLSIZE);
        }

        if (StringUtils.isEmpty(boltThreadPoolCoreSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoltThreadPoolCoreSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boltThreadPoolCoreSize;
    }

    public void setBoltThreadPoolCoreSize(String boltThreadPoolCoreSize) {
        this.boltThreadPoolCoreSize = boltThreadPoolCoreSize;
    }

    public String getBoltThreadPoolMaxSize() {

        if (environment.containsProperty(SofaOptions.TR_MAX_POOLSIZE)) {
            return environment.getProperty(SofaOptions.TR_MAX_POOLSIZE);
        }

        if (StringUtils.isEmpty(boltThreadPoolMaxSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoltThreadPoolMaxSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boltThreadPoolMaxSize;
    }

    public void setBoltThreadPoolMaxSize(String boltThreadPoolMaxSize) {
        this.boltThreadPoolMaxSize = boltThreadPoolMaxSize;
    }

    public String getBoltAcceptsSize() {
        if (StringUtils.isEmpty(boltAcceptsSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoltAcceptsSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boltAcceptsSize;
    }

    public void setBoltAcceptsSize(String boltAcceptsSize) {
        this.boltAcceptsSize = boltAcceptsSize;
    }

    public String getRestHostname() {
        if (StringUtils.isEmpty(restHostname)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestHostname")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restHostname;
    }

    public void setRestHostname(String restHostname) {
        this.restHostname = restHostname;
    }

    public String getRestPort() {
        if (StringUtils.isEmpty(restPort)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestPort")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public String getRestIoThreadSize() {
        if (StringUtils.isEmpty(restIoThreadSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestIoThreadSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restIoThreadSize;
    }

    public void setRestIoThreadSize(String restIoThreadSize) {
        this.restIoThreadSize = restIoThreadSize;
    }

    public String getRestContextPath() {
        if (StringUtils.isEmpty(restContextPath)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestContextPath")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restContextPath;
    }

    public void setRestContextPath(String restContextPath) {
        this.restContextPath = restContextPath;
    }

    public String getRestThreadPoolMaxSize() {
        if (StringUtils.isEmpty(restThreadPoolMaxSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestThreadPoolMaxSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restThreadPoolMaxSize;
    }

    public void setRestThreadPoolMaxSize(String restThreadPoolMaxSize) {
        this.restThreadPoolMaxSize = restThreadPoolMaxSize;
    }

    public String getRestMaxRequestSize() {
        if (StringUtils.isEmpty(restMaxRequestSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestMaxRequestSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restMaxRequestSize;
    }

    public void setRestMaxRequestSize(String restMaxRequestSize) {
        this.restMaxRequestSize = restMaxRequestSize;
    }

    public String getRestTelnet() {
        if (StringUtils.isEmpty(restTelnet)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestTelnet")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restTelnet;
    }

    public void setRestTelnet(String restTelnet) {
        this.restTelnet = restTelnet;
    }

    public String getRestDaemon() {
        if (StringUtils.isEmpty(restDaemon)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestDaemon")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restDaemon;
    }

    public void setRestDaemon(String restDaemon) {
        this.restDaemon = restDaemon;
    }

    public String getDubboPort() {
        if (StringUtils.isEmpty(dubboPort)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboPort")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

    public String getDubboThreadPoolMaxSize() {
        if (StringUtils.isEmpty(dubboThreadPoolMaxSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboThreadPoolMaxSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboThreadPoolMaxSize;
    }

    public void setDubboThreadPoolMaxSize(String dubboThreadPoolMaxSize) {
        this.dubboThreadPoolMaxSize = dubboThreadPoolMaxSize;
    }

    public String getDubboAcceptsSize() {
        if (StringUtils.isEmpty(dubboAcceptsSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboAcceptsSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboAcceptsSize;
    }

    public void setDubboAcceptsSize(String dubboAcceptsSize) {
        this.dubboAcceptsSize = dubboAcceptsSize;
    }

    public String getRegistryAddress() {
        if (StringUtils.isEmpty(registryAddress)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRegistryAddress")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getBoltThreadPoolQueueSize() {

        if (environment.containsProperty(SofaOptions.TR_QUEUE_SIZE)) {
            return environment.getProperty(SofaOptions.TR_QUEUE_SIZE);
        }
        if (StringUtils.isEmpty(boltThreadPoolQueueSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoltThreadPoolQueueSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boltThreadPoolQueueSize;
    }

    public void setBoltThreadPoolQueueSize(String boltThreadPoolQueueSize) {
        this.boltThreadPoolQueueSize = boltThreadPoolQueueSize;
    }

    public String getDubboThreadPoolCoreSize() {
        if (StringUtils.isEmpty(dubboThreadPoolCoreSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboThreadPoolCoreSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboThreadPoolCoreSize;
    }

    public void setDubboThreadPoolCoreSize(String dubboThreadPoolCoreSize) {
        this.dubboThreadPoolCoreSize = dubboThreadPoolCoreSize;
    }

    public String getDubboThreadPoolQueueSize() {
        if (StringUtils.isEmpty(dubboThreadPoolQueueSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getDubboThreadPoolQueueSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return dubboThreadPoolQueueSize;
    }

    public void setDubboThreadPoolQueueSize(String dubboThreadPoolQueueSize) {
        this.dubboThreadPoolQueueSize = dubboThreadPoolQueueSize;
    }

    public String getRestThreadPoolCoreSize() {
        if (StringUtils.isEmpty(restThreadPoolCoreSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestThreadPoolCoreSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restThreadPoolCoreSize;
    }

    public void setRestThreadPoolCoreSize(String restThreadPoolCoreSize) {
        this.restThreadPoolCoreSize = restThreadPoolCoreSize;
    }

    public String getVirtualHost() {
        if (StringUtils.isEmpty(virtualHost)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getVirtualHost")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getBoundHost() {
        if (StringUtils.isEmpty(boundHost)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBoundHost")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return boundHost;
    }

    public void setBoundHost(String boundHost) {
        this.boundHost = boundHost;
    }

    public String getVirtualPort() {
        if (StringUtils.isEmpty(virtualPort)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getVirtualPort")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return virtualPort;
    }

    public void setVirtualPort(String virtualPort) {
        this.virtualPort = virtualPort;
    }

    public String getEnabledIpRange() {

        if (environment.containsProperty(SofaOptions.CONFIG_IP_RANGE)) {
            return environment.getProperty(SofaOptions.CONFIG_IP_RANGE);
        }

        if (StringUtils.isEmpty(enabledIpRange)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getEnabledIpRange")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return enabledIpRange;
    }

    public void setEnabledIpRange(String enabledIpRange) {
        this.enabledIpRange = enabledIpRange;
    }

    public String getBindNetworkInterface() {

        if (environment.containsProperty(SofaOptions.CONFIG_NI_BIND)) {
            return environment.getProperty(SofaOptions.CONFIG_NI_BIND);
        }

        if (StringUtils.isEmpty(bindNetworkInterface)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getBindNetworkInterface")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return bindNetworkInterface;
    }

    public void setBindNetworkInterface(String bindNetworkInterface) {
        this.bindNetworkInterface = bindNetworkInterface;
    }

    public String getH2cPort() {
        if (StringUtils.isEmpty(h2cPort)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getH2cPort")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return h2cPort;
    }

    public void setH2cPort(String h2cPort) {
        this.h2cPort = h2cPort;
    }

    public String getH2cThreadPoolCoreSize() {
        if (StringUtils.isEmpty(h2cThreadPoolCoreSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getH2cThreadPoolCoreSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return h2cThreadPoolCoreSize;
    }

    public void setH2cThreadPoolCoreSize(String h2cThreadPoolCoreSize) {
        this.h2cThreadPoolCoreSize = h2cThreadPoolCoreSize;
    }

    public String getH2cThreadPoolMaxSize() {
        if (StringUtils.isEmpty(h2cThreadPoolMaxSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getH2cThreadPoolMaxSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return h2cThreadPoolMaxSize;
    }

    public void setH2cThreadPoolMaxSize(String h2cThreadPoolMaxSize) {
        this.h2cThreadPoolMaxSize = h2cThreadPoolMaxSize;
    }

    public String getH2cThreadPoolQueueSize() {
        if (StringUtils.isEmpty(h2cThreadPoolQueueSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getH2cThreadPoolQueueSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return h2cThreadPoolQueueSize;
    }

    public void setH2cThreadPoolQueueSize(String h2cThreadPoolQueueSize) {
        this.h2cThreadPoolQueueSize = h2cThreadPoolQueueSize;
    }

    public String getH2cAcceptsSize() {
        if (StringUtils.isEmpty(h2cAcceptsSize)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getH2cAcceptsSize")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return h2cAcceptsSize;
    }

    public void setH2cAcceptsSize(String h2cAcceptsSize) {
        this.h2cAcceptsSize = h2cAcceptsSize;
    }

    public String getLookoutCollectDisable() {
        if (StringUtils.isEmpty(lookoutCollectDisable)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getLookoutCollectDisable")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return lookoutCollectDisable;
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
        if (StringUtils.isEmpty(enableMesh)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getEnableMesh")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return enableMesh;
    }

    public void setEnableMesh(String enableMesh) {
        this.enableMesh = enableMesh;
    }

    public String getConsumerRepeatedReferenceLimit() {
        if (StringUtils.isEmpty(consumerRepeatedReferenceLimit)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getConsumerRepeatedReferenceLimit")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return consumerRepeatedReferenceLimit;
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
        if (StringUtils.isEmpty(hystrixEnable)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getHystrixEnable")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return hystrixEnable;
    }

    public void setHystrixEnable(String hystrixEnable) {
        this.hystrixEnable = hystrixEnable;
    }

    public String getRestAllowedOrigins() {
        if (StringUtils.isEmpty(restAllowedOrigins)) {
            Object object = new Object() {
            };
            @ContainReflection(value = "com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties", method = "getRestAllowedOrigins")
            String str = object.getClass().getEnclosingMethod().getName();
            return getDotString(str);
        }
        return restAllowedOrigins;
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

    public String camelToDot(String camelCaseString) {
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
