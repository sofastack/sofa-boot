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

import com.alipay.sofa.healthcheck.core.HealthCheckManager;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangen on 17/8/4.
 */
@Component
public class HealthCheckTrigger implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = SofaBootHealthCheckLoggerFactory.getLogger(HealthCheckTrigger.class
                                     .getCanonicalName());

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        HealthCheckManager.init(applicationContext);

        logPrintCheckers();

        HealthCheckStartupProcessor healthCheckStartupProcessor = new HealthCheckStartupProcessor();
        healthCheckStartupProcessor.checkHealth();

    }

    private void logPrintCheckers() {
        List<HealthChecker> healthCheckers = HealthCheckManager.getHealthCheckers();
        List<HealthIndicator> healthIndicators = HealthCheckManager.getHealthIndicator();

        StringBuilder hcInfo = new StringBuilder();

        hcInfo.append("\nFound " + healthCheckers.size() + " component health checkers:")
            .append("\n");
        for (HealthChecker healthchecker : healthCheckers) {
            hcInfo.append(healthchecker.getClass())
                .append("\n");
        }

        hcInfo.append("Found " + healthIndicators.size() + " indicator health checkers:")
            .append("\n");
        for (HealthIndicator healthIndicator : healthIndicators) {
            hcInfo.append(healthIndicator.getClass())
                .append("\n");
        }

        logger.info(hcInfo.toString());
    }
}
