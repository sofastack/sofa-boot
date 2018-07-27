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
import com.alipay.sofa.healthcheck.service.SofaBootHealthIndicator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicatorNameFactory;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to process all implementations of {@link HealthIndicator}
 *
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthIndicatorProcessor implements ApplicationContextAware {

    private static Logger                logger           = SofaBootHealthCheckLoggerFactory
                                                              .getLogger(HealthIndicatorProcessor.class);

    private ObjectMapper                 objectMapper     = new ObjectMapper();

    private AtomicBoolean                isInitiated      = new AtomicBoolean(false);

    private Map<String, HealthIndicator> healthIndicators = null;

    private ApplicationContext           applicationContext;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, "Application must not be null");

            healthIndicators = applicationContext.getBeansOfType(HealthIndicator.class);
            StringBuilder healthIndicatorInfo = new StringBuilder();
            healthIndicatorInfo.append("Found ").append(healthIndicators.size())
                .append(" HealthIndicator implementation:");
            for (String beanId : healthIndicators.keySet()) {
                healthIndicatorInfo.append(beanId).append(",");
            }
            logger.info(healthIndicatorInfo.deleteCharAt(healthIndicatorInfo.length() - 1)
                .toString());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext cxt) throws BeansException {
        applicationContext = cxt;
    }

    /**
     * Provided for readiness check.
     *
     * @param healthMap
     * @return
     */
    public boolean readinessHealthCheck(Map<String, Health> healthMap) {
        Assert.notNull(healthIndicators, "HealthIndicators must not be null.");

        logger.info("Begin SOFABoot HealthIndicator readiness check.");
        boolean result = true;
        for (String beanId : healthIndicators.keySet()) {
            if (healthIndicators.get(beanId) instanceof SofaBootHealthIndicator) {
                continue;
            }
            result = doHealthCheck(beanId, healthIndicators.get(beanId), healthMap) && result;
        }
        if (result) {
            logger.info("SOFABoot HealthIndicator readiness check result: success.");
        } else {
            logger.error("SOFABoot HealthIndicator readiness check result: failed.");
        }
        return result;
    }

    public boolean doHealthCheck(String beanId, HealthIndicator healthIndicator,
                                 Map<String, Health> healthMap) {
        Assert.notNull(healthMap, "HealthMap must not be null");

        boolean result = true;
        try {
            Health health = healthIndicator.health();
            Status status = health.getStatus();
            if (!status.equals(Status.UP)) {
                result = false;
                logger
                    .error(
                        "HealthIndicator[{}] readiness check fail; the status is: {}; the detail is: {}.",
                        beanId, status, objectMapper.writeValueAsString(health.getDetails()));
            } else {
                logger.info("HealthIndicator[{}] readiness check success.", beanId);
            }
            healthMap.put(getKey(beanId), health);
        } catch (Exception e) {
            result = false;
            logger.error(String.format(
                "Error occurred while doing HealthIndicator[%s] readiness check.",
                healthIndicator.getClass()), e);
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