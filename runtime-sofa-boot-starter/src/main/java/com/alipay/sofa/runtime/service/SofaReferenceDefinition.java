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
public class SofaReferenceDefinition {
    /** service interface */
    private Class    interfaceType;
    /** unique id **/
    private String   uniqueId;
    /** binding type, maybe jvm/bolt/rest */
    private String   bindingType;
    /** timeout */
    private int      timeout;
    /** retry times */
    private int      retries;
    /** address time out */
    private int      addressWaitTime;
    /** invoke type */
    private String   invokeType;
    /** filter beans */
    private String[] filters;
    /** direct url */
    private String   directUrl;
    /** call back handler,when invoke type is callback,it */
    private String   callBackHandler;
    /** registry for this consumer */
    private String   registry;
    /** version */
    private String   version;
    /** jvm first */
    private boolean  jvmFirst;

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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getAddressWaitTime() {
        return addressWaitTime;
    }

    public void setAddressWaitTime(int addressWaitTime) {
        this.addressWaitTime = addressWaitTime;
    }

    public String getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(String invokeType) {
        this.invokeType = invokeType;
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String getDirectUrl() {
        return directUrl;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    public String getCallBackHandler() {
        return callBackHandler;
    }

    public void setCallBackHandler(String callBackHandler) {
        this.callBackHandler = callBackHandler;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isJvmFirst() {
        return jvmFirst;
    }

    public void setJvmFirst(boolean jvmFirst) {
        this.jvmFirst = jvmFirst;
    }
}
