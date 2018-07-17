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
package com.alipay.sofa.healthcheck.startup;

import com.alipay.sofa.healthcheck.configuration.HealthCheckConfiguration;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.core.*;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

/**
 *
 * Health check start checker.
 * Created by liangen on 17/8/5.
 */
public class ReadinessCheckProcessor {
    private static Logger                           logger                            = SofaBootHealthCheckLoggerFactory
                                                                                          .getLogger(ReadinessCheckProcessor.class);

    private final SpringContextCheckProcessor       springContextCheckProcessor       = new SpringContextCheckProcessor();

    private final ComponentCheckProcessor           componentCheckProcessor           = new ComponentCheckProcessor();

    private final HealthIndicatorCheckProcessor     healthIndicatorCheckProcessor     = new HealthIndicatorCheckProcessor();

    private final AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = new AfterHealthCheckCallbackProcessor();

    public void checkHealth() {

        //publish SofaBootBeforeReadinessCheckEvent before the health check.
        publishBeforeHealthCheckEvent();

        boolean result = false;
        try {
            StartUpHealthCheckStatus.openStartStatus();
            //run the startup check
            if (startHealthCheckProcess()) {
                //run the runtime check
                result = operationHealthCheckProcess();
            }
        } finally {
            if (result) {
                logger.info("Readiness check result: success");
            } else {
                logger.error("Readiness check result: fail");
            }
            StartUpHealthCheckStatus.closeStartStatus();
        }
    }

    private boolean startHealthCheckProcess() {
        boolean result = true;
        try {
            //是否跳过所有健康检查
            if (skipAllCheck()) {
                logger.info("Skip the first phase of the readiness check");
                return true;
            }

            logger.info("Begin first phase of the readiness check");

            if (!springContextCheckProcessor.springContextCheck()) {
                result = false;
            }

            if (!componentCheckProcessor.startupCheckComponent()) {
                result = false;
            }

            if (!healthIndicatorCheckProcessor.checkIndicator()) {
                result = false;
            }

            if (result) {
                logger.info("first phase of the readiness check result: success");
            } else {
                logger.error("first phase of the readiness check result: fail");
            }
            return result;
        } catch (Throwable e) {
            logger
                .error("Unknown error occurred while doing first phase of the readiness check", e);
            return false;
        }
    }

    private boolean skipAllCheck() {
        String skipAllCheck = HealthCheckConfiguration
            .getPropertyAllCircumstances(HealthCheckConfigurationConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK);

        if (!StringUtils.hasText(skipAllCheck)) {
            return false;
        }

        return "true".equalsIgnoreCase(skipAllCheck);
    }

    /**
     * run the runtime check
     * @return
     */
    private boolean operationHealthCheckProcess() {
        boolean checkResult = true;

        try {
            logger.info("Begin second phase of the readiness check");

            if (!afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback()) {
                checkResult = false;
            }

            if (checkResult) {
                logger.info("second phase of the readiness check result: success");
            } else {
                logger.error("second phase of the readiness check result: fail");
            }
        } catch (Throwable e) {
            logger.error("Unknown error occurred while doing second phase of the readiness check",
                e);
            return false;
        }

        return checkResult;
    }

    private void publishBeforeHealthCheckEvent() {
        HealthCheckManager.publishEvent(new SofaBootBeforeReadinessCheckEvent(HealthCheckManager
            .getApplicationContext()));

    }
}
