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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.startup.BaseStat;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * A processor used to process {@link HealthChecker}.
 *
 * @author liangen
 * @author qilong.zql
 * @author huzijie
 * @since 2.3.0
 */
public class HealthCheckerProcessor implements ApplicationContextAware {

    private static final Logger                  logger                       = SofaBootLoggerFactory
                                                                                  .getLogger(HealthCheckerProcessor.class);

    private final ObjectMapper                   objectMapper                 = new ObjectMapper();

    private final AtomicBoolean                  isInitiated                  = new AtomicBoolean(
                                                                                  false);

    private final List<BaseStat>                 healthCheckerStartupStatList = new CopyOnWriteArrayList<>();

    private ExecutorService                      healthCheckExecutor;

    private ApplicationContext                   applicationContext;

    private LinkedHashMap<String, HealthChecker> healthCheckers               = new LinkedHashMap<>();

    private int                                  globalTimeout;

    private Map<String, HealthCheckerConfig>     healthCheckerConfigs;

    private boolean                              parallelCheck;

    private long                                 parallelCheckTimeout;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Assert.notNull(healthCheckExecutor, () -> "HealthCheckExecutor must not be null");
            Map<String, HealthChecker> beansOfType = applicationContext
                    .getBeansOfType(HealthChecker.class);
            healthCheckers = HealthCheckComparatorSupport.sortMapAccordingToValue(beansOfType,
                    HealthCheckComparatorSupport.getComparatorToUse(applicationContext.getAutowireCapableBeanFactory()));

