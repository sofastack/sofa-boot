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
package com.alipay.sofa.runtime.spi.binding;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * Binding Adapter for SOFA service & reference
 * <p/>
 * <p>refer {@link BindingType} to get supported binding types.
 *
 * @author xuanbei 18/2/28
 */
public interface BindingAdapter<T extends Binding> {
    /**
     * pre out binding
     *
     * @param contract binding contract
     * @param binding binding object
     * @param target  binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void preOutBinding(Object contract, T binding, Object target,
                       SofaRuntimeContext sofaRuntimeContext);

    /**
     * out binding, out binding means provide service
     *
     * @param contract binding contract
     * @param binding binding object
     * @param target  binding target
     * @param sofaRuntimeContext sofa runtime context
     * @return binding result
     */
    Object outBinding(Object contract, T binding, Object target,
                      SofaRuntimeContext sofaRuntimeContext);

    /**
     * pre unout binding
     *
     * @param contract binding contract
     * @param binding binding object
     * @param target  binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void preUnoutBinding(Object contract, T binding, Object target,
                         SofaRuntimeContext sofaRuntimeContext);

    /**
     * post unout binding
     *
     * @param contract binding contract
     * @param binding binding object
     * @param target  binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void postUnoutBinding(Object contract, T binding, Object target,
                          SofaRuntimeContext sofaRuntimeContext);

    /**
     * in binding, in binding means reference service
     *
     * @param contract binding contract
     * @param binding binding object
     * @param sofaRuntimeContext sofa runtime context
     */
    Object inBinding(Object contract, T binding, SofaRuntimeContext sofaRuntimeContext);

    /**
     * undo in binding
     *
     * @param contract
     * @param binding
     * @param sofaRuntimeContext
     */
    void unInBinding(Object contract, T binding, SofaRuntimeContext sofaRuntimeContext);

    /**
     * get binding type
     *
     * @return binding type
     */
    BindingType getBindingType();

    /**
     * get binding class
     *
     * @return binding class
     */
    Class<T> getBindingClass();
}
