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
package com.alipay.sofa.rpc.boot.runtime.param;

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingMethodInfo;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.server.UserThreadPool;
import com.alipay.sofa.runtime.api.client.param.BindingParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XML 元素和属性会被解析为 RpcBindingParam 。
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public abstract class RpcBindingParam implements BindingParam {

    /** global attr */
    protected Integer                    timeout;

    protected Integer                    addressWaitTime;

    protected Integer                    connectTimeout;

    protected Integer                    retries;

    protected String                     type;

    protected String                     callbackClass;

    protected String                     callbackRef;

    protected Object                     callbackHandler;

    protected Integer                    weight;

    protected Integer                    warmUpTime;

    protected Integer                    warmUpWeight;

    protected UserThreadPool             userThreadPool;

    protected String                     genericInterface;

    protected String                     loadBalancer;

    protected Boolean                    lazy;

    protected Boolean                    check;

    /** other */
    protected List<Filter>               filters;

    protected List<RpcBindingMethodInfo> methodInfos;

    protected String                     targetUrl;

    protected String                     serialization;

    protected Map<String, String>        parameters = new ConcurrentHashMap<String, String>();

    protected List<String>               registrys  = new ArrayList<String>();

    /**
     * Getter method for property <tt>timeout</tt>.
     *
     * @return property value of timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Setter method for property <tt>timeout</tt>.
     *
     * @param timeout value to be assigned to property timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Getter method for property <tt>addressWaitTime</tt>.
     *
     * @return property value of addressWaitTime
     */
    public Integer getAddressWaitTime() {
        return addressWaitTime;
    }

    /**
     * Setter method for property <tt>addressWaitTime</tt>.
     *
     * @param addressWaitTime value to be assigned to property addressWaitTime
     */
    public void setAddressWaitTime(Integer addressWaitTime) {
        this.addressWaitTime = addressWaitTime;
    }

    /**
     * Getter method for property <tt>connectTimeout</tt>.
     *
     * @return property value of connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Setter method for property <tt>connectTimeout</tt>.
     *
     * @param connectTimeout value to be assigned to property connectTimeout
     */
    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Getter method for property <tt>retries</tt>.
     *
     * @return property value of retries
     */
    public Integer getRetries() {
        return retries;
    }

    /**
     * Setter method for property <tt>retries</tt>.
     *
     * @param retries value to be assigned to property retries
     */
    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    /**
     * Getter method for property <tt>type</tt>.
     *
     * @return property value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter method for property <tt>type</tt>.
     *
     * @param type value to be assigned to property type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter method for property <tt>callbackClass</tt>.
     *
     * @return property value of callbackClass
     */
    public String getCallbackClass() {
        return callbackClass;
    }

    /**
     * Setter method for property <tt>callbackClass</tt>.
     *
     * @param callbackClass value to be assigned to property callbackClass
     */
    public void setCallbackClass(String callbackClass) {
        this.callbackClass = callbackClass;
    }

    /**
     * Getter method for property <tt>callbackRef</tt>.
     *
     * @return property value of callbackRef
     */
    public String getCallbackRef() {
        return callbackRef;
    }

    /**
     * Setter method for property <tt>callbackRef</tt>.
     *
     * @param callbackRef value to be assigned to property callbackRef
     */
    public void setCallbackRef(String callbackRef) {
        this.callbackRef = callbackRef;
    }

    /**
     * Getter method for property <tt>callbackHandler</tt>.
     *
     * @return property value of callbackHandler
     */
    public Object getCallbackHandler() {
        return callbackHandler;
    }

    /**
     * Setter method for property <tt>callbackHandler</tt>.
     *
     * @param callbackHandler value to be assigned to property callbackHandler
     */
    public void setCallbackHandler(Object callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    /**
     * Getter method for property <tt>weight</tt>.
     *
     * @return property value of weight
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * Setter method for property <tt>weight</tt>.
     *
     * @param weight value to be assigned to property weight
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * Getter method for property <tt>warmUpTime</tt>.
     *
     * @return property value of warmUpTime
     */
    public Integer getWarmUpTime() {
        return warmUpTime;
    }

    /**
     * Setter method for property <tt>warmUpTime</tt>.
     *
     * @param warmUpTime value to be assigned to property warmUpTime
     */
    public void setWarmUpTime(Integer warmUpTime) {
        this.warmUpTime = warmUpTime;
    }

    /**
     * Getter method for property <tt>warmUpWeight</tt>.
     *
     * @return property value of warmUpWeight
     */
    public Integer getWarmUpWeight() {
        return warmUpWeight;
    }

    /**
     * Setter method for property <tt>warmUpWeight</tt>.
     *
     * @param warmUpWeight value to be assigned to property warmUpWeight
     */
    public void setWarmUpWeight(Integer warmUpWeight) {
        this.warmUpWeight = warmUpWeight;
    }

    /**
     * Getter method for property <tt>filters</tt>.
     *
     * @return property value of filters
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Setter method for property <tt>filters</tt>.
     *
     * @param filters value to be assigned to property filters
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * Getter method for property <tt>methodInfos</tt>.
     *
     * @return property value of methodInfos
     */
    public List<RpcBindingMethodInfo> getMethodInfos() {
        return methodInfos;
    }

    /**
     * Setter method for property <tt>methodInfos</tt>.
     *
     * @param methodInfos value to be assigned to property methodInfos
     */
    public void setMethodInfos(List<RpcBindingMethodInfo> methodInfos) {
        this.methodInfos = methodInfos;
    }

    /**
     * Getter method for property <tt>targetUrl</tt>.
     *
     * @return property value of targetUrl
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Setter method for property <tt>targetUrl</tt>.
     *
     * @param targetUrl value to be assigned to property targetUrl
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Getter method for property <tt>userThreadPool</tt>.
     *
     * @return property value of userThreadPool
     */
    public UserThreadPool getUserThreadPool() {
        return userThreadPool;
    }

    /**
     * Setter method for property <tt>userThreadPool</tt>.
     *
     * @param userThreadPool value to be assigned to property userThreadPool
     */
    public void setUserThreadPool(UserThreadPool userThreadPool) {
        this.userThreadPool = userThreadPool;
    }

    /**
     * Getter method for property <tt>genericInterface</tt>.
     *
     * @return property value of genericInterface
     */
    public String getGenericInterface() {
        return genericInterface;
    }

    /**
     * Setter method for property <tt>genericInterface</tt>.
     *
     * @param genericInterface value to be assigned to property genericInterface
     */
    public void setGenericInterface(String genericInterface) {
        this.genericInterface = genericInterface;
    }

    /**
     * Getter method for property <tt>loadBalancer</tt>.
     *
     * @return property value of loadBalancer
     */
    public String getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * Setter method for property <tt>loadBalancer</tt>.
     *
     * @param loadBalancer  value to be assigned to property loadBalancer
     */
    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * Getter method for property <tt>lazy</tt>.
     *
     * @return property value of lazy
     */
    public Boolean getLazy() {
        return lazy;
    }

    /**
     * Setter method for property <tt>lazy</tt>.
     *
     * @param lazy  value to be assigned to property lazy
     */
    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * Getter method for property <tt>check</tt>.
     *
     * @return property value of check
     */
    public Boolean getCheck() {
        return check;
    }

    /**
     * Setter method for property <tt>check</tt>.
     *
     * @param check  value to be assigned to property check
     */
    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getRegistrys() {
        return registrys;
    }

    public void setRegistrys(List<String> registrys) {
        this.registrys = registrys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RpcBindingParam))
            return false;

        RpcBindingParam that = (RpcBindingParam) o;

        if (timeout != null ? !timeout.equals(that.timeout) : that.timeout != null)
            return false;
        if (addressWaitTime != null ? !addressWaitTime.equals(that.addressWaitTime) : that.addressWaitTime != null)
            return false;
        if (connectTimeout != null ? !connectTimeout.equals(that.connectTimeout) : that.connectTimeout != null)
            return false;
        if (retries != null ? !retries.equals(that.retries) : that.retries != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (callbackClass != null ? !callbackClass.equals(that.callbackClass) : that.callbackClass != null)
            return false;
        if (callbackRef != null ? !callbackRef.equals(that.callbackRef) : that.callbackRef != null)
            return false;
        if (callbackHandler != null ? !callbackHandler.equals(that.callbackHandler) : that.callbackHandler != null)
            return false;
        if (weight != null ? !weight.equals(that.weight) : that.weight != null)
            return false;
        if (warmUpTime != null ? !warmUpTime.equals(that.warmUpTime) : that.warmUpTime != null)
            return false;
        if (warmUpWeight != null ? !warmUpWeight.equals(that.warmUpWeight) : that.warmUpWeight != null)
            return false;
        if (userThreadPool != null ? !userThreadPool.equals(that.userThreadPool) : that.userThreadPool != null)
            return false;
        if (genericInterface != null ? !genericInterface.equals(that.genericInterface) : that.genericInterface != null)
            return false;
        if (loadBalancer != null ? !loadBalancer.equals(that.loadBalancer) : that.loadBalancer != null)
            return false;
        if (lazy != null ? !lazy.equals(that.lazy) : that.lazy != null)
            return false;
        if (check != null ? !check.equals(that.check) : that.check != null)
            return false;
        if (filters != null ? !filters.equals(that.filters) : that.filters != null)
            return false;
        if (methodInfos != null ? !methodInfos.equals(that.methodInfos) : that.methodInfos != null)
            return false;
        if (targetUrl != null ? !targetUrl.equals(that.targetUrl) : that.targetUrl != null)
            return false;
        if (serialization != null ? !serialization.equals(that.serialization) : that.serialization != null)
            return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null)
            return false;
        return registrys != null ? registrys.equals(that.registrys) : that.registrys == null;
    }

    @Override
    public int hashCode() {
        int result = timeout != null ? timeout.hashCode() : 0;
        result = 31 * result + (addressWaitTime != null ? addressWaitTime.hashCode() : 0);
        result = 31 * result + (connectTimeout != null ? connectTimeout.hashCode() : 0);
        result = 31 * result + (retries != null ? retries.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (callbackClass != null ? callbackClass.hashCode() : 0);
        result = 31 * result + (callbackRef != null ? callbackRef.hashCode() : 0);
        result = 31 * result + (callbackHandler != null ? callbackHandler.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (warmUpTime != null ? warmUpTime.hashCode() : 0);
        result = 31 * result + (warmUpWeight != null ? warmUpWeight.hashCode() : 0);
        result = 31 * result + (userThreadPool != null ? userThreadPool.hashCode() : 0);
        result = 31 * result + (genericInterface != null ? genericInterface.hashCode() : 0);
        result = 31 * result + (loadBalancer != null ? loadBalancer.hashCode() : 0);
        result = 31 * result + (lazy != null ? lazy.hashCode() : 0);
        result = 31 * result + (check != null ? check.hashCode() : 0);
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        result = 31 * result + (methodInfos != null ? methodInfos.hashCode() : 0);
        result = 31 * result + (targetUrl != null ? targetUrl.hashCode() : 0);
        result = 31 * result + (serialization != null ? serialization.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (registrys != null ? registrys.hashCode() : 0);
        return result;
    }
}