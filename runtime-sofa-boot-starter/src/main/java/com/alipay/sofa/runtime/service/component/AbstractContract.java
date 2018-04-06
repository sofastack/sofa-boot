/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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
    protected Set<Binding>        bindings      = new HashSet<Binding>(2);
    /** unique id */
    protected String              uniqueId      = "";
    /** interface class type */
    protected Class<?>            interfaceType;
    /** interface mode */
    protected InterfaceMode       interfaceMode = InterfaceMode.spring;
    /** properties of constract */
    protected Map<String, String> property      = new HashMap<String, String>();

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
