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
package com.alipay.sofa.runtime.api.client.param;

/**
 * Parameter class used when using {@link com.alipay.sofa.runtime.api.client.ReferenceClient} to create a SOFA
 * reference.
 *
 * @author xuanbei 18/2/28
 */
public class ReferenceParam<T> {

    private String       uniqueId = "";
    private Class<T>     interfaceType;
    private BindingParam bindingParam;
    private boolean      jvmFirst;

    /**
     * Get the unique id of the SOFA reference to be created.
     *
     * @return The unique id of the SOFA reference to be created.
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Set the unique id of the SOFA reference to be created.
     *
     * @param uniqueId The unique id of the SOFA reference.
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Get the interface type of the SOFA reference to be created.
     *
     * @return The interface type of the SOFA reference to be created.
     */
    public Class<T> getInterfaceType() {
        return interfaceType;
    }

    /**
     * The interface type of the SOFA reference to be created.
     *
     * @param interfaceType The interface type of the SOFA reference.
     */
    public void setInterfaceType(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * The {@link BindingParam} of the SOFA reference to be created.
     *
     * @return The {@link BindingParam} of the SOFA reference to be created.
     */
    public BindingParam getBindingParam() {
        return bindingParam;
    }

    /**
     * Set the {@link BindingParam} of the SOFA reference to be created. When creating a JVM SOFA reference, you should
     * not set {@link BindingParam}; When creating a RPC SOFA reference, you must set {@link BindingParam}.
     *
     * @param bindingParam The {@link BindingParam} of the SOFA reference.
     */
    public void setBindingParam(BindingParam bindingParam) {
        this.bindingParam = bindingParam;
    }

    /**
     * Get the jvm-first parameter of the SOFA reference.
     *
     * @return The jvm-first parameter of the SOFA reference.
     */
    public boolean isJvmFirst() {
        return jvmFirst;
    }

    /**
     * Set whether the SOFA reference should invoke the SOFA service in the same JVM when available. This value is
     * default to true.
     *
     * @param jvmFirst Set whether the SOFA reference should invoke the SOFA service in the same JVM when available.
     */
    public void setJvmFirst(boolean jvmFirst) {
        this.jvmFirst = jvmFirst;
    }

    /**
     * Get the local-first parameter of the SOFA reference.
     * Deprecated, you should use ${@link ReferenceParam#isJvmFirst()}.
     *
     * @return The local-first parameter of the SOFA reference.
     */
    @Deprecated
    public boolean isLocalFirst() {
        return jvmFirst;
    }

    /**
     * Set whether the SOFA reference should invoke the SOFA service in the same JVM when available. This value is
     * default to true.
     * Deprecated, you should use  ${@link ReferenceParam#setJvmFirst(boolean)}.
     *
     * @param localFirst Set whether the SOFA reference should invoke the SOFA service in the same JVM when available.
     */
    @Deprecated
    public void setLocalFirst(boolean localFirst) {
        this.jvmFirst = localFirst;
    }

    /**
     * Get the jvm-service parameter of the SOFA reference.
     * Deprecated, will do nothing, please don't use.
     *
     * @return The jvm-service parameter of the SOFA reference.
     */
    @Deprecated
    public boolean isJvmService() {
        return false;
    }

    /**
     * Set whether expose the SOFA reference to be created to a JVM SOFA service. This parameter only works when
     * creating a RPC SOFA reference. This parameter is default to false.
     * Deprecated, will do nothing, please don't use.
     *
     * @param jvmService The jvm-service parameter of the SOFA reference.
     */
    @Deprecated
    public void setJvmService(boolean jvmService) {
    }
}
