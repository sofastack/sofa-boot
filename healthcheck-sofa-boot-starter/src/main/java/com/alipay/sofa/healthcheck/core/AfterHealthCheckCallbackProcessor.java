/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.healthcheck.core;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.startup.SofaBootApplicationAfterHealthCheckCallback;
import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterHealthCheckCallback;
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
            logger.info("AfterHealthCheckCallback startup health check result: success.");
        } else {
            logger.error("AfterHealthCheckCallback startup health check result: failed.");
        }
        return result;

    }

    /**
     * process middleware afterHealthCheckCallback
     * @return
     */
    private boolean doMiddlewareAfterHealthCheckCallback() {
        boolean result = true;
        logger.info("Begin MiddlewareAfterHealthCheckCallback startup health check");

        List<SofaBootMiddlewareAfterHealthCheckCallback> afterHealthCheckCallbacks = HealthCheckManager
            .getMiddlewareAfterHealthCheckCallbacks();
        for (SofaBootMiddlewareAfterHealthCheckCallback afterHealthCheckCallback : afterHealthCheckCallbacks) {
            try {
                Health health = afterHealthCheckCallback.onHealthy(HealthCheckManager.getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("sofaboot middleware afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") failed, the details is: "
                        + JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("sofaboot middleware afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(getKey(afterHealthCheckCallback.getClass()
                    .getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking MiddlewareAfterHealthCheckCallback "
                    + afterHealthCheckCallback.getClass().getName()
                    + " got an exception.", t);
            }
        }

        if (result) {
            logger.info("MiddlewareAfterHealthCheckCallback startup health check result: success.");
        } else {
            logger.error("MiddlewareAfterHealthCheckCallback startup health check result: failed.");

        }
        return result;
    }

    /**
     * process application afterHealthCheckCallback
     * @return
     */
    private boolean doApplicationAfterHealthCheckCallback() {
        boolean result = true;

        logger.info("Begin ApplicationAfterHealthCheckCallback startup health check");

        List<SofaBootApplicationAfterHealthCheckCallback> afterHealthCheckCallbacks = HealthCheckManager
            .getApplicationAfterHealthCheckCallbacks();
        for (SofaBootApplicationAfterHealthCheckCallback afterHealthCheckCallback : afterHealthCheckCallbacks) {
            try {
                Health health = afterHealthCheckCallback.onHealthy(HealthCheckManager.getApplicationContext());
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;

                    logger.error("sofaboot application afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") failed, the details is: "
                        + JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("sofaboot application afterHealthCheck callback("
                        + afterHealthCheckCallback.getClass()
                        + ") ]success.");
                }

                StartUpHealthCheckStatus.putAfterHealthCheckCallbackDetail(getKey(afterHealthCheckCallback.getClass()
                    .getName()), health);

            } catch (Throwable t) {
                result = false;

                logger.error("Invoking  ApplicationAfterHealthCheckCallback "
                    + afterHealthCheckCallback.getClass().getName()
                    + " got an exception.", t);
            }
        }

        if (result) {
            logger.info("ApplicationAfterHealthCheckCallback startup health check result: success.");
        } else {
            logger.error("ApplicationAfterHealthCheckCallback startup health check result: failed.");

        }
        return result;
    }

    private String getKey(String className) {

        String[] fullName = className.split("\\.");
        String name = fullName[fullName.length - 1];

        return name;
    }
}