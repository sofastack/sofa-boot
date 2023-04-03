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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributorNameFactory;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * A processor used to process {@link HealthIndicator}.
 *
 * @author liangen
 * @author qilong.zql
 * @author huzijie
 * @version 2.3.0
 */
public class HealthIndicatorProcessor implements ApplicationContextAware {

    private static final Logger                    logger                         = SofaBootLoggerFactory
                                                                                      .getLogger(HealthIndicatorProcessor.class);

    private static final List<String>              DEFAULT_EXCLUDE_INDICATORS     = Arrays
                                                                                      .asList(
                                                                                          "com.alipay.sofa.boot.actuator.health.NonReadinessCheck",
                                                                                          "org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator",
                                                                                          "org.springframework.boot.actuate.availability.LivenessStateHealthIndicator");

    private static final String                    REACTOR_CLASS                  = "reactor.core.publisher.Mono";

    private static final boolean                   REACTOR_CLASS_EXIST;

    private final List<BaseStat>                   healthIndicatorStartupStatList = new CopyOnWriteArrayList<>();

    private final ObjectMapper                     objectMapper                   = new ObjectMapper();

    private final AtomicBoolean                    isInitiated                    = new AtomicBoolean(
                                                                                      false);

    private LinkedHashMap<String, HealthIndicator> healthIndicators               = null;

    private ApplicationContext                     applicationContext;

    private ExecutorService                        healthCheckExecutor;

    private Set<Class<?>>                          excludedIndicators             = new HashSet<>();

    private int                                    globalTimeout;

    private Map<String, HealthCheckerConfig>       healthIndicatorConfig;

    private boolean                                parallelCheck;

    private long                                   parallelCheckTimeout;

    static {
        REACTOR_CLASS_EXIST = ClassUtils.isPresent(REACTOR_CLASS, null);
    }

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Assert.notNull(healthCheckExecutor, () -> "HealthCheckExecutor must not be null");
            Map<String, HealthIndicator> beansOfType = applicationContext
                    .getBeansOfType(HealthIndicator.class);
            if (REACTOR_CLASS_EXIST) {
                applicationContext.getBeansOfType(ReactiveHealthIndicator.class).forEach(
                        (name, indicator) -> beansOfType.put(name, () -> indicator.health().block()));
            }

            healthIndicators = beansOfType.entrySet().stream().filter(entry -> !isExcluded(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            healthIndicators = HealthCheckComparatorSupport.sortMapAccordingToValue(healthIndicators,
                    HealthCheckComparatorSupport.getComparatorToUse(applicationContext.getAutowireCapableBeanFactory()));

            String healthIndicatorInfo = "Found " + healthIndicators.size() + " HealthIndicator implementation:" + String.join(",", healthIndicators.keySet());
            logger.info(healthIndicatorInfo);
        }
    }

    public void initExcludedIndicators(List<String> excludes) {
        if (CollectionUtils.isEmpty(excludes)) {
            excludes = DEFAULT_EXCLUDE_INDICATORS;
        } else {
            excludes.addAll(DEFAULT_EXCLUDE_INDICATORS);
        }

        excludedIndicators = new HashSet<>();
        for (String exclude : excludes) {
            try {
                Class<?> c = Class.forName(exclude);
                excludedIndicators.add(c);
            } catch (Throwable e) {
                logger.warn("Unable to find excluded HealthIndicator class {}, just ignore it.",
                    exclude);
            }
        }
    }

