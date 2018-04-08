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
    private static Logger logger = SofaBootHealthCheckLoggerFactory
                                     .getLogger(HealthIndicatorCheckProcessor.class
                                         .getCanonicalName());

    public boolean checkIndicator() {
        if (skipHealthIndicator()) {
            logger.info("Skip HealthIndicator readiness check.");
            return true;
        }

        logger.info("Begin HealthIndicator readiness check.");

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
                    logger.error("HealthIndicator (" + healthIndicator.getClass()
                                 + ")check fail. And the status is[" + status
                                 + "]; the detail is: " + JSON.toJSONString(health.getDetails()));
                } else {
                    logger.info("HealthIndicator (" + healthIndicator.getClass()
                                + ")check success.");
                }

                StartUpHealthCheckStatus.addHealthIndicatorDetail(new HealthIndicatorDetail(
                    getKey(healthIndicator), health));
            } catch (Exception e) {
                result = false;
                logger.error("Error occurred while doing HealthIndicator readiness check("
                             + healthIndicator.getClass() + ")", e);
            }

        }

        if (result) {
            logger.info("Readiness check HealthIndicator result: success.");
        } else {
            logger.error("Readiness check HealthIndicator result: fail.");
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

        int index = name.toLowerCase().indexOf("HealthIndicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }

}