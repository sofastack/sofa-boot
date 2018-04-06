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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.health.HealthResult;

import java.util.Map;

/**
 * Component info interface
 *
 * @author xuanbei 18/2/28
 */
public interface ComponentInfo extends Component {
    /**
     * get component type
     *
     * @return component type
     */
    ComponentType getType();

    /**
     * get component name
     *
     * @return component name
     */
    ComponentName getName();

    /**
     * get component implementation
     *
     * @return component implementation
     */
    Implementation getImplementation();

    /**
     * get sofa runtime context
     *
     * @return {@link SofaRuntimeContext}
     */
    SofaRuntimeContext getContext();

    /**
     * get all properties
     *
     * @return properties
     */
    Map<String, Property> getProperties();

    /**
     * get component status
     *
     * @return component status
     */
    ComponentStatus getState();

    /**
     * check component is activated or not
     *
     * @return true or false
     */
    boolean isActivated();

    /**
     * check component is resolved or not
     *
     * @return true or false
     */
    boolean isResolved();

    /**
     * get component information
     *
     * @return
     */
    String dump();

    /**
     * check component is health or not
     *
     * @return health result
     */
    HealthResult isHealthy();
}