    public boolean isExcluded(Object target) {
        Class<?> klass = AopProxyUtils.ultimateTargetClass(target);
        for (Class<?> c : excludedIndicators) {
            if (c.isAssignableFrom(klass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provided for readiness check.
     *
     * @param healthMap used to save the information.
     * @return whether readiness health check passes or not
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthIndicators, () -> "HealthIndicators must not be null.");

        logger.info("Begin SOFABoot HealthIndicator readiness check.");
        String checkComponentNames = String.join(",", healthIndicators.keySet());
        logger.info("SOFABoot HealthIndicator readiness check {} item: {}.",
                healthIndicators.size(), checkComponentNames);
        boolean result;
        if (isParallelCheck()) {
            CountDownLatch countDownLatch = new CountDownLatch(healthIndicators.size());
            AtomicBoolean parallelResult = new AtomicBoolean(true);
            healthIndicators.forEach((key, value) -> healthCheckExecutor.execute(() -> {
                try {
                    if (!doHealthCheck(key, value, healthMap, false)) {
                        parallelResult.set(false);
                    }
                } catch (Throwable t) {
                    parallelResult.set(false);
                    logger.error(ErrorCode.convert("01-21003"), t);
                    healthMap.put(key, new Health.Builder().withException(t).status(Status.DOWN).build());
                } finally {
                    countDownLatch.countDown();
                }
            }));
            boolean finished = false;
            try {
                finished = countDownLatch.await(getParallelCheckTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error(ErrorCode.convert("01-21004"), e);
            }
            if (!finished) {
                parallelResult.set(false);
                healthMap.put("parallelCheck", new Health.Builder().withDetail("timeout", getParallelCheckTimeout())
                        .status(Status.UNKNOWN).build());
            }
            result = finished && parallelResult.get();
        } else {
            result = healthIndicators.entrySet().stream()
                    .map(entry -> doHealthCheck(entry.getKey(), entry.getValue(), healthMap, true))
                    .reduce(true, (a, b) -> a && b);
        }
        if (result) {
            logger.info("SOFABoot HealthIndicator readiness check result: success.");
        } else {
            logger.error(ErrorCode.convert("01-21000"));
        }
        return result;
    }

    public boolean doHealthCheck(String beanId, HealthIndicator healthIndicator,
                                 Map<String, Health> healthMap, boolean wait) {
        Assert.notNull(healthMap, () -> "HealthMap must not be null");

        boolean result;
        Health health;
        logger.info("HealthIndicator [{}] readiness check start.", beanId);
        int timeout = Optional.ofNullable(getHealthIndicatorConfig())
                .map(k -> getHealthIndicatorConfig().get(beanId))
                .map(HealthCheckerConfig::getTimeout)
                .orElse(getGlobalTimeout());
        Assert.isTrue(timeout > 0, "HealthIndicator timeout must lager than zero");

        BaseStat baseStat = new BaseStat();
        baseStat.setName(beanId);
        baseStat.putAttribute("type", "healthIndicator");
        baseStat.setStartTime(System.currentTimeMillis());

        try {
            if (wait) {
                Future<Health> future = healthCheckExecutor
                        .submit(healthIndicator::health);
                health = future.get(timeout, TimeUnit.MILLISECONDS);
            } else {
                health = healthIndicator.health();
            }
            Status status = health.getStatus();
            result = status.equals(Status.UP);
            if (!result) {
                logger.error(
                        ErrorCode.convert("01-21001",
                                beanId, status, objectMapper.writeValueAsString(health.getDetails())));
            }
        } catch (TimeoutException e) {
            result = false;
            logger.error(
                    "HealthIndicator[{}] readiness check fail; the status is: {}; the detail is: timeout, the timeout value is: {}ms.",
                    beanId, Status.UNKNOWN, timeout);
            health = new Health.Builder().withException(e).withDetail("timeout", timeout).status(Status.UNKNOWN).build();
        } catch (Exception e) {
            result = false;
            logger.error(
                    ErrorCode.convert("01-21002",
                            healthIndicator.getClass()),
                    e);
            health = new Health.Builder().withException(e).status(Status.DOWN).build();
        }

        baseStat.setEndTime(System.currentTimeMillis());
        healthIndicatorStartupStatList.add(baseStat);
        healthMap.put(getKey(beanId), health);

        return result;
    }

    /**
     * refer to {@link HealthContributorNameFactory#apply(String)}
     */
    public String getKey(String name) {
        int index = name.toLowerCase().indexOf("healthindicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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

    public Map<String, HealthCheckerConfig> getHealthIndicatorConfig() {
        return healthIndicatorConfig;
    }

    public void setHealthIndicatorConfig(Map<String, HealthCheckerConfig> healthIndicatorConfig) {
        this.healthIndicatorConfig = healthIndicatorConfig;
    }

    public List<BaseStat> getHealthIndicatorStartupStatList() {
        return healthIndicatorStartupStatList;
    }
}
