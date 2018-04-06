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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.binding.AbstractBinding;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.w3c.dom.Element;

/**
 * JVM Service & Reference Binding
 *
 * @author xuanbei 18/2/28
 */
public class JvmBinding extends AbstractBinding {

    /**
     * binding type: JVM
     */
    public static BindingType JVM_BINDING_TYPE = new BindingType("jvm");

    public JvmBinding() {

    }

    /**
     * backup proxy
     */
    private Object backupProxy;

    public Object getBackupProxy() {
        return backupProxy;
    }

    public void setBackupProxy(Object backupProxy) {
        this.backupProxy = backupProxy;
    }

    /**
     * whether has backup proxy or not
     *
     * @return true or false
     */
    public boolean hasBackupProxy() {
        return this.backupProxy != null;
    }

    @Override
    public String getURI() {
        return null;
    }

    @Override
    public BindingType getBindingType() {
        return JVM_BINDING_TYPE;
    }

    @Override
    public Element getBindingPropertyContent() {
        return null;
    }

    @Override
    public int getBindingHashCode() {
        return JVM_BINDING_TYPE.hashCode();
    }

    @Override
    public HealthResult healthCheck() {
        HealthResult healthResult = new HealthResult(getName());
        healthResult.setHealthy(isHealthy);
        return healthResult;
    }
}
