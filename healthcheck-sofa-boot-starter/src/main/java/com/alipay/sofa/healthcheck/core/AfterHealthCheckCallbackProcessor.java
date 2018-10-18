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
import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterReadinessCheckCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to check AfterHealthCheckCallback
 *
 * @author liangen
 * @author qiong.zql
 * @version 2.3.0
 */
public class AfterHealthCheckCallbackProcessor implements ApplicationContextAware {

    private static Logger                                              logger                                 = SofaBootHealthCheckLoggerFactory
                                                                                                                  .getLogger(AfterHealthCheckCallbackProcessor.class);

    private ObjectMapper                                               objectMapper                           = new ObjectMapper();

    private AtomicBoolean                                              isInitiated                            = new AtomicBoolean(
                                                                                                                  false);

    private ApplicationContext                                         applicationContext                     = null;

    private Map<String, SofaBootMiddlewareAfterReadinessCheckCallback> middlewareAfterReadinessCheckCallbacks = null;

    private Map<String, SofaBootAfterReadinessCheckCallback>           afterReadinessCheckCallbacks           = null;

    public void init() {
        if (isInitiated.compareAndSet(false, true)) {
            Assert.notNull(applicationContext, "Application must not be null");

            middlewareAfterReadinessCheckCallbacks = applicationContext
                .getBeansOfType(SofaBootMiddlewareAfterReadinessCheckCallback.class);
            afterReadinessCheckCallbacks = applicationContext
                .getBeansOfType(SofaBootAfterReadinessCheckCallback.class);

            StringBuilder middlewareCallbackInfo = new StringBuilder();
            middlewareCallbackInfo.append("Found ")
                .append(middlewareAfterReadinessCheckCallbacks.size())
                .append(" SofaBootMiddlewareAfterReadinessCheckCallback implementation:");
            for (String beanId : middlewareAfterReadinessCheckCallbacks.keySet()) {
                middlewareCallbackInfo.append(beanId).append(",");
            }
            logger.info(middlewareCallbackInfo.deleteCharAt(middlewareCallbackInfo.length() - 1)
                .toString());

            StringBuilder applicationCallbackInfo = new StringBuilder();
            applicationCallbackInfo.append("Found ").append(afterReadinessCheckCallbacks.size())
                .append(" SofaBootAfterReadinessCheckCallback implementation:");
            for (String beanId : afterReadinessCheckCallbacks.keySet()) {
                applicationCallbackInfo.append(beanId).append(",");
            }
            logger.info(applicationCallbackInfo.deleteCharAt(applicationCallbackInfo.length() - 1)
                .toString());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext cxt) throws BeansException {
        this.applicationContext = cxt;
    }

    public boolean afterReadinessCheckCallback(Map<String, Health> healthMap) {
        logger.info("Begin SOFABoot ReadinessCheckCallback check.");
        boolean result = true;
        result = middlewareAfterHealthCheckCallback(healthMap) && result;
        result = applicationAfterHealthCheckCallback(healthMap) && result;
        if (result) {
            logger.info("SOFABoot ReadinessCheckCallback check: success.");
        } else {
            logger.error("SOFABoot ReadinessCheckCallback check: failed.");
        }
        return result;
    }

    /**
     * process middleware afterHealthCheckCallback
     *
     * @param healthMap Used to save information of healthcheck.
     * @return Callback check result.
     */
    private boolean middlewareAfterHealthCheckCallback(Map<String, Health> healthMap) {
        Assert.notNull(middlewareAfterReadinessCheckCallbacks,
            "SofaBootMiddlewareAfterReadinessCheckCallback must not be null.");

        logger.info("Begin SofaBootMiddlewareAfterReadinessCheckCallback check");
        boolean result = true;
        for (String beanId : middlewareAfterReadinessCheckCallbacks.keySet()) {
            result = doHealthCheckCallback(beanId,
                middlewareAfterReadinessCheckCallbacks.get(beanId), healthMap)
                     && result;
        }
        if (result) {
            logger.info("SofaBootMiddlewareAfterReadinessCheckCallback check result: success.");
        } else {
            logger.error("SofaBootMiddlewareAfterReadinessCheckCallback check result: failed.");
        }
        return result;
    }

    /**
     * process application afterHealthCheckCallback
     * @return
     */
    private boolean applicationAfterHealthCheckCallback(Map<String, Health> healthMap) {
        Assert.notNull(afterReadinessCheckCallbacks,
            "SofaBootAfterReadinessCheckCallback must not be null.");

        logger.info("Begin SofaBootAfterReadinessCheckCallback readiness check");
        boolean result = true;
        for (String beanId : afterReadinessCheckCallbacks.keySet()) {
            result = doHealthCheckCallback(beanId, afterReadinessCheckCallbacks.get(beanId),
                healthMap) && result;
        }
        if (result) {
            logger.info("SofaBootAfterReadinessCheckCallback readiness check result: success.");
        } else {
            logger.error("SofaBootAfterReadinessCheckCallback readiness check result: failed.");
        }
        return result;
    }

    private boolean doHealthCheckCallback(String beanId,
                                          ReadinessCheckCallback readinessCheckCallback,
                                          Map<String, Health> healthMap) {
        Assert.notNull(healthMap, "HealthMap must not be null");

        boolean result = false;
        Health health = null;
        try {
            health = readinessCheckCallback.onHealthy(applicationContext);
            if (health.getStatus().equals(Status.UP)) {
                result = true;
                logger.info("SOFABoot ReadinessCheckCallback[{}] check success.", beanId);
            } else {
                logger.error(
                    "SOFABoot ReadinessCheckCallback[{}] check failed, the details is: {}.",
                    beanId, objectMapper.writeValueAsString(health.getDetails()));
            }
        } catch (Throwable t) {
            health = new Health.Builder().down(new RuntimeException(t)).build();
            logger.error(String.format(
                "Error occurred while doing ReadinessCheckCallback[%s] check.", beanId), t);
        } finally {
            healthMap.put(beanId, health);
        }
        return result;
    }
}