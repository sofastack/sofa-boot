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
import com.alipay.sofa.healthcheck.startup.SofaBootAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterReadinessCheckCallback;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static Logger             logger       = SofaBootHealthCheckLoggerFactory
                                                       .getLogger(AfterHealthCheckCallbackProcessor.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public boolean checkAfterHealthCheckCallback() {

        boolean result = false;

        if (doMiddlewareAfterHealthCheckCallback()) {
            result = doApplicationAfterHealthCheckCallback();
        }

        StartUpHealthCheckStatus.setAfterHealthCheckCallbackStatus(result);

        if (result) {
            logger.info("SOFABoot readiness check callback : success.");
        } else {
            logger.error("SOFABoot readiness check callback : failed.");
        }
        return result;

    }

    /**
     * process middleware afterHealthCheckCallback
     * @return
     */
    private boolean doMiddlewareAfterHealthCheckCallback() {
        boolean result = true;
        logger.info("Begin SofaBootMiddlewareAfterReadinessCheckCallback readiness check");

        List<SofaBootMiddlewareAfterReadinessCheckCallback> middlewareAfterReadinessCheckCallbacks = HealthCheckManager
            .getMiddlewareAfterHealthCheckCallbacks();
        for (SofaBootMiddlewareAfterReadinessCheckCallback middlewareAfterReadinessCheckCallback : middlewareAfterReadinessCheckCallbacks) {
            try {
                Health health = middlewareAfterReadinessCheckCallback.onHealthy(HealthCheckManager
                    .getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("SOFABoot middleware after readiness check callback("
                                 + middlewareAfterReadinessCheckCallback.getClass()
                                 + ") failed, the details is: "
                                 + objectMapper.writeValueAsString(health.getDetails()));
                } else {
                    logger.info("SOFABoot middleware after readiness check callback("
                                + middlewareAfterReadinessCheckCallback.getClass() + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(
                    getKey(middlewareAfterReadinessCheckCallback.getClass().getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking SofaBootMiddlewareAfterReadinessCheckCallback "
                             + middlewareAfterReadinessCheckCallback.getClass().getName()
                             + " got an exception.", t);
            }
        }

        if (result) {
            logger
                .info("SofaBootMiddlewareAfterReadinessCheckCallback readiness check result: success.");
        } else {
            logger
                .error("SofaBootMiddlewareAfterReadinessCheckCallback readiness check result: failed.");

        }
        return result;
    }

    /**
     * process application afterHealthCheckCallback
     * @return
     */
    private boolean doApplicationAfterHealthCheckCallback() {
        boolean result = true;

        logger.info("Begin SofaBootAfterReadinessCheckCallback readiness check");

        List<SofaBootAfterReadinessCheckCallback> afterReadinessCheckCallbacks = HealthCheckManager
            .getApplicationAfterHealthCheckCallbacks();
        for (SofaBootAfterReadinessCheckCallback afterReadinessCheckCallback : afterReadinessCheckCallbacks) {
            try {
                Health health = afterReadinessCheckCallback.onHealthy(HealthCheckManager
                    .getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("SOFABoot application after readiness check callback("
                                 + afterReadinessCheckCallback.getClass()
                                 + ") failed, the details is: "
                                 + objectMapper.writeValueAsString(health.getDetails()));
                } else {
                    logger.info("SOFABoot application after readiness check callback("
                                + afterReadinessCheckCallback.getClass() + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(
                    getKey(afterReadinessCheckCallback.getClass().getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking  SofaBootAfterReadinessCheckCallback "
                             + afterReadinessCheckCallback.getClass().getName()
                             + " got an exception.", t);
            }
        }

        if (result) {
            logger.info("SofaBootAfterReadinessCheckCallback readiness check result: success.");
        } else {
            logger.error("SofaBootAfterReadinessCheckCallback readiness check result: failed.");

        }
        return result;
    }

    private String getKey(String className) {
        String[] fullName = className.split("\\.");
        return fullName[fullName.length - 1];
    }
}