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
package com.alipay.sofa.runtime.spi.binding;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.model.InterfaceMode;

import java.util.Map;
import java.util.Set;

/**
 * Contract Description Interface
 *
 * @author xuanbei 18/2/28
 */
public interface Contract {
    /**
     * get interface type
     *
     * @return interface type
     */
    Class<?> getInterfaceType();

    /**
     * has binding or not
     *
     * @return true or false
     */
    boolean hasBinding();

    /**
     * get supported binding set
     *
     * @return supported binding set
     */
    <T extends Binding> Set<T> getBindings();

    /**
     * get binding object by binding type
     *
     * @param bindingType binding type
     * @return binding object
     */
    Binding getBinding(BindingType bindingType);

    /**
     * get unique id, to support distribute service from same interface
     *
     * @return
     */
    String getUniqueId();

    /**
     * get property
     *
     * @param key property key
     * @return property value
     */
    String getProperty(String key);

    /***
     * get all properties
     *
     * @return all properties
     */
    Map<String, String> getProperty();

    /**
     * get interface mode
     *
     * @return interface mode
     */
    InterfaceMode getInterfaceMode();

    /**
     * add binding object, Contract supports multiple binding
     *
     * @param binding binding object
     */
    <T extends Binding> void addBinding(T binding);

    /**
     * add binding object, Contract supports multiple binding
     *
     * @param bindings binding objects
     */
    <T extends Binding> void addBinding(Set<T> bindings);
}
