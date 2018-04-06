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

import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.w3c.dom.Element;
import com.alipay.sofa.runtime.api.binding.BindingType;

/**
 * @author xuanbei 18/2/28
 */
public interface Binding {
    /**
     * get binding URI
     *
     * @return binding URI
     */
    String getURI();

    /**
     * get binding name
     *
     * @return binding name
     */
    String getName();

    /**
     * get binding type
     *
     * @return binding type
     */
    BindingType getBindingType();

    /**
     * get binding property dom Element
     *
     * @return dom Element
     */
    Element getBindingPropertyContent();

    /**
     * get binding hashcode, prevent duplicate registration
     *
     * @return binding hashcode
     */
    int getBindingHashCode();

    /**
     * dump binding information
     *
     * @return binding information
     */
    String dump();

    /**
     * check binding health
     *
     * @return check result
     */
    HealthResult healthCheck();

    /**
     * set binding healthy
     *
     * @param healthy
     */
    void setHealthy(boolean healthy);

    /**
     * set binding destroyed state
     *
     * @param destroyed
     */
    void setDestroyed(boolean destroyed);

    /**
     * determine whether binding is destroyed
     *
     * @return true or false
     */
    boolean isDestroyed();
}
