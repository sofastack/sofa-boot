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

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.startup.SofaBootAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

/**
 * Used to check AfterHealthCheckCallback
 * @author liangen
 * @version $Id: AfterHealthCheckCallback.java, v 0.1 2018年03月09日 上午11:10 liangen Exp $
 */
public class AfterHealthCheckCallbackProcessor {
    private static Logger logger = SofaBootHealthCheckLoggerFactory.getLogger(AfterHealthCheckCallbackProcessor.class
                                     .getCanonicalName());

    public boolean checkAfterHealthCheckCallback() {

        boolean result = false;

        if (doMiddlewareAfterHealthCheckCallback()) {
            result = doApplicationAfterHealthCheckCallback();
        }

        StartUpHealthCheckStatus.setAfterHealthCheckCallbackStatus(result);

        if (result) {
            logger.info("SOFABoot readiness check callback startup health check result: success.");
        } else {
            logger.error("SOFABoot readiness check callback startup health check result: failed.");
        }
        return result;

    }

    /**
     * process middleware afterHealthCheckCallback
     * @return
     */
    private boolean doMiddlewareAfterHealthCheckCallback() {
        boolean result = true;
        logger.info("Begin SofaBootMiddlewareAfterReadinessCheckCallback startup health check");

        List<SofaBootMiddlewareAfterReadinessCheckCallback> afterHealthCheckCallbacks = HealthCheckManager
            .getMiddlewareAfterHealthCheckCallbacks();
        for (SofaBootMiddlewareAfterReadinessCheckCallback afterHealthCheckCallback : afterHealthCheckCallbacks) {
            try {
                Health health = afterHealthCheckCallback.onHealthy(HealthCheckManager.getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("SOFABoot middleware afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") failed, the details is: "
                        + JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("SOFABoot middleware afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(getKey(afterHealthCheckCallback.getClass()
                    .getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking SofaBootMiddlewareAfterReadinessCheckCallback "
                    + afterHealthCheckCallback.getClass().getName()
                    + " got an exception.", t);
            }
        }

        if (result) {
            logger.info("SofaBootMiddlewareAfterReadinessCheckCallback startup health check result: success.");
        } else {
            logger.error("SofaBootMiddlewareAfterReadinessCheckCallback startup health check result: failed.");

        }
        return result;
    }

    /**
     * process application afterHealthCheckCallback
     * @return
     */
    private boolean doApplicationAfterHealthCheckCallback() {
        boolean result = true;

        logger.info("Begin SofaBootAfterReadinessCheckCallback startup health check");

        List<SofaBootAfterReadinessCheckCallback> afterHealthCheckCallbacks = HealthCheckManager
            .getApplicationAfterHealthCheckCallbacks();
        for (SofaBootAfterReadinessCheckCallback afterHealthCheckCallback : afterHealthCheckCallbacks) {
            try {
                Health health = afterHealthCheckCallback.onHealthy(HealthCheckManager.getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("SOFABoot application afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") failed, the details is: "
                        + JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("SOFABoot application afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(getKey(afterHealthCheckCallback.getClass()
                    .getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking  SofaBootAfterReadinessCheckCallback "
                    + afterHealthCheckCallback.getClass().getName()
                    + " got an exception.", t);
            }
        }

        if (result) {
            logger.info("SofaBootAfterReadinessCheckCallback startup health check result: success.");
        } else {
            logger.error("SofaBootAfterReadinessCheckCallback startup health check result: failed.");

        }
        return result;
    }

    private String getKey(String className) {

        String[] fullName = className.split("\\.");
        String name = fullName[fullName.length - 1];

        return name;
    }
}