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

import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to process all implementations of {@link HealthChecker}
 *
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public class HealthCheckerProcessor implements ApplicationContextAware {

    private static Logger              logger             = SofaBootHealthCheckLoggerFactory
                                                              .getLogger(HealthCheckerProcessor.class);

    private ObjectMapper               objectMapper       = new ObjectMapper();

    private AtomicBoolean              isInitiated        = new AtomicBoolean(false);

    private ApplicationContext         applicationContext = null;

    private Map<String, HealthChecker> healthCheckers     = null;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, "Application must not be null");

            healthCheckers = applicationContext.getBeansOfType(HealthChecker.class);
            StringBuilder healthCheckInfo = new StringBuilder();
            healthCheckInfo.append("Found ").append(healthCheckers.size())
                .append(" HealthChecker implementation:");
            for (String beanId : healthCheckers.keySet()) {
                healthCheckInfo.append(beanId).append(",");
            }
            logger.info(healthCheckInfo.deleteCharAt(healthCheckInfo.length() - 1).toString());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext cxt) throws BeansException {
        applicationContext = cxt;
    }

    /**
     * Provided for liveness check.
     *
     * @param healthMap used to save the information of {@link HealthChecker}.
     * @return
     */
    public boolean livenessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthCheckers, "HealthCheckers must not be null");

        logger.info("Begin SOFABoot HealthChecker liveness check.");
        boolean result = true;
        for (String beanId : healthCheckers.keySet()) {
            result = doHealthCheck(beanId, healthCheckers.get(beanId), false, healthMap, false)
                     && result;
        }
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
        boolean result = true;
        for (String beanId : healthCheckers.keySet()) {
            result = doHealthCheck(beanId, healthCheckers.get(beanId), true, healthMap, true)
                     && result;
        }
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
        boolean result = false;
        int retryCount = 0;
        String checkType = isReadiness ? "readiness" : "liveness";
        do {
            health = healthChecker.isHealthy();
            if (health.getStatus().equals(Status.UP)) {
                result = true;
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
            if (!health.getStatus().equals(Status.UP)) {
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