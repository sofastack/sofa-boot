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
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.core;

/**
 *
 * @author liangen
 * @version $Id: HealthCheckerType.java, v 0.1 2017年10月24日 上午10:31 liangen Exp $
 */
public enum HealthCheckerType {

    /**
     * Retry the checker.
     *
     * The interface will be retried when it fails and the number of retries
     * will be configured by the user 20 times.
     * If the maximum number of retries is also checked for success by default,
     * the check process and final check results are not affected, and only
     * the check log is printed. If the user open com.alipay.sofa.healthcheck.strict.com ponent parameters,
     * the will to the situation of the actual inspection as test results, affect the inspection process
     * and final inspection results. A reference service such as RPC is usually recommended as the
     * implementation of this interface, because the process will have a situation where the address is
     * not timely.
     */
    RETRY_ON_FAILED_CHECK("retry_on_failed_check"),

    /**
     * Un Retry the checker.
     *
     * If fail to check, won't retry.
     */
    UN_RETRY_ON_FAILED_CHECK("un_retry_on_failed_check");

    private final String typeName;

    HealthCheckerType(String typeName) {
        this.typeName = typeName;
    }
}