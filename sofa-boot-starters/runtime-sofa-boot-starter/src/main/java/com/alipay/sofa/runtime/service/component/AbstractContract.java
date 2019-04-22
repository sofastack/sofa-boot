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
package com.alipay.sofa.runtime.service.component;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.Contract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * abstract contract implementation
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractContract implements Contract {
    /** associated binding */
    protected Set<Binding>        bindings      = new HashSet<>(2);
    /** unique id */
    protected String              uniqueId      = "";
    /** interface class type */
    protected Class<?>            interfaceType;
    /** interface mode */
    protected InterfaceMode       interfaceMode = InterfaceMode.spring;
    /** properties of contract */
    protected Map<String, String> property      = new HashMap<>();

    protected AbstractContract(String uniqueId, Class<?> interfaceType) {
        if (uniqueId == null) {
            this.uniqueId = "";
        } else {
            this.uniqueId = uniqueId;
        }
        this.interfaceType = interfaceType;
    }

    protected AbstractContract(String uniqueId, Class<?> interfaceType, InterfaceMode interfaceMode) {
        this(uniqueId, interfaceType);
        this.interfaceMode = interfaceMode;
    }

    protected AbstractContract(String uniqueId, Class<?> interfaceType,
                               InterfaceMode interfaceMode, Map<String, String> property) {
        this(uniqueId, interfaceType, interfaceMode);
        this.property = property;
    }

    public <T extends Binding> void addBinding(T binding) {
        this.bindings.add(binding);
    }

    public <T extends Binding> void addBinding(Set<T> bindings) {
        this.bindings.addAll(bindings);
    }

    public Binding getBinding(BindingType bindingType) {
        for (Binding binding : this.bindings) {
            if (binding != null && binding.getBindingType() == bindingType) {
                return binding;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Set<Binding> getBindings() {
        return this.bindings;
    }

    public boolean hasBinding() {
        return this.bindings.size() > 0;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public InterfaceMode getInterfaceMode() {
        return this.interfaceMode;
    }

    public Class<?> getInterfaceType() {
        return interfaceType;
    }

    @Override
    public String getProperty(String key) {
        if (property == null) {
            return null;
        }

        return property.get(key);
    }

    @Override
    public Map<String, String> getProperty() {
        return property;
    }
}
