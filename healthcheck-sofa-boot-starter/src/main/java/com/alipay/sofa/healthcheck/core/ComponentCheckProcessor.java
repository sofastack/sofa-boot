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
    private static Logger logger = SofaBootHealthCheckLoggerFactory
                                     .getLogger(ComponentCheckProcessor.class.getCanonicalName());

    /**
     * Provides for HTTP checking. The method does not retry the reference component.
     * @param healthMap used to load the information of component process.
     * @return
     */
    public boolean livenessCheckComponent(Map<String, Health> healthMap) {
        boolean result = true;

        logger.info("Begin SOFABoot component readiness check.");

        for (HealthChecker healthChecker : HealthCheckManager.getHealthCheckers()) {
            boolean resultItem = doCheckComponentHealth(healthChecker, false, healthMap);
            if (!resultItem) {
                result = false;
            }
        }

        if (result) {
            logger.info("SOFABoot component readiness check result: success.");
        } else {
            logger.error("SOFABoot component readiness check result: failed.");
        }

        return result;
    }

    /**
     * Provides for starting check. This method retries the reference component.
     * @return
     */
    public boolean startupCheckComponent() {
        if (skipComponentHealthCheck()) {
            logger.info("Skip SOFABoot component readiness check.");
            return true;
        }

        logger.info("Begin SOFABoot component readiness check.");

        boolean result = true;

        for (HealthChecker healthChecker : HealthCheckManager.getHealthCheckers()) {
            boolean resultItem = doCheckComponentHealth(healthChecker, true, null);
            if (!resultItem) {
                result = false;
            }
        }

        if (result) {
            logger.info("SOFABoot component readiness check result: success.");

        } else {
            logger.error("SOFABoot component readiness check result: failed.");
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

    private boolean doCheckComponentHealth(HealthChecker healthChecker, boolean isRetry,
                                           Map<String, Health> healthMap) {
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
                    logger.error(
                        "Exception occurred while sleeping of retry component readiness check.", e);
                }
                health = healthChecker.isHealthy();
                if (HealthCheckUtil.isHealth(health)) {
                    logger.info("component readiness check success. component name["
                                + componentName + "]; retry count[" + (i + 1) + "]");
                    break;
                } else {
                    logger.error("component readiness check failed. component name["
                                 + componentName + "]; retry count[" + (i + 1)
                                 + "]; fail details:[" + health.getDetails() + "]");
                }
            }

            if (retryCount == 0) {
                logger.error("component readiness check failed. component name[" + componentName
                             + "]; no retry; fail details:[" + health.getDetails() + "]");
            }
        } else {
            logger.info("component readiness check success. component name[" + componentName
                        + "]; no retry.");
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