            String healthCheckInfo = "Found " + healthCheckers.size() + " HealthChecker implementation:" + String.join(",", healthCheckers.keySet());
            logger.info(healthCheckInfo);
        }
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
        if (isParallelCheck()) {
            CountDownLatch countDownLatch = new CountDownLatch(healthCheckers.size());
            AtomicBoolean parallelResult = new AtomicBoolean(true);
            healthCheckers.forEach((String key, HealthChecker value) -> healthCheckExecutor.execute(() -> {
                try {
                    if (!doHealthCheck(key, value, false, healthMap, true, false)) {
                        parallelResult.set(false);
                    }
                } catch (Throwable t) {
                    parallelResult.set(false);
                    logger.error(ErrorCode.convert("01-22004"), t);
                    healthMap.put(key, new Health.Builder().withException(t).status(Status.DOWN).build());
                } finally {
                    countDownLatch.countDown();
                }
            }));
            boolean finished = false;
            try {
                finished = countDownLatch.await(getParallelCheckTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error(ErrorCode.convert("01-22005"), e);
            }
            if (!finished) {
                parallelResult.set(false);
                healthMap.put("parallelCheck", new Health.Builder().withDetail("timeout", getParallelCheckTimeout())
                        .status(Status.UNKNOWN).build());
            }
            result = finished && parallelResult.get();
        } else {
            result = readinessHealthCheckers.entrySet().stream()
                    .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), true, healthMap, true, true))
                    .reduce(true, (a, b) -> a && b);
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
        String checkType = isReadiness ? "readiness" : "liveness";
        logger.info("HealthChecker [{}] {} check start.", beanId, checkType);

        // 定制配置
        healthChecker = wrapperHealthCheckerForCustomProperty(healthChecker);

        BaseStat baseStat = new BaseStat();
        baseStat.setName(healthChecker.getComponentName());
        baseStat.putAttribute("type", "HealthChecker");
        baseStat.setStartTime(System.currentTimeMillis());
        int retryCount = 0;
        int timeout = healthChecker.getTimeout();
        do {
            try {
                if (wait) {
                    Future<Health> future = healthCheckExecutor.submit(healthChecker::isHealthy);
                    health = future.get(timeout, TimeUnit.MILLISECONDS);
                } else {
                    health = healthChecker.isHealthy();
                }
            }  catch (TimeoutException e) {
                logger.error(
                        "Timeout occurred while doing HealthChecker[{}] {} check, the timeout value is: {}ms.",
                        beanId, checkType, timeout);
                health = new Health.Builder().withException(e).withDetail("timeout", timeout).status(Status.UNKNOWN).build();
            } catch (Throwable e) {
                logger.error(String.format(
                        "Exception occurred while wait the result of HealthChecker[%s] %s check.",
                        beanId, checkType), e);
                health = new Health.Builder().withException(e).status(Status.DOWN).build();
            }
            result = health.getStatus().equals(Status.UP);
            if (result) {
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

        baseStat.setEndTime(System.currentTimeMillis());
        healthCheckerStartupStatList.add(baseStat);

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

    private HealthChecker wrapperHealthCheckerForCustomProperty(HealthChecker healthChecker) {
        String componentName = healthChecker.getComponentName();
        Map<String, HealthCheckerConfig> healthCheckerConfigs = getHealthCheckerConfigs();

        int retryCount = Optional.ofNullable(healthCheckerConfigs)
                .map(k -> healthCheckerConfigs.get(componentName))
                .map(HealthCheckerConfig::getRetryCount)
                .orElse(healthChecker.getRetryCount());
        Assert.isTrue(retryCount >= 0, "HealthIndicator retryCount must no less than zero");

        long retryInterval = Optional.ofNullable(healthCheckerConfigs)
                .map(k -> healthCheckerConfigs.get(componentName))
                .map(HealthCheckerConfig::getRetryTimeInterval)
                .orElse(healthChecker.getRetryTimeInterval());
        Assert.isTrue(retryInterval >= 0, "HealthIndicator retryInterval must lager than zero");

        boolean strictCheck = Optional.ofNullable(healthCheckerConfigs)
                .map(k -> healthCheckerConfigs.get(componentName))
                .map(HealthCheckerConfig::getStrictCheck)
                .orElse(healthChecker.isStrictCheck());

        int timeout = Optional.ofNullable(healthCheckerConfigs)
                .map(k -> healthCheckerConfigs.get(componentName))
                .map(HealthCheckerConfig::getTimeout)
                .orElse(globalTimeout);
        Assert.isTrue(timeout > 0, "HealthIndicator timeout must lager than zero");

        return new WrapperHealthChecker(healthChecker, retryCount, retryInterval, strictCheck, timeout);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ExecutorService getHealthCheckExecutor() {
        return healthCheckExecutor;
    }

    public void setHealthCheckExecutor(ExecutorService healthCheckExecutor) {
        this.healthCheckExecutor = healthCheckExecutor;
    }

    public int getGlobalTimeout() {
        return globalTimeout;
    }

    public void setGlobalTimeout(int globalTimeout) {
        this.globalTimeout = globalTimeout;
    }

    public Map<String, HealthCheckerConfig> getHealthCheckerConfigs() {
        return healthCheckerConfigs;
    }

    public void setHealthCheckerConfigs(Map<String, HealthCheckerConfig> healthCheckerConfigs) {
        this.healthCheckerConfigs = healthCheckerConfigs;
    }

    public boolean isParallelCheck() {
        return parallelCheck;
    }

    public void setParallelCheck(boolean parallelCheck) {
        this.parallelCheck = parallelCheck;
    }

    public long getParallelCheckTimeout() {
        return parallelCheckTimeout;
    }

    public void setParallelCheckTimeout(long parallelCheckTimeout) {
        this.parallelCheckTimeout = parallelCheckTimeout;
    }

    public List<BaseStat> getHealthCheckerStartupStatList() {
        return healthCheckerStartupStatList;
    }

    public static class WrapperHealthChecker implements HealthChecker {

        private final HealthChecker healthCheckChecker;
        private final int           retryCount;
        private final long          retryTimeInterval;
        private final boolean       strictCheck;
        private final int           timeout;

        public WrapperHealthChecker(HealthChecker healthChecker, int retryCount,
                                    long retryTimeInterval, boolean strictCheck, int timeout) {
            this.healthCheckChecker = healthChecker;
            this.retryCount = retryCount;
            this.retryTimeInterval = retryTimeInterval;
            this.strictCheck = strictCheck;
            this.timeout = timeout;
        }

        @Override
        public Health isHealthy() {
            return healthCheckChecker.isHealthy();
        }

        @Override
        public String getComponentName() {
            return healthCheckChecker.getComponentName();
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
