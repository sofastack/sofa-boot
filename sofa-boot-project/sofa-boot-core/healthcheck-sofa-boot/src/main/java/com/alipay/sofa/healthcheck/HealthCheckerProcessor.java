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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.alipay.sofa.boot.health.NonReadinessCheck;
import com.alipay.sofa.boot.util.BinaryOperators;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to process all implementations of {@link HealthChecker}
 *
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public class HealthCheckerProcessor {

    private static Logger                        logger         = HealthCheckLoggerFactory
                                                                    .getLogger(HealthCheckerProcessor.class);

    private ObjectMapper                         objectMapper   = new ObjectMapper();

    private AtomicBoolean                        isInitiated    = new AtomicBoolean(false);

    @Autowired
    private ApplicationContext                   applicationContext;

    private LinkedHashMap<String, HealthChecker> healthCheckers = null;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Map<String, HealthChecker> beansOfType = applicationContext
                    .getBeansOfType(HealthChecker.class);
            healthCheckers = HealthCheckUtils.sortMapAccordingToValue(beansOfType,
                    applicationContext.getAutowireCapableBeanFactory());

            StringBuilder healthCheckInfo = new StringBuilder(512).append("Found ")
                    .append(healthCheckers.size()).append(" HealthChecker implementation:")
                    .append(String.join(",", healthCheckers.keySet()));
            logger.info(healthCheckInfo.toString());
        }
    }

    /**
     * Provided for liveness check.
     *
     * @param healthMap used to save the information of {@link HealthChecker}.
     * @return
     */
    public boolean livenessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthCheckers, () -> "HealthCheckers must not be null");

        logger.info("Begin SOFABoot HealthChecker liveness check.");
        boolean result = healthCheckers.entrySet().stream()
                .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), false, healthMap, false))
                .reduce(true, BinaryOperators.andBoolean());
        if (result) {
            logger.info("SOFABoot HealthChecker liveness check result: success.");
        } else {
            logger.error("SOFABoot HealthChecker liveness check result: failed.");
        }
        return result;
    }

    /**
     * Provided for readiness check.
     *
     * @param healthMap used to save the information of {@link HealthChecker}.
     * @return
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthCheckers, "HealthCheckers must not be null.");

        logger.info("Begin SOFABoot HealthChecker readiness check.");
        boolean result = healthCheckers.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof NonReadinessCheck))
                .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), true, healthMap, true))
                .reduce(true, BinaryOperators.andBoolean());
        if (result) {
            logger.info("SOFABoot HealthChecker readiness check result: success.");
        } else {
            logger.error("SOFABoot HealthChecker readiness check result: failed.");
        }
        return result;
    }

    /**
     * Process HealthChecker.
     *
     * @param healthChecker Specify {@link HealthChecker} to check.
     * @param isRetry Whether retry when check failed, true for readiness and false for liveness.
     * @param healthMap Used to save the information of {@link HealthChecker}.
     * @param isReadiness Mark whether invoked during readiness.
     * @return
     */
    private boolean doHealthCheck(String beanId, HealthChecker healthChecker, boolean isRetry,
                                  Map<String, Health> healthMap, boolean isReadiness) {
        Assert.notNull(healthMap, "HealthMap must not be null");

        Health health;
        boolean result;
        int retryCount = 0;
        String checkType = isReadiness ? "readiness" : "liveness";
        do {
            health = healthChecker.isHealthy();
            result = health.getStatus().equals(Status.UP);
            if (result) {
                logger.info("HealthChecker[{}] {} check success with {} retry.", beanId, checkType,
                    retryCount);
                break;
            } else {
                logger.info("HealthChecker[{}] {} check fail with {} retry.", beanId, checkType,
                    retryCount);
            }
            if (isRetry && retryCount < healthChecker.getRetryCount()) {
                try {
                    retryCount += 1;
                    TimeUnit.MILLISECONDS.sleep(healthChecker.getRetryTimeInterval());
                } catch (InterruptedException e) {
                    logger
                        .error(
                            String
                                .format(
                                    "Exception occurred while sleeping of %d retry HealthChecker[%s] %s check.",
                                    retryCount, beanId, checkType), e);
                }
            }
        } while (isRetry && retryCount < healthChecker.getRetryCount());

        healthMap.put(beanId, health);
        try {
            if (!result) {
                logger
                    .error(
                        "HealthChecker[{}] {} check fail with {} retry; fail details:{}; strict mode:{}",
                        beanId, checkType, retryCount,
                        objectMapper.writeValueAsString(health.getDetails()),
                        healthChecker.isStrictCheck());
            }
        } catch (JsonProcessingException ex) {
            logger.error(
                String.format("Error occurred while doing HealthChecker %s check.", checkType), ex);
        }
        return !healthChecker.isStrictCheck() || result;
    }
}