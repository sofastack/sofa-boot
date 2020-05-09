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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhaowang
 * @version : ExtensionProperties.java, v 0.1 2020年05月09日 2:13 下午 zhaowang Exp $
 */
@ConfigurationProperties(ExtensionProperties.PREFIX)
public class ExtensionProperties {
    public static final String PREFIX = "com.alipay.sofa.rpc.ext";

    private String             addressHolder;

    private String             cluster;

    private String             connectionHolder;

    private String             loadBalancer;

    private String             proxy;

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public String getConnectionHolder() {
        return connectionHolder;
    }

    public void setConnectionHolder(String connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getAddressHolder() {
        return addressHolder;
    }

    public void setAddressHolder(String addressHolder) {
        this.addressHolder = addressHolder;
    }

}