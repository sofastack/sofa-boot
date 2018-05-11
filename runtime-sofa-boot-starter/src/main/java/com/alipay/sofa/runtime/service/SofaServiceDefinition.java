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
package com.alipay.sofa.runtime.service;

/**
 * @author xuanbei 18/5/11
 */
public class SofaServiceDefinition {
    /** service interface */
    private Class    interfaceType;
    /** unique id **/
    private String   uniqueId;
    /** binding type, maybe jvm/bolt/rest */
    private String   bindingType;
    /** version */
    private String   version;
    /** normal weight,when default,will use rpc default value, 100 */
    private int      weight;
    /** when warm up,the weight. */
    private int      warmUpWeight;
    /** warm up time, default is 0 */
    private int      warmUpTime;
    /** filter beans */
    private String[] filters;
    /** user thread pool for current service */
    private String   userThreadPool;
    /** registry for this consumer */
    private String   registry;
    /** timeout */
    private int      timeout;

    public Class getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getBindingType() {
        return bindingType;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWarmUpWeight() {
        return warmUpWeight;
    }

    public void setWarmUpWeight(int warmUpWeight) {
        this.warmUpWeight = warmUpWeight;
    }

    public int getWarmUpTime() {
        return warmUpTime;
    }

    public void setWarmUpTime(int warmUpTime) {
        this.warmUpTime = warmUpTime;
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String getUserThreadPool() {
        return userThreadPool;
    }

    public void setUserThreadPool(String userThreadPool) {
        this.userThreadPool = userThreadPool;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
