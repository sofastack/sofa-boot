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
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A processor used to process {@link ReadinessCheckCallback}.
 *
 * @author liangen
 * @author qiong.zql
 * @version 2.3.0
 */
public class ReadinessCheckCallbackProcessor implements ApplicationContextAware {

    private static final Logger                           logger                                = SofaBootLoggerFactory
                                                                                                    .getLogger(ReadinessCheckCallbackProcessor.class);

    private final ObjectMapper                            objectMapper                          = new ObjectMapper();

    private final AtomicBoolean                           isInitiated                           = new AtomicBoolean(
                                                                                                    false);

    private final List<BaseStat>                          readinessCheckCallbackStartupStatList = new CopyOnWriteArrayList<>();

    private ApplicationContext                            applicationContext;

    private LinkedHashMap<String, ReadinessCheckCallback> readinessCheckCallbacks               = null;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, () -> "Application must not be null");
            Map<String, ReadinessCheckCallback> beansOfType = applicationContext
                    .getBeansOfType(ReadinessCheckCallback.class);
            readinessCheckCallbacks = HealthCheckComparatorSupport.sortMapAccordingToValue(beansOfType,
                    HealthCheckComparatorSupport.getComparatorToUse(applicationContext.getAutowireCapableBeanFactory()));

            String applicationCallbackInfo = "Found " + readinessCheckCallbacks.size() + " ReadinessCheckCallback implementation: " + String.join(",", beansOfType.keySet());
            logger.info(applicationCallbackInfo);
        }
    }

    public boolean readinessCheckCallback(Map<String, Health> callbackDetails) {
        logger.info("Begin ReadinessCheckCallback readiness check");
        Assert.notNull(readinessCheckCallbacks, "ReadinessCheckCallbacks must not be null.");

        boolean allResult = true;
        String failedBeanId = "";
        for (Map.Entry<String, ReadinessCheckCallback> entry : readinessCheckCallbacks.entrySet()) {
            String beanId = entry.getKey();
            if (allResult) {
                if (!doHealthCheckCallback(beanId, entry.getValue(), callbackDetails)) {
                    failedBeanId = beanId;
                    allResult = false;
                }
            } else {
                logger.warn(beanId + " is skipped due to the failure of " + failedBeanId);
                callbackDetails.put(
                    beanId,
                    Health.down()
                        .withDetail("invoking", "skipped due to the failure of " + failedBeanId)
                        .build());
            }
        }

        if (allResult) {
            logger.info("ReadinessCheckCallback readiness check result: success.");
        } else {
            logger.error(ErrorCode.convert("01-24000"));
        }
        return allResult;
    }

    private boolean doHealthCheckCallback(String beanId,
                                          ReadinessCheckCallback readinessCheckCallback,
                                          Map<String, Health> callbackDetails) {
        Assert.notNull(callbackDetails, () -> "HealthCallbackDetails must not be null");

        boolean result = false;
        Health health = null;
        logger.info("ReadinessCheckCallback [{}] check start.", beanId);

        BaseStat baseStat = new BaseStat();
        baseStat.setName(beanId);
        baseStat.putAttribute("type", "readinessCheckCallbacn");
        baseStat.setStartTime(System.currentTimeMillis());

        try {
            health = readinessCheckCallback.onHealthy(applicationContext);
            result = health.getStatus().equals(Status.UP);
            if (!result) {
                logger.error(
                        ErrorCode.convert("01-24001", beanId,
                                objectMapper.writeValueAsString(health.getDetails())));
            }
        } catch (Throwable t) {
            if (health == null) {
                health = new Health.Builder().down(new RuntimeException(t)).build();
            }
            logger.error(ErrorCode.convert("01-24002", beanId), t);
        } finally {
            callbackDetails.put(beanId, health);
        }

        baseStat.setEndTime(System.currentTimeMillis());
        readinessCheckCallbackStartupStatList.add(baseStat);
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<BaseStat> getReadinessCheckCallbackStartupStatList() {
        return readinessCheckCallbackStartupStatList;
    }
}
