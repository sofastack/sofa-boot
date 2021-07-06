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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.core.HealthCheckExecutor;
import org.slf4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicatorNameFactory;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

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
    private static Logger                          logger                     = HealthCheckLoggerFactory
                                                                                  .getLogger(HealthIndicatorProcessor.class);

    private static final List<String>              DEFAULT_EXCLUDE_INDICATORS = Arrays
                                                                                  .asList(
                                                                                      "com.alipay.sofa.boot.health.NonReadinessCheck",
                                                                                      "org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator",
                                                                                      "org.springframework.boot.actuate.availability.LivenessStateHealthIndicator");

    private ObjectMapper                           objectMapper               = new ObjectMapper();

    private AtomicBoolean                          isInitiated                = new AtomicBoolean(
                                                                                  false);

    private LinkedHashMap<String, HealthIndicator> healthIndicators           = null;

    @Autowired
    private ApplicationContext                     applicationContext;
    private Environment                            environment;

    private final static String                    REACTOR_CLASS              = "reactor.core.publisher.Mono";

    @Autowired
    private HealthCheckProperties                  healthCheckProperties;

    private Set<Class<?>>                          excludedIndicators;

    @Value("${" + SofaBootConstants.SOFABOOT_HEALTH_CHECK_DEFAULT_TIMEOUT + ":"
           + SofaBootConstants.SOFABOOT_HEALTH_CHECK_DEFAULT_TIMEOUT_VALUE
           + "}")
    private int                                    defaultTimeout;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            environment = applicationContext.getEnvironment();
            Map<String, HealthIndicator> beansOfType = applicationContext
                    .getBeansOfType(HealthIndicator.class);
            if (ClassUtils.isPresent(REACTOR_CLASS, null)) {
                applicationContext.getBeansOfType(ReactiveHealthIndicator.class).forEach(
                        (name, indicator) -> beansOfType.put(name, () -> indicator.health().block()));
            }
            initExcludedIndicators(healthCheckProperties.getExcludedIndicators());

            healthIndicators = beansOfType.entrySet().stream().filter(entry -> !isExcluded(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            healthIndicators = HealthCheckUtils.sortMapAccordingToValue(healthIndicators,
                    applicationContext.getAutowireCapableBeanFactory());

            StringBuilder healthIndicatorInfo = new StringBuilder(512).append("Found ")
                    .append(healthIndicators.size()).append(" HealthIndicator implementation:")
                    .append(String.join(",", healthIndicators.keySet()));
            logger.info(healthIndicatorInfo.toString());
        }
    }

    private void initExcludedIndicators(List<String> excludes) {
        if (excludes == null || excludes.size() == 0) {
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

    private boolean isExcluded(Object target) {
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
     * @param healthMap used to save the information of {@link HealthIndicator}.
     * @return whether readiness health check passes or not
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthIndicators, () -> "HealthIndicators must not be null.");

        logger.info("Begin SOFABoot HealthIndicator readiness check.");
        String checkComponentNames = healthIndicators.keySet().stream()
                .collect(Collectors.joining(","));
        logger.info("SOFABoot HealthChecker readiness check {} item: {}.",
                healthIndicators.size(), checkComponentNames);
        boolean result = healthIndicators.entrySet().stream()
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
        Health health;
        logger.info("HealthIndicator[{}] readiness check start.", beanId);
        Integer timeout = environment.getProperty(
                SofaBootConstants.SOFABOOT_INDICATOR_HEALTH_CHECK_TIMEOUT_PREFIX + beanId,
                Integer.class);
        if (timeout == null || timeout <= 0) {
            timeout = defaultTimeout;
        }
        try {
            Future<Health> future = HealthCheckExecutor
                    .submitTask(environment, healthIndicator::health);
            health = future.get(timeout, TimeUnit.MILLISECONDS);
            Status status = health.getStatus();
            result = status.equals(Status.UP);
            if (result) {
                logger.info("HealthIndicator[{}] readiness check success.", beanId);
            } else {
                logger.error(
                        "HealthIndicator[{}] readiness check fail; the status is: {}; the detail is: {}.",
                        beanId, status,
                        objectMapper.writeValueAsString(health.getDetails()));
            }
            healthMap.put(getKey(beanId), health);
        } catch (TimeoutException e) {
            result = false;
            logger.error(
                    "HealthIndicator[{}] readiness check fail; the status is: {}; the detail is: timeout, the timeout value is: {}ms.",
                    beanId, Status.UNKNOWN, timeout);
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
