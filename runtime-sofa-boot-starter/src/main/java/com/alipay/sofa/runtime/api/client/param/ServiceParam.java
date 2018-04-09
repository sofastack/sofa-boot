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

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter class used when using {@link com.alipay.sofa.runtime.api.client.ServiceClient} to create a SOFA service.
 *
 * @author xuanbei 18/2/28
 */
public class ServiceParam {

    private String             uniqueId;
    private Class<?>           interfaceType;
    private Object             instance;
    private List<BindingParam> bindingParams = new ArrayList<>();

    /**
     * Get the unique id of the SOFA service to be created.
     *
     * @return The unique id of the SOFA service to be created.
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Set the unique id of the SOFA service to be created.
     *
     * @param uniqueId The unique id of the SOFA service to be created.
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Get the interface type of the SOFA service to be created.
     *
     * @return The interface type of the SOFA service to be created.
     */
    public Class<?> getInterfaceType() {
        return interfaceType;
    }

    /**
     * Set the interface type of the SOFA service to be created.
     *
     * @param interfaceType The interface type of the SOFA service to be created.
     */
    public void setInterfaceType(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * Get the instance of the SOFA service to be created.
     *
     * @return The instance of the SOFA service to be created.
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Set the instance of the SOFA service to be created.
     *
     * @param instance The instance of the SOFA service to be created.
     */
    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /**
     * Get the {@link BindingParam} list of the SOFA service to be created.
     *
     * @return The {@link BindingParam} list of the SOFA service to be created.
     */
    public List<BindingParam> getBindingParams() {
        return bindingParams;
    }

    /**
     * Set the {@link BindingParam} list of the SOFA service to be created.
     *
     * @param bindingParams The {@link BindingParam} list of the SOFA service to be created.
     */
    public void setBindingParams(List<BindingParam> bindingParams) {
        this.bindingParams = bindingParams;
    }

    /**
     * Add a {@link BindingParam} to the existing {@link BindingParam} list of the SOFA service to be created.
     *
     * @param bindingParam The {@link BindingParam} to add.
     */
    public void addBindingParam(BindingParam bindingParam) {
        bindingParams.add(bindingParam);
    }
}
