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
    private boolean      localFirst;
    private boolean      jvmService;

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
     * Get the local-first parameter of the SOFA reference.
     *
     * @return The local-first parameter of the SOFA reference.
     */
    public boolean isLocalFirst() {
        return localFirst;
    }

    /**
     * Set whether the SOFA reference should invoke the SOFA service in the same JVM when available. This value is
     * default to true.
     *
     * @param localFirst Set whether the SOFA reference should invoke the SOFA service in the same JVM when available.
     */
    public void setLocalFirst(boolean localFirst) {
        this.localFirst = localFirst;
    }

    /**
     * Get the jvm-service parameter of the SOFA reference.
     *
     * @return The jvm-service parameter of the SOFA reference.
     */
    public boolean isJvmService() {
        return jvmService;
    }

    /**
     * Set whether expose the SOFA reference to be created to a JVM SOFA service. This parameter only works when
     * creating a RPC SOFA reference. This parameter is default to false.
     *
     * @param jvmService The jvm-service parameter of the SOFA reference.
     */
    public void setJvmService(boolean jvmService) {
        this.jvmService = jvmService;
    }
}
