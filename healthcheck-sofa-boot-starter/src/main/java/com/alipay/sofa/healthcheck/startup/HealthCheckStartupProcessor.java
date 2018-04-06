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
public class HealthCheckStartupProcessor {
    private static Logger                           logger                            = SofaBootHealthCheckLoggerFactory
                                                                                          .getLogger(HealthCheckStartupProcessor.class
                                                                                              .getCanonicalName());

    private final SpringContextCheckProcessor       springContextCheckProcessor       = new SpringContextCheckProcessor();

    private final ComponentCheckProcessor           componentCheckProcessor           = new ComponentCheckProcessor();

    private final HealthIndicatorCheckProcessor     healthIndicatorCheckProcessor     = new HealthIndicatorCheckProcessor();

    private final AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = new AfterHealthCheckCallbackProcessor();

    public void checkHealth() {

        //publish SofaBootBeforeHealthCheckEvent before the health check.
        publishBeforeHealthCheckEvent();

        boolean result = false;
        try {

            StartUpHealthCheckStatus.openStartStatu();

            //run the startup check
            if (startHealthCheckProcess()) {
                //run the runtime check
                result = operationHealthCheckProcess();
            }
        } finally {
            if (result) {
                logger.info("Startup health check result: success");
            } else {
                logger.error("Startup health check result: fail");
            }

            StartUpHealthCheckStatus.closeStartStatu();
        }
    }

    private boolean startHealthCheckProcess() {

        boolean result = true;
        try {
            //是否跳过所有健康检查
            if (skipAllCheck()) {
                logger.info("Skip the first phase of the startup health check");
                return true;
            }

            logger.info("Begin first phase of the startup health check");

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
                logger.info("first phase of the startup health check result: success");
            } else {
                logger.error("first phase of the startup health check result: fail");
            }
            return result;

        } catch (Throwable e) {

            logger.error("Unknown error occurred while doing first phase of the startup health check", e);
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
            logger.info("Begin second phase of the startup health check");

            if (!afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback()) {

                checkResult = false;

            }

            if (checkResult) {
                logger.info("second phase of the startup health check result: success");
            } else {
                logger.error("second phase of the startup health check result: fail");
            }
        } catch (Throwable e) {
            logger.error("Unknown error occurred while doing second phase of the startup health check", e);
            return false;
        }

        return checkResult;
    }

    private void publishBeforeHealthCheckEvent() {

        HealthCheckManager.publishEvent(new SofaBootBeforeHealthCheckEvent(HealthCheckManager.getApplicationContext()));

    }
}
