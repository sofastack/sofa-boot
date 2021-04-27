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
package com.alipay.sofa.healthcheck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;

/**
 * Health check start checker.
 * @author liangen
 * @author qilong.zql
 */
public class ReadinessCheckListener implements ApplicationContextAware, Ordered,
                                   GenericApplicationListener {
    private static Logger                        logger                     = HealthCheckLoggerFactory
                                                                                .getLogger(ReadinessCheckListener.class);

    private final StatusAggregator               statusAggregator           = StatusAggregator
                                                                                .getDefault();

    protected ApplicationContext                 applicationContext;

    @Autowired
    private Environment                          environment;

    @Autowired
    private HealthCheckerProcessor               healthCheckerProcessor;

    @Autowired
    private HealthIndicatorProcessor             healthIndicatorProcessor;

    @Autowired
    private AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor;

    @Autowired
    private SofaRuntimeConfigurationProperties   sofaRuntimeConfigurationProperties;

    @Autowired
    private HealthCheckProperties                healthCheckProperties;

    private boolean                              healthCheckerStatus        = true;

    private Map<String, Health>                  healthCheckerDetails       = new HashMap<>();

    private boolean                              healthIndicatorStatus      = true;

    private Map<String, Health>                  healthIndicatorDetails     = new HashMap<>();

    private boolean                              healthCallbackStatus       = true;
    private boolean                              readinessCheckFinish       = false;
    private AtomicBoolean                        readinessCallbackTriggered = new AtomicBoolean(
                                                                                false);

    private ReadinessState                       readinessState;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    private Map<String, Health> healthCallbackDetails = new HashMap<>();

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        Class<?> eventClass = eventType.getRawClass();
        if (eventClass == null) {
            return false;
        }

        return ContextRefreshedEvent.class.isAssignableFrom(eventClass)
               || AvailabilityChangeEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshedEvent((ContextRefreshedEvent) event);
        } else if (event instanceof AvailabilityChangeEvent) {
            onAvailabilityChangeEvent((AvailabilityChangeEvent<?>) event);
        }
    }

    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            healthCheckerProcessor.init();
            healthIndicatorProcessor.init();
            afterReadinessCheckCallbackProcessor.init();
            readinessHealthCheck();
            readinessCheckFinish = true;
        }
    }

    public void onAvailabilityChangeEvent(AvailabilityChangeEvent<?> event) {
        if (isReadinessCheckFinish()) {
            Object payload = event.getPayload();
            if (payload instanceof ReadinessState && payload != readinessState) {
                // If readinessCheck if performed normally, readinessState won't be null
                // it means application is in critical state if it is
                if (readinessState == null) {
                    AvailabilityChangeEvent.publish(applicationContext,
                        ReadinessState.REFUSING_TRAFFIC);
                } else {
                    AvailabilityChangeEvent.publish(applicationContext, readinessState);
                }
            }
        }
    }

    /**
     * Do readiness health check.
     */
    public void readinessHealthCheck() {
        if (skipAllCheck()) {
            logger.warn("Skip all readiness health check.");
        } else {
            if (skipComponent()) {
                logger.warn("Skip HealthChecker health check.");
            } else {
                healthCheckerStatus = healthCheckerProcessor
                    .readinessHealthCheck(healthCheckerDetails);
            }
            if (skipIndicator()) {
                logger.warn("Skip HealthIndicator health check.");
            } else {
                healthIndicatorStatus = healthIndicatorProcessor
                    .readinessHealthCheck(healthIndicatorDetails);
            }
        }

        if (sofaRuntimeConfigurationProperties.isManualReadinessCallback()) {
            logger
                .info("Manual readiness callback is set to true, skip normal readiness callback. "
                      + "You can trigger all readiness callbacks through URL: actuator/triggerReadinessCallback");
        } else {
            if (healthCheckerStatus && healthIndicatorStatus) {
                readinessCallbackTriggered.set(true);
                logger.info("Invoking all readiness check callbacks...");
                healthCallbackStatus = afterReadinessCheckCallbackProcessor
                    .afterReadinessCheckCallback(healthCallbackDetails);
            }
        }
        determineReadinessState();
    }

    // After invoking readiness callbacks, we will determine readinessState again to include healthCallbackStatus
    public ManualReadinessCallbackResult triggerReadinessCallback() {
        if (healthCheckerStatus && healthIndicatorStatus) {
            if (readinessCallbackTriggered.compareAndSet(false, true)) {
                logger.info("Invoking all readiness callbacks...");
                healthCallbackStatus = afterReadinessCheckCallbackProcessor
                    .afterReadinessCheckCallback(healthCallbackDetails);
                determineReadinessState();
                return new ManualReadinessCallbackResult(true,
                    "Readiness callbacks invoked successfully with result: " + healthCallbackStatus);
            } else {
                logger.warn("Readiness callbacks are already triggered! Action skipped.");
                return ManualReadinessCallbackResult.SKIPPED;
            }
        } else {
            logger.warn("Health checker or indicator failed, skip all readiness callbacks!");
            return ManualReadinessCallbackResult.STAGE_ONE_FAILED;
        }
    }

    private void determineReadinessState() {
        if (healthCheckerStatus && healthIndicatorStatus && healthCallbackStatus) {
            readinessState = ReadinessState.ACCEPTING_TRAFFIC;
            logger.info("Readiness check result: success");
        } else {
            readinessState = ReadinessState.REFUSING_TRAFFIC;
            logger.error("Readiness check result: fail");
            if (healthCheckProperties.isHealthCheckInsulator()) {
                throw new HealthCheckException(
                    "Application health check is failed and health check insulator switch is turned on!");
            }
        }
        AvailabilityChangeEvent.publish(applicationContext, readinessState);
    }

    public Health aggregateReadinessHealth() {
        Map<String, Health> healths = new HashMap<>();
        if (!isReadinessCheckFinish()) {
            healths.put(
                SofaBootConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_KEY,
                Health
                    .unknown()
                    .withDetail(SofaBootConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_KEY,
                        SofaBootConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_MSG).build());
        } else {
            boolean healthCheckerStatus = getHealthCheckerStatus();
            Map<String, Health> healthCheckerDetails = getHealthCheckerDetails();
            Map<String, Health> healthIndicatorDetails = getHealthIndicatorDetails();

            boolean afterReadinessCheckCallbackStatus = getHealthCallbackStatus();
            Map<String, Health> afterReadinessCheckCallbackDetails = getHealthCallbackDetails();

            Health.Builder builder;
            if (healthCheckerStatus && afterReadinessCheckCallbackStatus) {
                builder = Health.up();
            } else {
                builder = Health.down();
            }
            if (!CollectionUtils.isEmpty(healthCheckerDetails)) {
                builder = builder.withDetail("HealthChecker", healthCheckerDetails);
            }
            if (!CollectionUtils.isEmpty(afterReadinessCheckCallbackDetails)) {
                builder = builder.withDetail("ReadinessCheckCallback",
                    afterReadinessCheckCallbackDetails);
            }
            healths.put("SOFABootReadinessHealthCheckInfo", builder.build());

            // HealthIndicator
            healths.putAll(healthIndicatorDetails);
        }
        Status overallStatus = this.statusAggregator.getAggregateStatus(
                healths.values().stream().map(Health::getStatus).collect(Collectors.toSet()));
        return new Health.Builder(overallStatus, healths).build();
    }

    public boolean skipAllCheck() {
        String skipAllCheck = environment
            .getProperty(SofaBootConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK);
        return StringUtils.hasText(skipAllCheck) && "true".equalsIgnoreCase(skipAllCheck);
    }

    public boolean skipComponent() {
        String skipComponent = environment
            .getProperty(SofaBootConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK);
        return StringUtils.hasText(skipComponent) && "true".equalsIgnoreCase(skipComponent);
    }

    public boolean skipIndicator() {
        String skipIndicator = environment
            .getProperty(SofaBootConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK);
        return StringUtils.hasText(skipIndicator) && "true".equalsIgnoreCase(skipIndicator);
    }

    public boolean getHealthCheckerStatus() {
        return healthCheckerStatus;
    }

    public Map<String, Health> getHealthCheckerDetails() {
        return healthCheckerDetails;
    }

    public boolean getHealthIndicatorStatus() {
        return healthIndicatorStatus;
    }

    public Map<String, Health> getHealthIndicatorDetails() {
        return healthIndicatorDetails;
    }

    public boolean getHealthCallbackStatus() {
        return healthCallbackStatus;
    }

    public Map<String, Health> getHealthCallbackDetails() {
        return healthCallbackDetails;
    }

    public boolean isReadinessCheckFinish() {
        return readinessCheckFinish;
    }

    public AtomicBoolean getReadinessCallbackTriggered() {
        return readinessCallbackTriggered;
    }

    public static class ManualReadinessCallbackResult {
        public static ManualReadinessCallbackResult STAGE_ONE_FAILED = new ManualReadinessCallbackResult(
                                                                         false,
                                                                         "Health checker or indicator failed.");
        public static ManualReadinessCallbackResult SKIPPED          = new ManualReadinessCallbackResult(
                                                                         false,
                                                                         "Readiness callbacks are already triggered.");

        private boolean                             success;
        private String                              details;

        public ManualReadinessCallbackResult() {
        }

        public ManualReadinessCallbackResult(boolean success, String details) {
            this.success = success;
            this.details = details;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
