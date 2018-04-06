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
package com.alipay.sofa.healthcheck.startup;

import org.springframework.boot.actuate.health.Health;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: StartUpHealthCheckStatus.java, v 0.1 2018年02月02日 下午10:53 liangen Exp $
 */
public class StartUpHealthCheckStatus {

    private static boolean                     isOpen                          = false;

    private static boolean                     springContextStatus;

    private static boolean                     componentStatus;

    private static Map<String, Health>         componentDetail                 = new HashMap<String, Health>();

    private static boolean                     healthIndicatorStatus           = false;

    private static List<HealthIndicatorDetail> healthIndicatorDetails          = new ArrayList<HealthIndicatorDetail>();

    private static boolean                     afterHealthCheckCallbackStatus;

    private static Map<String, Health>         afterHealthCheckCallbackDetails = new HashMap<String, Health>();

    public static void setSpringContextStatus(boolean isHealth) {
        if (!isOpen) {
            return;
        }
        springContextStatus = isHealth;
    }

    public static void setComponentStatus(boolean componentStatus) {
        if (!isOpen) {
            return;
        }

        StartUpHealthCheckStatus.componentStatus = componentStatus;
    }

    public static void setHealthIndicatorStatus(boolean healthIndicatorStatus) {
        if (!isOpen) {
            return;
        }

        StartUpHealthCheckStatus.healthIndicatorStatus = healthIndicatorStatus;
    }

    public static void putComponentDetail(String name, Health health) {
        if (!isOpen) {
            return;
        }

        componentDetail.put(name, health);
    }

    public static void addHealthIndicatorDetail(HealthIndicatorDetail healthIndicatorDetail) {
        if (!isOpen) {
            return;
        }

        healthIndicatorDetails.add(healthIndicatorDetail);
    }

    public static void setAfterHealthCheckCallbackStatus(boolean afterHealthCheckCallbackStatus) {
        if (!isOpen) {
            return;
        }

        StartUpHealthCheckStatus.afterHealthCheckCallbackStatus = afterHealthCheckCallbackStatus;
    }

    public static void putAfterHealthCheckCallbackDetail(String name, Health health) {
        if (!isOpen) {
            return;
        }

        afterHealthCheckCallbackDetails.put(name, health);
    }

    public static boolean getSpringContextStatus() {
        return springContextStatus;
    }

    public static boolean getComponentStatus() {
        return componentStatus;
    }

    public static List<HealthIndicatorDetail> getHealthIndicatorDetails() {
        return healthIndicatorDetails;

    }

    public static boolean getHealthIndicatorStatus() {
        return healthIndicatorStatus;
    }

    public static boolean getAfterHealthCheckCallbackStatus() {
        return afterHealthCheckCallbackStatus;
    }

    public static Map<String, Health> getAfterHealthCheckCallbackDetails() {
        return afterHealthCheckCallbackDetails;
    }

    public static Map<String, Health> getComponentDetail() {
        return componentDetail;
    }

    public static void openStartStatu() {
        isOpen = true;
    }

    public static void closeStartStatu() {
        isOpen = false;
    }

    public static void clean() {
        isOpen = false;
        springContextStatus = false;
        componentStatus = false;
        healthIndicatorStatus = false;
        afterHealthCheckCallbackStatus = false;
        componentDetail.clear();
        healthIndicatorDetails.clear();
        afterHealthCheckCallbackDetails.clear();

    }

    public static class HealthIndicatorDetail {

        private final String name;

        private final Health health;

        public HealthIndicatorDetail(String name, Health health) {
            this.name = name;
            this.health = health;
        }

        /**
         * Getter method for property <tt>name</tt>.
         *
         * @return property value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Getter method for property <tt>health</tt>.
         *
         * @return property value of health
         */
        public Health getHealth() {
            return health;
        }
    }
}