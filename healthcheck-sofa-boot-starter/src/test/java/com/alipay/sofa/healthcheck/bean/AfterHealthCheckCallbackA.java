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

import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterHealthCheckCallback;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: AfterHealthCheckCallbackA.java, v 0.1 2018年03月11日 下午2:40 liangen Exp $
 */
public class AfterHealthCheckCallbackA implements SofaBootMiddlewareAfterHealthCheckCallback {

    private static boolean health = false;

    @Override
    public Health onHealthy(ApplicationContext applicationContext) {
        if (health) {
            return Health.up().withDetail("server", "server is ok").build();
        } else {
            return Health.down().withDetail("server", "server is bad").build();

        }
    }

    public static void setHealth(boolean health) {
        AfterHealthCheckCallbackA.health = health;
    }
}