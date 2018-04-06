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
package com.alipay.sofa.healthcheck.bean;

import com.alipay.sofa.healthcheck.core.HealthChecker;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author liangen
 * @version $Id: ReferenceABean.java, v 0.1 2018年03月10日 下午10:24 liangen Exp $
 */
public class ReferenceB implements HealthChecker {

    private static boolean isStrict;

    private static int     retryCount;

    @Override
    public Health isHealthy() {
        return Health.up().withDetail("network", "network is ok").build();
    }

    @Override
    public String getComponentName() {
        return "BBB";
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public long getRetryTimeInterval() {
        return 200;
    }

    @Override
    public boolean isStrictCheck() {
        return isStrict;
    }

    public static void setStrict(boolean strict) {
        isStrict = strict;
    }

    public static void setRetryCount(int retryCount) {
        ReferenceB.retryCount = retryCount;
    }

}