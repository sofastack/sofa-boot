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
package com.alipay.sofa.healthcheck;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicatorNameFactory;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.alipay.sofa.boot.health.NonReadinessCheck;
import com.alipay.sofa.boot.util.BinaryOperators;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to process all implementations of {@link HealthIndicator}
 *
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthIndicatorProcessor {

    private static Logger                          logger           = HealthCheckLoggerFactory
                                                                        .getLogger(HealthIndicatorProcessor.class);

    private ObjectMapper                           objectMapper     = new ObjectMapper();

    private AtomicBoolean                          isInitiated      = new AtomicBoolean(false);

    private LinkedHashMap<String, HealthIndicator> healthIndicators = null;

    @Autowired
    private ApplicationContext                     applicationContext;

    private final static String                    REACTOR_CLASS    = "reactor.core.publisher.Mono";

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Map<String, HealthIndicator> beansOfType = applicationContext
                    .getBeansOfType(HealthIndicator.class);
            if (ClassUtils.isPresent(REACTOR_CLASS, null)) {
                applicationContext.getBeansOfType(ReactiveHealthIndicator.class).forEach(
                        (name, indicator) -> beansOfType.put(name, () -> indicator.health().block()));
            }
            healthIndicators = HealthCheckUtils.sortMapAccordingToValue(beansOfType,
                    applicationContext.getAutowireCapableBeanFactory());

            StringBuilder healthIndicatorInfo = new StringBuilder(512).append("Found ")
                    .append(healthIndicators.size()).append(" HealthIndicator implementation:")
                    .append(String.join(",", healthIndicators.keySet()));
            logger.info(healthIndicatorInfo.toString());
        }
    }

    /**
     * Provided for readiness check.
     *
     * @param healthMap
     * @return
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthIndicators, () -> "HealthIndicators must not be null.");

        logger.info("Begin SOFABoot HealthIndicator readiness check.");
        boolean result = healthIndicators.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof NonReadinessCheck))
                .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), healthMap))
                .reduce(true, BinaryOperators.andBoolean());
        if (result) {
            logger.info("SOFABoot HealthIndicator readiness check result: success.");
        } else {
            logger.error("SOFABoot HealthIndicator readiness check result: failed.");
        }
        return result;
    }

    public boolean doHealthCheck(String beanId, HealthIndicator healthIndicator,
                                 Map<String, Health> healthMap) {
        Assert.notNull(healthMap, () -> "HealthMap must not be null");

        boolean result;
        try {
            Health health = healthIndicator.health();
            Status status = health.getStatus();
            result = status.equals(Status.UP);
            if (result) {
                logger.info("HealthIndicator[{}] readiness check success.", beanId);
            } else {
                logger.error(
                        "HealthIndicator[{}] readiness check fail; the status is: {}; the detail is: {}.",
                        beanId, status, objectMapper.writeValueAsString(health.getDetails()));
            }
            healthMap.put(getKey(beanId), health);
        } catch (Exception e) {
            result = false;
            logger.error(
                    String.format("Error occurred while doing HealthIndicator[%s] readiness check.",
                            healthIndicator.getClass()),
                    e);
        }

        return result;
    }

    /**
     * refer to {@link HealthIndicatorNameFactory#apply(String)}
     */
    public String getKey(String name) {
        int index = name.toLowerCase().indexOf("healthindicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }
}