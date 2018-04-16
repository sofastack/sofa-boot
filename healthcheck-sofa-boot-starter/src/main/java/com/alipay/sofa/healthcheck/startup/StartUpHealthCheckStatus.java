/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private static Map<String, Health>         componentDetail                 = new HashMap<>();

    private static boolean                     healthIndicatorStatus           = false;

    private static List<HealthIndicatorDetail> healthIndicatorDetails          = new ArrayList<>();

    private static boolean                     afterHealthCheckCallbackStatus;

    private static Map<String, Health>         afterHealthCheckCallbackDetails = new HashMap<>();

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

    public static void openStartStatus() {
        isOpen = true;
    }

    public static void closeStartStatus() {
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