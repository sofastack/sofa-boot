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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.boot.health.HealthCheckerConfig;
import com.alipay.sofa.boot.health.NonReadinessCheck;
import com.alipay.sofa.boot.util.BinaryOperators;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Used to process all implementations of {@link HealthChecker}
 *
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public class HealthCheckerProcessor implements ApplicationContextAware {

    private static final Logger                  logger         = HealthCheckLoggerFactory.DEFAULT_LOG;

    private final ObjectMapper                   objectMapper   = new ObjectMapper();

    private final AtomicBoolean                  isInitiated    = new AtomicBoolean(false);

    private ApplicationContext                   applicationContext;

    private LinkedHashMap<String, HealthChecker> healthCheckers = null;

    private int                                  defaultTimeout;

    private final HealthCheckProperties          healthCheckProperties;

    private final HealthCheckExecutor            healthCheckExecutor;

    private Map<String, HealthCheckerConfig>     healthCheckersConfig;

    public HealthCheckerProcessor(HealthCheckProperties healthCheckProperties,
                                  HealthCheckExecutor healthCheckExecutor) {
        this.healthCheckProperties = healthCheckProperties;
        this.healthCheckExecutor = healthCheckExecutor;
        this.healthCheckersConfig = healthCheckProperties.getHealthCheckerConfigs();
    }

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
     * @return whether liveness health check passes or not
     */
    public boolean livenessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthCheckers, () -> "HealthCheckers must not be null");

        logger.info("Begin SOFABoot HealthChecker liveness check.");
        String checkComponentNames = healthCheckers.values().stream()
                .map(HealthChecker::getComponentName).collect(Collectors.joining(","));
        logger.info("SOFABoot HealthChecker liveness check {} item: {}.",
                healthCheckers.size(), checkComponentNames);
        boolean result;
        if (healthCheckProperties.isHealthCheckParallelEnable()) {
            CountDownLatch countDownLatch = new CountDownLatch(healthCheckers.size());
            AtomicBoolean parallelResult = new AtomicBoolean(true);
            healthCheckers.forEach((key, value) -> healthCheckExecutor.executeTask(() -> {
                try {
                    if (!doHealthCheck(key, value, false, healthMap, false, false)) {
                        parallelResult.set(false);
                    }
                } catch (Throwable t) {
                    logger.error(ErrorCode.convert("01-22001"), t);
                } finally {
                    countDownLatch.countDown();
                }
            }));
            boolean finished = false;
            try {
                finished = countDownLatch.await(healthCheckProperties.getHealthCheckParallelTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error(ErrorCode.convert("01-22002"), e);
            }
            result = finished && parallelResult.get();
        } else {
            result = healthCheckers.entrySet().stream()
                    .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), false, healthMap, false, true))
                    .reduce(true, BinaryOperators.andBoolean());
        }
        if (result) {
            logger.info("SOFABoot HealthChecker liveness check result: success.");
        } else {
            logger.error(ErrorCode.convert("01-22000"));
        }
        return result;
    }

    /**
     * Provided for readiness check.
     *
     * @param healthMap used to save the information of {@link HealthChecker}.
     * @return whether readiness health check passes or not
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthCheckers, "HealthCheckers must not be null.");

        logger.info("Begin SOFABoot HealthChecker readiness check.");
        Map<String, HealthChecker> readinessHealthCheckers = healthCheckers.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof NonReadinessCheck))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String checkComponentNames = readinessHealthCheckers.values().stream()
                .map(HealthChecker::getComponentName).collect(Collectors.joining(","));
        logger.info("SOFABoot HealthChecker readiness check {} item: {}.",
                healthCheckers.size(), checkComponentNames);
        boolean result;
        if (healthCheckProperties.isHealthCheckParallelEnable()) {
            CountDownLatch countDownLatch = new CountDownLatch(healthCheckers.size());
            AtomicBoolean parallelResult = new AtomicBoolean(true);
            healthCheckers.forEach((String key, HealthChecker value) -> healthCheckExecutor.executeTask(() -> {
                try {
                    if (!doHealthCheck(key, value, false, healthMap, true, false)) {
                        parallelResult.set(false);
                    }
                } catch (Throwable t) {
                    logger.error(ErrorCode.convert("01-22004"), t);
                } finally {
                    countDownLatch.countDown();
                }
            }));
            boolean finished = false;
            try {
                finished = countDownLatch.await(healthCheckProperties.getHealthCheckParallelTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error(ErrorCode.convert("01-22005"), e);
            }
            if (!finished) {
                healthMap.put(SofaBootConstants.SOFABOOT_HEALTH_CHECK_TIMEOUT_KEY, Health.unknown().withDetail(SofaBootConstants.SOFABOOT_HEALTH_CHECK_TIMEOUT_KEY,SofaBootConstants.SOFABOOT_HEALTH_CHECK_TIMEOUT_MSG).build());
            }
            result = finished && parallelResult.get();
        } else {
            result = readinessHealthCheckers.entrySet().stream()
                    .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), true, healthMap, true, true))
                    .reduce(true, BinaryOperators.andBoolean());
        }
        if (result) {
            logger.info("SOFABoot HealthChecker readiness check result: success.");
        } else {
            logger.error(ErrorCode.convert("01-23000"));
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
     * @param wait Whether wait for result
     * @return health check passes or not
     */
    private boolean doHealthCheck(String beanId, HealthChecker healthChecker, boolean isRetry,
                                      Map<String, Health> healthMap, boolean isReadiness, boolean wait) {
        Assert.notNull(healthMap, "HealthMap must not be null");

        Health health;
        boolean result;
        int retryCount = 0;
        String checkType = isReadiness ? "readiness" : "liveness";
        logger.info("HealthChecker[{}] {} check start.", beanId, checkType);

        // load custom healthChecker config
        healthChecker = wrapperHealthCheckerForCustomConfig(healthChecker);

        int timeout = healthChecker.getTimeout();
        if (timeout <= 0) {
            timeout = defaultTimeout;
        }
        do {
            try {
                if (wait) {
                    Future<Health> future = healthCheckExecutor.submitTask(healthChecker::isHealthy);
                    health = future.get(timeout, TimeUnit.MILLISECONDS);
                } else {
                    health = healthChecker.isHealthy();
                }
            }  catch (TimeoutException e) {
                logger.error(
                        "Timeout occurred while doing HealthChecker[{}] {} check, the timeout value is: {}ms.",
                        beanId, checkType, timeout);
                health = new Health.Builder().withException(e).status(Status.UNKNOWN).build();
            } catch (Throwable e) {
                logger.error(String.format(
                        "Exception occurred while wait the result of HealthChecker[%s] %s check.",
                        beanId, checkType), e);
                health = new Health.Builder().withException(e).status(Status.DOWN).build();
            }
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
                        .error(ErrorCode.convert("01-23002", retryCount, beanId,
                            checkType), e);
                }
            }
        } while (isRetry && retryCount < healthChecker.getRetryCount());

        healthMap.put(beanId, health);
        try {
            if (!result) {
                if (healthChecker.isStrictCheck()) {
                    logger.error(ErrorCode.convert("01-23001", beanId, checkType, retryCount,
                            objectMapper.writeValueAsString(health.getDetails()),
                            healthChecker.isStrictCheck()));
                } else {
                    logger.warn(ErrorCode.convert("01-23001", beanId, checkType, retryCount,
                            objectMapper.writeValueAsString(health.getDetails()),
                            healthChecker.isStrictCheck()));
                }
            }
        } catch (JsonProcessingException ex) {
            logger.error(ErrorCode.convert("01-23003", checkType), ex);
        }
        return !healthChecker.isStrictCheck() || result;
    }

    public HealthChecker wrapperHealthCheckerForCustomConfig(HealthChecker healthChecker) {
        String componentName = healthChecker.getComponentName();
        Map<String, HealthCheckerConfig> healthCheckerConfigs = healthCheckProperties.getHealthCheckerConfigs();

        int retryCount = Optional.ofNullable(healthCheckerConfigs)
                .map(v -> v.get(componentName))
                .map(HealthCheckerConfig::getRetryCount)
                .orElse(healthChecker.getRetryCount());
        Assert.isTrue(retryCount >= 0, "HealthChecker retry count must be no less than zero");

        long retryTimeInterval = Optional.ofNullable(healthCheckerConfigs)
                .map(v -> v.get(componentName))
                .map(HealthCheckerConfig::getRetryTimeInterval)
                .orElse(healthChecker.getRetryTimeInterval());
        Assert.isTrue(retryTimeInterval >= 0, "HealthChecker retry time interval must be no less than zero");

        boolean strictCheck = Optional.ofNullable(healthCheckerConfigs)
                .map(v -> v.get(componentName))
                .map(HealthCheckerConfig::getStrictCheck)
                .orElse(healthChecker.isStrictCheck());

        int timeout = Optional.ofNullable(healthCheckerConfigs)
                .map(v -> v.get(componentName))
                .map(HealthCheckerConfig::getTimeout)
                .orElse(healthChecker.getTimeout());
        Assert.isTrue(timeout >= 0, "HealthChecker timeout must be greater than zero");

        return new WrapperHealthChecker(healthChecker, retryCount, retryTimeInterval, strictCheck, timeout);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.defaultTimeout = Integer.parseInt(applicationContext.getEnvironment().getProperty(
            SofaBootConstants.SOFABOOT_HEALTH_CHECK_DEFAULT_TIMEOUT,
            String.valueOf(SofaBootConstants.SOFABOOT_HEALTH_CHECK_DEFAULT_TIMEOUT_VALUE)));
    }

    public static class WrapperHealthChecker implements HealthChecker {
        private final HealthChecker healthChecker;
        private final int           retryCount;
        private final long          retryTimeInterval;
        private final boolean       strictCheck;
        private final int           timeout;

        public WrapperHealthChecker(HealthChecker healthChecker, int retryCount,
                                    long retryTimeInterval, boolean strictCheck, int timeout) {
            this.healthChecker = healthChecker;
            this.retryCount = retryCount;
            this.retryTimeInterval = retryTimeInterval;
            this.strictCheck = strictCheck;
            this.timeout = timeout;
        }

        @Override
        public Health isHealthy() {
            return this.healthChecker.isHealthy();
        }

        @Override
        public String getComponentName() {
            return this.healthChecker.getComponentName();
        }

        @Override
        public int getRetryCount() {
            return this.retryCount;
        }

        @Override
        public long getRetryTimeInterval() {
            return this.retryTimeInterval;
        }

        @Override
        public boolean isStrictCheck() {
            return this.strictCheck;
        }

        @Override
        public int getTimeout() {
            return this.timeout;
        }
    }
}
