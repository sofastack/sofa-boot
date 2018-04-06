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

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfiguration;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus.HealthIndicatorDetail;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 *
 * @author liangen
 * @version $Id: HealthIndicatorCheckProcessor.java, v 0.1 2017年10月20日 上午10:55 liangen Exp $
 */
public class HealthIndicatorCheckProcessor {
    private static Logger logger = SofaBootHealthCheckLoggerFactory.getLogger(HealthIndicatorCheckProcessor.class
                                     .getCanonicalName());

    public boolean checkIndicator() {
        if (skipHealthIndicator()) {
            logger.info("Skip startup healthIndicator check.");
            return true;
        }

        logger.info("Begin startup healthIndicator check.");

        List<HealthIndicator> healthIndicators = HealthCheckManager.getHealthIndicator();
        if (healthIndicators == null) {
            return true;
        }

        boolean result = true;
        for (HealthIndicator healthIndicator : healthIndicators) {
            try {
                Health health = healthIndicator.health();
                Status status = health.getStatus();
                if (!status.equals(Status.UP)) {
                    result = false;
                    logger.error("healthIndicator (" + healthIndicator.getClass()
                        + ")check fail. And the status is[" + status + "]; the detail is: " +
                        JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("healthIndicator (" + healthIndicator.getClass()
                        + ")check success.");
                }

                StartUpHealthCheckStatus.addHealthIndicatorDetail(new HealthIndicatorDetail(getKey(healthIndicator),
                    health));
            } catch (Exception e) {
                result = false;
                logger.error("Error occurred while doing healthIndicator health check("
                    + healthIndicator.getClass() + ")", e);
            }

        }

        if (result) {
            logger.info("Startup healthIndicator check result: success.");
        } else {
            logger.error("Startup healthIndicator check result: fail.");
        }

        StartUpHealthCheckStatus.setHealthIndicatorStatus(result);
        return result;
    }

    private boolean skipHealthIndicator() {
        String skipHealthIndicator = HealthCheckConfiguration
            .getPropertyAllCircumstances(HealthCheckConfigurationConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK);

        if (!StringUtils.hasText(skipHealthIndicator)) {
            return false;
        }

        return "true".equalsIgnoreCase(skipHealthIndicator);

    }

    private String getKey(HealthIndicator healthIndicator) {

        String[] fullName = healthIndicator.getClass().getName().split("\\.");
        String name = fullName[fullName.length - 1];

        int index = name.toLowerCase().indexOf("healthindicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }

}