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

import com.alipay.sofa.healthcheck.service.SofaBootComponentHealthCheckInfo;
import com.alipay.sofa.healthcheck.service.SpringContextHealthCheckInfo;
import com.alipay.sofa.healthcheck.startup.SofaBootApplicationAfterHealthCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterHealthCheckCallback;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: HealthCheckManager.java, v 0.1 2017年10月16日 上午10:40 liangen Exp $
 */
public class HealthCheckManager {
    private static ApplicationContext applicationContext;

    public static void init(ApplicationContext applicationContext) {
        HealthCheckManager.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static List<HealthChecker> getHealthCheckers() {
        List<HealthChecker> healthCheckers = new ArrayList<HealthChecker>();

        Map<String, HealthChecker> stringToHealthChecker = applicationContext.getBeansOfType(HealthChecker.class);
        if (!CollectionUtils.isEmpty(stringToHealthChecker)) {
            for (HealthChecker healthChecker : stringToHealthChecker.values()) {
                healthCheckers.add(healthChecker);
            }
        }

        return healthCheckers;
    }

    public static List<HealthIndicator> getHealthIndicator() {
        List<HealthIndicator> healthIndicators = new ArrayList<HealthIndicator>();

        Map<String, HealthIndicator> stringToHealthIndicator = applicationContext.getBeansOfType(HealthIndicator.class);
        if (!CollectionUtils.isEmpty(stringToHealthIndicator)) {
            for (HealthIndicator healthIndicator : stringToHealthIndicator.values()) {
                if (!(healthIndicator instanceof SofaBootComponentHealthCheckInfo) &&
                    !(healthIndicator instanceof SpringContextHealthCheckInfo)) { //排除掉SofaBootComponentHealthCheckInfo 和 SpringContextHealthCheckInfo
                    healthIndicators.add(healthIndicator);
                }
            }
        }

        return healthIndicators;
    }

    public static List<SofaBootApplicationAfterHealthCheckCallback> getApplicationAfterHealthCheckCallbacks() {
        List<SofaBootApplicationAfterHealthCheckCallback> afterHealthCheckCallbacks = null;

        Map<String, SofaBootApplicationAfterHealthCheckCallback> stringToCallback = applicationContext
            .getBeansOfType(SofaBootApplicationAfterHealthCheckCallback.class);
        if (!CollectionUtils.isEmpty(stringToCallback)) {
            afterHealthCheckCallbacks = new ArrayList<SofaBootApplicationAfterHealthCheckCallback>(
                stringToCallback.values());
        } else {
            afterHealthCheckCallbacks = Collections.EMPTY_LIST;
        }

        return afterHealthCheckCallbacks;

    }

    public static List<SofaBootMiddlewareAfterHealthCheckCallback> getMiddlewareAfterHealthCheckCallbacks() {
        List<SofaBootMiddlewareAfterHealthCheckCallback> afterHealthCheckCallbacks = null;

        Map<String, SofaBootMiddlewareAfterHealthCheckCallback> stringToCallback = applicationContext
            .getBeansOfType(SofaBootMiddlewareAfterHealthCheckCallback.class);
        if (!CollectionUtils.isEmpty(stringToCallback)) {
            afterHealthCheckCallbacks = new ArrayList<SofaBootMiddlewareAfterHealthCheckCallback>(
                stringToCallback.values());
        } else {
            afterHealthCheckCallbacks = Collections.EMPTY_LIST;
        }

        return afterHealthCheckCallbacks;
    }

    public static void publishEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    public static boolean springContextCheck() {
        boolean isHealth = false;

        if (applicationContext == null) {

            isHealth = false;

        } else if (applicationContext instanceof AbstractApplicationContext) {

            isHealth = ((AbstractApplicationContext) applicationContext).isActive();

        } else {

            isHealth = true;
        }

        return isHealth;
    }

}