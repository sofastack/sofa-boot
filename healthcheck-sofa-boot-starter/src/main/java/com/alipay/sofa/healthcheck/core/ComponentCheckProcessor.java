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
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.configuration.HealthCheckConfiguration;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.util.HealthCheckUtil;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Used to check Component
 * @author liangen
 * @version $Id: ComponentCheckProcessor.java, v 0.1 2017年10月20日 上午10:52 liangen Exp $
 */
public class ComponentCheckProcessor {
    private static Logger logger = SofaBootHealthCheckLoggerFactory.getLogger(ComponentCheckProcessor.class
                                     .getCanonicalName());

    /**
     * Provides for HTTP checking. The method does not retry the reference component.
     * @param healthMap used to load the information of component process.
     * @return
     */
    public boolean httpCheckComponent(Map<String, Health> healthMap) {
        boolean result = true;

        logger.info("Begin http component health check.");

        for (HealthChecker healthChecker : HealthCheckManager.getHealthCheckers()) {

            boolean resultItem = doCheckComponentHealth(healthChecker, false, healthMap);
            if (!resultItem) {
                result = false;
            }

        }

        if (result) {
            logger.info("http component health check result: success.");
        } else {
            logger.error("http component health check result: failed.");

        }

        return result;
    }

    /**
     * Provides for starting check. This method retries the reference component.
     * @return
     */
    public boolean startupCheckComponent() {
        if (skipComponentHealthCheck()) {
            logger.info("Skip startup component health check.");
            return true;
        }

        logger.info("Begin startup component health check.");

        boolean result = true;

        for (HealthChecker healthChecker : HealthCheckManager.getHealthCheckers()) {

            boolean resultItem = doCheckComponentHealth(healthChecker, true, null);
            if (!resultItem) {
                result = false;
            }
        }

        if (result) {
            logger.info("component startup health check result: success.");
        } else {
            logger.error("component startup health check result: failed.");
        }

        StartUpHealthCheckStatus.setComponentStatus(result);
        return result;
    }

    private boolean skipComponentHealthCheck() {
        String skipComponentHealthCheck = HealthCheckConfiguration
            .getPropertyAllCircumstances(HealthCheckConfigurationConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK);

        if (!StringUtils.hasText(skipComponentHealthCheck)) {
            return false;
        }

        return "true".equalsIgnoreCase(skipComponentHealthCheck);
    }

    private boolean doCheckComponentHealth(HealthChecker healthChecker, boolean isRetry, Map<String, Health> healthMap) {

        boolean result = true;

        Health health = healthChecker.isHealthy();
        String componentName = healthChecker.getComponentName();
        int retryCount = healthChecker.getRetryCount();
        long retryTimeInterval = healthChecker.getRetryTimeInterval();
        boolean isStrictCheck = healthChecker.isStrictCheck();

        if (!isRetry) {
            retryCount = 0;
        }
        if (!HealthCheckUtil.isHealth(health)) {
            for (int i = 0; i < retryCount; i++) {

                try {
                    TimeUnit.MILLISECONDS.sleep(retryTimeInterval);
                } catch (InterruptedException e) {
                    logger.error("Exception occurred while sleeping of retry component health check.", e);
                }

                health = healthChecker.isHealthy();
                if (HealthCheckUtil.isHealth(health)) {

                    logger.info("component health check success. component name[" + componentName + "]; retry count[" +
                        (i + 1) + "]");
                    break;
                } else {
                    logger.error("component health check failed. component name[" + componentName + "]; retry count[" +
                        (i + 1) + "]; fail details:[" + health.getDetails() + "]");

                }

            }

            if (retryCount == 0) {
                logger.error("component health check failed. component name[" + componentName +
                    "]; no retry; fail details:[" + health.getDetails() + "]");
            }

        } else {
            logger.info("component health check success. component name[" + componentName + "]; no retry.");

        }

        StartUpHealthCheckStatus.putComponentDetail(componentName, health);
        if (healthMap != null) {
            healthMap.put(componentName, health);
        }

        if (!isStrictCheck) {
            result = true;
        } else {
            result = HealthCheckUtil.isHealth(health);
        }

        return result;
    }
}