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
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.alipay.sofa.boot.util.BinaryOperators;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to check {@link ReadinessCheckCallback}
 *
 * @author liangen
 * @author qiong.zql
 * @version 2.3.0
 */
public class AfterReadinessCheckCallbackProcessor {

    private static Logger                                 logger                  = HealthCheckLoggerFactory
                                                                                      .getLogger(AfterReadinessCheckCallbackProcessor.class);

    private ObjectMapper                                  objectMapper            = new ObjectMapper();

    private AtomicBoolean                                 isInitiated             = new AtomicBoolean(
                                                                                      false);
    @Autowired
    private ApplicationContext                            applicationContext;

    private LinkedHashMap<String, ReadinessCheckCallback> readinessCheckCallbacks = null;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Map<String, ReadinessCheckCallback> beansOfType = applicationContext
                    .getBeansOfType(ReadinessCheckCallback.class);
            readinessCheckCallbacks = HealthCheckUtils.sortMapAccordingToValue(beansOfType,
                    applicationContext.getAutowireCapableBeanFactory());

            StringBuilder applicationCallbackInfo = new StringBuilder(512).append("Found ")
                    .append(readinessCheckCallbacks.size())
                    .append(" ReadinessCheckCallback implementation: ")
                    .append(String.join(",", beansOfType.keySet()));
            logger.info(applicationCallbackInfo.toString());
        }
    }

    public boolean afterReadinessCheckCallback(Map<String, Health> healthMap) {
        logger.info("Begin ReadinessCheckCallback readiness check");
        Assert.notNull(readinessCheckCallbacks, "ReadinessCheckCallbacks must not be null.");

        boolean result = readinessCheckCallbacks.entrySet().stream()
                .map(entry -> doHealthCheckCallback(entry.getKey(), entry.getValue(), healthMap))
                .reduce(true, BinaryOperators.andBoolean());

        if (result) {
            logger.info("ReadinessCheckCallback readiness check result: success.");
        } else {
            logger.error("ReadinessCheckCallback readiness check result: failed.");
        }
        return result;
    }

    private boolean doHealthCheckCallback(String beanId,
                                          ReadinessCheckCallback readinessCheckCallback,
                                          Map<String, Health> healthMap) {
        Assert.notNull(healthMap, () -> "HealthMap must not be null");

        boolean result = false;
        Health health = null;
        try {
            health = readinessCheckCallback.onHealthy(applicationContext);
            result = health.getStatus().equals(Status.UP);
            if (result) {
                logger.info("SOFABoot ReadinessCheckCallback[{}] check success.", beanId);
            } else {
                logger.error(
                        "SOFABoot ReadinessCheckCallback[{}] check failed, the details is: {}.", beanId,
                        objectMapper.writeValueAsString(health.getDetails()));
            }
        } catch (Throwable t) {
            if (health == null) {
                health = new Health.Builder().down(new RuntimeException(t)).build();
            }
            logger.error(String
                    .format("Error occurred while doing ReadinessCheckCallback[%s] check.", beanId), t);
        } finally {
            healthMap.put(beanId, health);
        }
        return result;
    }
}