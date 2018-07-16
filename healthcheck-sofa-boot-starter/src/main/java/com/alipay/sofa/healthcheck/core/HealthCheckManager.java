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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.service.SofaBootComponentHealthCheckInfo;
import com.alipay.sofa.healthcheck.service.SpringContextHealthCheckInfo;
import com.alipay.sofa.healthcheck.startup.SofaBootAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterReadinessCheckCallback;
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
@SuppressWarnings("unchecked")
public class HealthCheckManager {
    private static ApplicationContext applicationContext;

    public static void init(ApplicationContext applicationContext) {
        HealthCheckManager.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static List<HealthChecker> getHealthCheckers() {
        List<HealthChecker> healthCheckers = new ArrayList<>();

        Map<String, HealthChecker> stringToHealthChecker = applicationContext
            .getBeansOfType(HealthChecker.class);
        if (!CollectionUtils.isEmpty(stringToHealthChecker)) {
            healthCheckers.addAll(stringToHealthChecker.values());
        }

        return healthCheckers;
    }

    public static List<HealthIndicator> getHealthIndicator() {
        List<HealthIndicator> healthIndicators = new ArrayList<>();

        Map<String, HealthIndicator> stringToHealthIndicator = applicationContext
            .getBeansOfType(HealthIndicator.class);
        if (!CollectionUtils.isEmpty(stringToHealthIndicator)) {
            for (HealthIndicator healthIndicator : stringToHealthIndicator.values()) {
                if (!(healthIndicator instanceof SofaBootComponentHealthCheckInfo)
                    && !(healthIndicator instanceof SpringContextHealthCheckInfo)) { //排除掉SofaBootComponentHealthCheckInfo 和 SpringContextHealthCheckInfo
                    healthIndicators.add(healthIndicator);
                }
            }
        }

        return healthIndicators;
    }

    public static List<SofaBootAfterReadinessCheckCallback> getApplicationAfterHealthCheckCallbacks() {
        List<SofaBootAfterReadinessCheckCallback> afterReadinessCheckCallbacks = null;

        Map<String, SofaBootAfterReadinessCheckCallback> stringToAfterReadinessCheckCallback = applicationContext
            .getBeansOfType(SofaBootAfterReadinessCheckCallback.class);
        if (!CollectionUtils.isEmpty(stringToAfterReadinessCheckCallback)) {
            afterReadinessCheckCallbacks = new ArrayList<>(
                stringToAfterReadinessCheckCallback.values());
        } else {
            afterReadinessCheckCallbacks = Collections.EMPTY_LIST;
        }

        return afterReadinessCheckCallbacks;

    }

    public static List<SofaBootMiddlewareAfterReadinessCheckCallback> getMiddlewareAfterHealthCheckCallbacks() {
        List<SofaBootMiddlewareAfterReadinessCheckCallback> middlewareAfterReadinessCheckCallbacks = null;

        Map<String, SofaBootMiddlewareAfterReadinessCheckCallback> stringToMiddlewareAfterReadinessCheckCallback = applicationContext
            .getBeansOfType(SofaBootMiddlewareAfterReadinessCheckCallback.class);
        if (!CollectionUtils.isEmpty(stringToMiddlewareAfterReadinessCheckCallback)) {
            middlewareAfterReadinessCheckCallbacks = new ArrayList<>(
                stringToMiddlewareAfterReadinessCheckCallback.values());
        } else {
            middlewareAfterReadinessCheckCallbacks = Collections.EMPTY_LIST;
        }

        return middlewareAfterReadinessCheckCallbacks;
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