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

import com.alipay.sofa.healthcheck.core.HealthCheckManager;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.Ordered;
import java.util.List;

/**
 * HealthCheckTrigger listens to ContextRefreshedEvent, SofaModuleContextRefreshedListener of isle-sofa-boot-starter should execute before this class.
 * In order to ensure this class execute at last, please don't implement ${@link PriorityOrdered} or ${@link Ordered} interface.
 *
 * Created by liangen on 17/8/4.
 */
@Component
public class HealthCheckTrigger implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = SofaBootHealthCheckLoggerFactory
                                     .getLogger(HealthCheckTrigger.class.getCanonicalName());

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        HealthCheckManager.init(applicationContext);

        logPrintCheckers();

        ReadinessCheckProcessor healthCheckStartupProcessor = new ReadinessCheckProcessor();
        healthCheckStartupProcessor.checkHealth();
    }

    private void logPrintCheckers() {
        List<HealthChecker> healthCheckers = HealthCheckManager.getHealthCheckers();
        List<HealthIndicator> healthIndicators = HealthCheckManager.getHealthIndicator();

        StringBuilder hcInfo = new StringBuilder();

        hcInfo.append("\nFound " + healthCheckers.size() + " SOFABoot component health checkers:")
            .append("\n");
        for (HealthChecker healthchecker : healthCheckers) {
            hcInfo.append(healthchecker.getClass()).append("\n");
        }

        hcInfo.append("Found " + healthIndicators.size() + " HealthIndicator checkers:").append(
            "\n");
        for (HealthIndicator healthIndicator : healthIndicators) {
            hcInfo.append(healthIndicator.getClass()).append("\n");
        }

        logger.info(hcInfo.toString());
    }
}
