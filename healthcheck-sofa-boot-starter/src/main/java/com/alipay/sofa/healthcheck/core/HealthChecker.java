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
package com.alipay.sofa.healthcheck.core;

import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author liangen
 * @version $Id: HealthIndicatorCheckProcessor.java, v 0.1 2017年08月04日 liangen Exp $
 */
public interface HealthChecker {

    /**
     * Health check information.
     * @return
     */
    Health isHealthy();

    /**
     * Component name
     * @return
     */
    String getComponentName();

    /**
     * The number of retries after failure.
     * @return
     */
    int getRetryCount();

    /**
     * The time interval for each retry after failure.
     * @return
     */
    long getRetryTimeInterval();

    /**
     * Is it strictly checked?
     * If true, the final check result of isHealthy() is used as the result of the component's check.
     * If false, the final result of the component is considered to be healthy, but the exception log is printed.
     * @return
     */
    boolean isStrictCheck();
}
