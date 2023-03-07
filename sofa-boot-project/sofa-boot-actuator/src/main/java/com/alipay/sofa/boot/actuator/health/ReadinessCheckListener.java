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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.boot.startup.StartupReporterAware;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
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
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Implementation of {@link GenericApplicationListener} to trigger readiness check.
 *
 * @author liangen
 * @author qilong.zql
 * @author huzijie
 */
public class ReadinessCheckListener implements ApplicationContextAware, Ordered,
                                   GenericApplicationListener, StartupReporterAware {

    private static final Logger                   logger                                    = SofaBootLoggerFactory
                                                                                                .getLogger(ReadinessCheckListener.class);

    public static final String                    READINESS_CHECK_STAGE                     = "ReadinessCheckStage";

    public static final String                    READINESS_HEALTH_CHECK_EXECUTOR_BEAN_NAME = "readinessHealthCheckExecutor";

    /**
     * health check not ready result key
     */
    public static final String                    HEALTH_CHECK_NOT_READY_KEY                = "HEALTH-CHECK-NOT-READY";

    /**
     * health check not ready result
     */
    public static final String                    HEALTH_CHECK_NOT_READY_MSG                = "App is still in startup process, please try later!";

    /**
     * processor for {@link HealthChecker}
     */
    private final HealthCheckerProcessor          healthCheckerProcessor;

    /**
     * processor for {@link org.springframework.boot.actuate.health.HealthIndicator}
     */
    private final HealthIndicatorProcessor        healthIndicatorProcessor;

    /**
     * processor for {@link ReadinessCheckCallback}
     */
    private final ReadinessCheckCallbackProcessor readinessCheckCallbackProcessor;

    /**
     * Check result for healthCheckerProcessor
     */
    private boolean                               healthCheckerStatus                       = true;

    /**
     * Check result details for healthCheckerProcessor
     */
    private final Map<String, Health>             healthCheckerDetails                      = new HashMap<>();

    /**
     * Check result for healthIndicatorProcessor
     */
    private boolean                               healthIndicatorStatus                     = true;

    /**
     * Check result for healthIndicatorProcessor
     */
    private final Map<String, Health>             healthIndicatorDetails                    = new HashMap<>();

    /**
     * Check result for readinessCheckCallbackProcessor
     */
    private boolean                               healthCallbackStatus                      = true;

    /**
     * Check result details for readinessCheckCallbackProcessor
     */
    private final Map<String, Health>             healthCallbackDetails                     = new HashMap<>();

    /**
     * ReadinessCheckCallbackProcessor trigger status
     */
    private final AtomicBoolean                   readinessCallbackTriggered                = new AtomicBoolean(
                                                                                                false);

    /**
     * Readiness check finish status
     */
    private boolean                               readinessCheckFinish                      = false;

    /**
     * Readiness check result
     */
    private ReadinessState                        readinessState;

    private ApplicationContext                    applicationContext;

    private StartupReporter                       startupReporter;

    private boolean                               skipAll                                   = false;

    private boolean                               skipHealthChecker                         = false;

    private boolean                               skipHealthIndicator                       = false;

    private boolean                               manualReadinessCallback                   = false;

    private boolean                               throwExceptionWhenHealthCheckFailed       = false;

    private ThreadPoolExecutor                    healthCheckExecutor;

    public ReadinessCheckListener(HealthCheckerProcessor healthCheckerProcessor,
                                  HealthIndicatorProcessor healthIndicatorProcessor,
                                  ReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor) {
        this.healthCheckerProcessor = healthCheckerProcessor;
        this.healthIndicatorProcessor = healthIndicatorProcessor;
        this.readinessCheckCallbackProcessor = afterReadinessCheckCallbackProcessor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setStartupReporter(StartupReporter startupReporter) throws BeansException {
        this.startupReporter = startupReporter;
    }

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
        return LOWEST_PRECEDENCE - 1;
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
            readinessCheckCallbackProcessor.init();

            // start collect startup stats
            ChildrenStat<BaseStat> stat = new ChildrenStat<>();
            stat.setName(READINESS_CHECK_STAGE);
            stat.setStartTime(System.currentTimeMillis());

            readinessHealthCheck();

            // end collect startup stats
            stat.setEndTime(System.currentTimeMillis());
            List<BaseStat> baseStatList = new ArrayList<>();

            // add healthCheckerStats
            List<BaseStat> healthCheckerStats = healthCheckerProcessor
                .getHealthCheckerStartupStatList();
            baseStatList.addAll(healthCheckerStats);
            healthCheckerStats.clear();

            // add healthIndicatorStats
            List<BaseStat> healthIndicatorStats = healthIndicatorProcessor
                .getHealthIndicatorStartupStatList();
            baseStatList.addAll(healthIndicatorStats);
            healthIndicatorStats.clear();

            // add readinessCheckCallbackStartupStats
            List<BaseStat> readinessCheckCallbackStartupStats = readinessCheckCallbackProcessor
                .getReadinessCheckCallbackStartupStatList();
            baseStatList.addAll(readinessCheckCallbackStartupStats);
            readinessCheckCallbackStartupStats.clear();

            stat.setChildren(baseStatList);
            if (startupReporter != null) {
                startupReporter.addCommonStartupStat(stat);
            }
            healthCheckExecutor.shutdown();
        }
    }

    public void onAvailabilityChangeEvent(AvailabilityChangeEvent<?> event) {
        if (isReadinessCheckFinish()) {
            Object payload = event.getPayload();
            if (payload instanceof ReadinessState && payload != readinessState) {
                // If readinessCheck is performed normally, readinessState won't be null
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
        Assert.notNull(applicationContext, () -> "Application must not be null");
        if (isSkipAll()) {
            logger.warn("Skip all readiness health check.");
        } else {
            if (isSkipHealthChecker()) {
                logger.warn("Skip HealthChecker health check.");
            } else {
                healthCheckerStatus = healthCheckerProcessor
                    .readinessHealthCheck(healthCheckerDetails);
            }
            if (isSkipHealthIndicator()) {
                logger.warn("Skip HealthIndicator health check.");
            } else {
                healthIndicatorStatus = healthIndicatorProcessor
                    .readinessHealthCheck(healthIndicatorDetails);
            }
        }

        if (manualReadinessCallback) {
            logger
                .info("Manual readiness callback is set to true, skip normal readiness callback. "
                      + "You can trigger all readiness callbacks through URL: actuator/triggerReadinessCallback");
        } else {
            if (healthCheckerStatus && healthIndicatorStatus) {
                readinessCallbackTriggered.set(true);
                logger.info("Invoking all readiness check callbacks...");
                healthCallbackStatus = readinessCheckCallbackProcessor
                    .readinessCheckCallback(healthCallbackDetails);
            }
        }
        determineReadinessState();
        readinessCheckFinish = true;
    }

    // After invoking readiness callbacks, we will determine readinessState again to include healthCallbackStatus
    public ManualReadinessCallbackResult triggerReadinessCallback() {
        if (healthCheckerStatus && healthIndicatorStatus) {
            if (readinessCallbackTriggered.compareAndSet(false, true)) {
                logger.info("Invoking all readiness callbacks...");
                healthCallbackStatus = readinessCheckCallbackProcessor
                    .readinessCheckCallback(healthCallbackDetails);
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
            logger.error(ErrorCode.convert("01-20000"));
            if (throwExceptionWhenHealthCheckFailed) {
                throw new HealthCheckException(
                    "Application health check is failed and health check insulator switch is turned on!");
            }
        }
        AvailabilityChangeEvent.publish(applicationContext, readinessState);
    }

    public Health aggregateReadinessHealth() {
        if (!isReadinessCheckFinish()) {
            return Health.unknown().withDetail(HEALTH_CHECK_NOT_READY_KEY,
                    HEALTH_CHECK_NOT_READY_MSG).build();
        }
        Map<String, Health> healths = new HashMap<>();
        // 聚合 HealthChecker 的结果
        boolean healthCheckerStatus = getHealthCheckerStatus();
        Health healthCheckerResult;
        if (healthCheckerStatus) {
            healthCheckerResult = Health.up().withDetails(getHealthCheckerDetails()).build();
        } else {
            healthCheckerResult = Health.down().withDetails(getHealthCheckerDetails()).build();
        }
        healths.put("HealthCheckerInfo", healthCheckerResult);

        // 聚合 HealthChecker 的结果
        Health healthIndicatorResult;
        boolean healthIndicatorStatus = getHealthIndicatorStatus();
        if (healthIndicatorStatus) {
            healthIndicatorResult = Health.up().withDetails(getHealthIndicatorDetails()).build();
        } else {
            healthIndicatorResult = Health.down().withDetails(getHealthIndicatorDetails()).build();
        }
        healths.put("HealthIndicatorInfo", healthIndicatorResult);


        // 聚合 ReadinessCallBack 的结果
        Health healthCallBackResult;
        boolean healthCallBackStatus = getHealthCallbackStatus();
        if (healthCallBackStatus) {
            healthCallBackResult = Health.up().withDetails(getHealthCallbackDetails()).build();
        } else {
            healthCallBackResult = Health.down().withDetails(getHealthCheckerDetails()).build();
        }
        healths.put("HealthCallBackInfo", healthCallBackResult);
        Status overallStatus = StatusAggregator.getDefault().getAggregateStatus(
                healths.values().stream().map(Health::getStatus).collect(Collectors.toSet()));
        return new Health.Builder(overallStatus, healths).build();
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

    public boolean isSkipAll() {
        return skipAll;
    }

    public void setSkipAll(boolean skipAll) {
        this.skipAll = skipAll;
    }

    public boolean isSkipHealthChecker() {
        return skipHealthChecker;
    }

    public void setSkipHealthChecker(boolean skipHealthChecker) {
        this.skipHealthChecker = skipHealthChecker;
    }

    public boolean isSkipHealthIndicator() {
        return skipHealthIndicator;
    }

    public void setSkipHealthIndicator(boolean skipHealthIndicator) {
        this.skipHealthIndicator = skipHealthIndicator;
    }

    public boolean isManualReadinessCallback() {
        return manualReadinessCallback;
    }

    public void setManualReadinessCallback(boolean manualReadinessCallback) {
        this.manualReadinessCallback = manualReadinessCallback;
    }

    public boolean isThrowExceptionWhenHealthCheckFailed() {
        return throwExceptionWhenHealthCheckFailed;
    }

    public void setThrowExceptionWhenHealthCheckFailed(boolean throwExceptionWhenHealthCheckFailed) {
        this.throwExceptionWhenHealthCheckFailed = throwExceptionWhenHealthCheckFailed;
    }

    public boolean isHealthCheckerStatus() {
        return healthCheckerStatus;
    }

    public boolean isHealthIndicatorStatus() {
        return healthIndicatorStatus;
    }

    public boolean isHealthCallbackStatus() {
        return healthCallbackStatus;
    }

    public ReadinessState getReadinessState() {
        return readinessState;
    }

    public void setHealthCheckExecutor(ThreadPoolExecutor healthCheckExecutor) {
        this.healthCheckExecutor = healthCheckExecutor;
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
