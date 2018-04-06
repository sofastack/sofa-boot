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
package com.alipay.sofa.runtime.service.component.impl;

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.AbstractContract;
import com.alipay.sofa.runtime.service.component.Service;

import java.util.Map;

/**
 * Service Implementation
 *
 * @author xuanbei 18/3/1
 */
public class ServiceImpl extends AbstractContract implements Service {

    private Object target;

    public ServiceImpl(String uniqueId, Class<?> interfaceType, Object target) {
        super(uniqueId, interfaceType);
        this.target = target;
    }

    public ServiceImpl(String uniqueId, Class<?> interfaceType, InterfaceMode interfaceMode,
                       Object target) {
        super(uniqueId, interfaceType, interfaceMode);
        this.target = target;
    }

    public ServiceImpl(String uniqueId, Class<?> interfaceType, InterfaceMode interfaceMode,
                       Object target, Map<String, String> property) {
        super(uniqueId, interfaceType, interfaceMode, property);
        this.target = target;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return this.getInterfaceType().getName();
    }
}
