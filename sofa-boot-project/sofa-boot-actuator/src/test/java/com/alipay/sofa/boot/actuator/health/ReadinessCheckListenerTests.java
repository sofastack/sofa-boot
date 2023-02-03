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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;

/**
 * Tests for {@link ReadinessCheckListener}.
 *
 * @author huzijie
 * @version ReadinessCheckListenerTests.java, v 0.1 2023年01月06日 1:20 PM huzijie Exp $
 */
//todo 补充初始化及check结果的日志校验
@ExtendWith(MockitoExtension.class)
public class ReadinessCheckListenerTests {

    @InjectMocks
    private ReadinessCheckListener          readinessCheckListener;

    @Mock
    private HealthCheckerProcessor          healthCheckerProcessor;

    @Mock
    private HealthIndicatorProcessor        healthIndicatorProcessor;

    @Mock
    private ReadinessCheckCallbackProcessor readinessCheckCallbackProcessor;

    @Mock
    private ApplicationContext              applicationContext;

    @Test
    public void applicationContextNull() {
        ReadinessCheckListener readinessCheckListener = new ReadinessCheckListener(null, null, null);
        assertThatThrownBy(readinessCheckListener::readinessHealthCheck).hasMessage("Application must not be null");
    }

    @Test
    public void allAllHealthCheckSuccess() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.readinessHealthCheck();

        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();

        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().toString()).contains("HealthCheckerInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthIndicatorInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthCallBackInfo=UP");
    }

    @Test
    public void healthCheckerFail() {
        Mockito.doReturn(false).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isFalse();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isFalse();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.REFUSING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();

        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("HealthCheckerInfo=DOWN");
        assertThat(health.getDetails().toString()).contains("HealthIndicatorInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthCallBackInfo=UP");
    }

    @Test
    public void healthIndicatorFail() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(false).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isFalse();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isFalse();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.REFUSING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();

        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("HealthCheckerInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthIndicatorInfo=DOWN");
        assertThat(health.getDetails().toString()).contains("HealthCallBackInfo=UP");
    }

    @Test
    public void healthCallbackFail() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(false).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.readinessHealthCheck();

        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isFalse();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.REFUSING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();

        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("HealthCheckerInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthIndicatorInfo=UP");
        assertThat(health.getDetails().toString()).contains("HealthCallBackInfo=DOWN");
    }

    @Test
    public void skipAll() {
        Mockito.doReturn(true).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setSkipAll(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(healthCheckerProcessor, never()).readinessHealthCheck(anyMap());
        Mockito.verify(healthIndicatorProcessor, never()).readinessHealthCheck(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();
    }

    @Test
    public void skipHealthChecker() {
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setSkipHealthChecker(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(healthCheckerProcessor, never()).readinessHealthCheck(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();
    }

    @Test
    public void skipHealthIndicator() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setSkipHealthIndicator(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(healthIndicatorProcessor, never()).readinessHealthCheck(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();
    }

    @Test
    public void throwExceptionWhenHealthCheckFailed() {
        Mockito.doReturn(false).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(true);
        assertThatThrownBy(() -> readinessCheckListener.readinessHealthCheck())
                .isInstanceOf(HealthCheckException.class)
                .hasMessage("Application health check is failed and health check insulator switch is turned on!");
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isFalse();
    }

    @Test
    public void manualSuccess() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setManualReadinessCallback(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());
        assertThat(readinessCheckListener.getHealthCheckerStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthIndicatorStatus()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isFalse();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();

        ReadinessCheckListener.ManualReadinessCallbackResult manualReadinessCallbackResult = readinessCheckListener
            .triggerReadinessCallback();

        Mockito.verify(readinessCheckCallbackProcessor, only()).readinessCheckCallback(anyMap());
        assertThat(manualReadinessCallbackResult).isNotNull();
        assertThat(manualReadinessCallbackResult.isSuccess()).isTrue();
        assertThat(manualReadinessCallbackResult.getDetails()).contains(
            "Readiness callbacks invoked successfully with result: true");
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);

        manualReadinessCallbackResult = readinessCheckListener.triggerReadinessCallback();
        Mockito.verify(readinessCheckCallbackProcessor, only()).readinessCheckCallback(anyMap());
        assertThat(manualReadinessCallbackResult).isNotNull();
        assertThat(manualReadinessCallbackResult.isSuccess()).isFalse();
        assertThat(manualReadinessCallbackResult.getDetails()).contains(
            "Readiness callbacks are already triggered");
    }

    @Test
    public void manualHealthCheckFail() {
        Mockito.doReturn(false).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setManualReadinessCallback(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());

        ReadinessCheckListener.ManualReadinessCallbackResult manualReadinessCallbackResult = readinessCheckListener
            .triggerReadinessCallback();

        assertThat(manualReadinessCallbackResult).isNotNull();
        assertThat(manualReadinessCallbackResult.isSuccess()).isFalse();
        assertThat(manualReadinessCallbackResult.getDetails()).contains(
            "Health checker or indicator failed");
        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isFalse();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isTrue();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.REFUSING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();
    }

    @Test
    public void manualReadinessCallbackFail() {
        Mockito.doReturn(true).when(healthCheckerProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(true).when(healthIndicatorProcessor).readinessHealthCheck(anyMap());
        Mockito.doReturn(false).when(readinessCheckCallbackProcessor)
            .readinessCheckCallback(anyMap());
        readinessCheckListener.setApplicationContext(applicationContext);
        readinessCheckListener.setManualReadinessCallback(true);
        readinessCheckListener.readinessHealthCheck();

        Mockito.verify(readinessCheckCallbackProcessor, never()).readinessCheckCallback(anyMap());

        ReadinessCheckListener.ManualReadinessCallbackResult manualReadinessCallbackResult = readinessCheckListener
            .triggerReadinessCallback();

        assertThat(manualReadinessCallbackResult).isNotNull();
        assertThat(manualReadinessCallbackResult.isSuccess()).isTrue();
        assertThat(manualReadinessCallbackResult.getDetails()).contains(
            "Readiness callbacks invoked successfully with result: false");
        Mockito.verify(readinessCheckCallbackProcessor, only()).readinessCheckCallback(anyMap());
        assertThat(readinessCheckListener.getReadinessCallbackTriggered()).isTrue();
        assertThat(readinessCheckListener.getHealthCallbackStatus()).isFalse();
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.REFUSING_TRAFFIC);
        assertThat(readinessCheckListener.isReadinessCheckFinish()).isTrue();
    }

    @Test
    public void aggregateReadinessHealthNotReady() {
        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(health.getDetails().toString()).isEqualTo(
            "{HEALTH-CHECK-NOT-READY=App is still in startup process, please try later!}");
    }

    @Test
    public void aggregateReadinessHealthDetails() {
        ReadinessCheckListener readinessCheckListener = new ReadinessCheckListener(null, null, null);
        readinessCheckListener = Mockito.spy(readinessCheckListener);
        Mockito.doReturn(true).when(readinessCheckListener).isReadinessCheckFinish();

        Map<String, Health> healthCheckDetails = new HashMap<>();
        healthCheckDetails
            .put("healthChecker", Health.up().withDetail("reason", "success").build());
        Mockito.doReturn(true).when(readinessCheckListener).getHealthCheckerStatus();
        Mockito.doReturn(healthCheckDetails).when(readinessCheckListener).getHealthCheckerDetails();

        Map<String, Health> healthIndicatorDetails = new HashMap<>();
        healthIndicatorDetails.put("healthIndicator", Health.up().withDetail("reason", "success")
            .build());
        Mockito.doReturn(true).when(readinessCheckListener).getHealthIndicatorStatus();
        Mockito.doReturn(healthIndicatorDetails).when(readinessCheckListener)
            .getHealthIndicatorDetails();

        Map<String, Health> healthCallBackDetails = new HashMap<>();
        healthCallBackDetails.put("healthCallBack", Health.up().withDetail("reason", "success")
            .build());
        Mockito.doReturn(true).when(readinessCheckListener).getHealthCallbackStatus();
        Mockito.doReturn(healthCallBackDetails).when(readinessCheckListener)
            .getHealthCallbackDetails();

        Health health = readinessCheckListener.aggregateReadinessHealth();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().toString()).contains(
            "HealthCheckerInfo=UP {healthChecker=UP {reason=success}");
        assertThat(health.getDetails().toString()).contains(
            "HealthIndicatorInfo=UP {healthIndicator=UP {reason=success}");
        assertThat(health.getDetails().toString()).contains(
            "HealthCallBackInfo=UP {healthCallBack=UP {reason=success}");
    }
}
