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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;

/**
 * SOFA Component Interface.
 * <p/>
 * <p> Component Lifecycle:
 * <pre>
 *                  [UNREGISTERED]
 *                      |   ▲
 *           register   │   │   unregister
 *                      |   |
 *                   [REGISTERED]
 *                      |   ▲
 *           resolve    │   │   unresolve
 *                      |   |
 *                    [RESOLVED]
 *                      |   ▲
 *                 ┌────┘   └────┐
 *                 │             │
 *        activate |             ▲ deactivate
 *                 │             │
 *                 └───┐    ┌────┘
 *                          |
 *                   [ACTIVATED]
 * </pre>
 *
 * @author xuanbei 18/2/28
 */
public interface Component {

    /**
     * register component
     */
    void register();

    /**
     * unregister component
     */
    void unregister() throws ServiceRuntimeException;

    /**
     * resolve component
     *
     * @return success or not
     */
    boolean resolve();

    /**
     * unresolve component
     */
    void unresolve() throws ServiceRuntimeException;

    /**
     * activate component
     *
     * @throws ServiceRuntimeException
     */
    void activate() throws ServiceRuntimeException;

    /**
     * deactivate component
     *
     * @throws ServiceRuntimeException
     */
    void deactivate() throws ServiceRuntimeException;

    /**
     * create an exception to descript error
     *
     * @throws ServiceRuntimeException
     */
    void exception(Exception e) throws ServiceRuntimeException;
}
