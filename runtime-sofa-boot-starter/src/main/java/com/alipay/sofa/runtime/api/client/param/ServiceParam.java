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
    private List<BindingParam> bindingParams = new ArrayList<BindingParam>();

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
