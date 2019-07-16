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
package com.alipay.sofa.rpc.boot.runtime.binding;

/**
 *
 * Rpc 方法级别参数
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class RpcBindingMethodInfo {

    private String  name;

    private String  type;

    private Integer timeout;

    private Integer retries;

    private String  callbackClass;

    private String  callbackRef;

    private Object  callbackHandler;

    /**
     * Getter method for property <tt>name</tt>.
     *
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for property <tt>name</tt>.
     *
     * @param name  value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
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
     * @param type  value to be assigned to property type
     */
    public void setType(String type) {
        this.type = type;
    }

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
     * @param timeout  value to be assigned to property timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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
     * @param retries  value to be assigned to property retries
     */
    public void setRetries(Integer retries) {
        this.retries = retries;
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
     * @param callbackClass  value to be assigned to property callbackClass
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
     * @param callbackRef  value to be assigned to property callbackRef
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
     * @param callbackHandler  value to be assigned to property callbackHandler
     */
    public void setCallbackHandler(Object callbackHandler) {
        this.callbackHandler = callbackHandler;
    }
}