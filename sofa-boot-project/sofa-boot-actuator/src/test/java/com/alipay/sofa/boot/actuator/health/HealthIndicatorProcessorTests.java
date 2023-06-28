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

import com.alipay.sofa.boot.util.LogOutPutUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link HealthIndicatorProcessorTests}.
 *
 * @author huzijie
 * @version HealthIndicatorProcessorTests.java, v 0.1 2023年01月05日 6:03 PM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class HealthIndicatorProcessorTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(HealthIndicatorProcessor.class);
    }

    private final ExecutorService         executorService          = Executors
                                                                       .newFixedThreadPool(10);

    private final HealthIndicator         successHealthIndicator   = new SuccessHealthIndicator();

    private final HealthIndicator         failHealthIndicator      = new FailHealthIndicator();

    private final HealthIndicator         exceptionHealthIndicator = new ExceptionHealthIndicator();

    private final HealthIndicator         timeoutHealthIndicator   = new TimeoutHealthIndicator();

    private final ReactiveHealthIndicator reactiveHealthIndicator  = new TestReactiveHealthIndicator();

    @InjectMocks
    private HealthIndicatorProcessor      healthIndicatorProcessor;

    @Mock
    private ApplicationContext            applicationContext;

    @BeforeEach
    public void setUp() {
        healthIndicatorProcessor.setGlobalTimeout(100);
        healthIndicatorProcessor.setHealthCheckExecutor(executorService);
    }

    @Test
    public void applicationContextNull() {
        HealthIndicatorProcessor healthIndicatorProcessor = new HealthIndicatorProcessor();
        healthIndicatorProcessor.setHealthCheckExecutor(executorService);
        assertThatThrownBy(healthIndicatorProcessor::init).hasMessage("Application must not be null");
    }

    @Test
    public void healthCheckExecutorNull() {
        HealthIndicatorProcessor healthIndicatorProcessor = new HealthIndicatorProcessor();
        healthIndicatorProcessor.setApplicationContext(applicationContext);
        assertThatThrownBy(healthIndicatorProcessor::init).hasMessage("HealthCheckExecutor must not be null");
    }

    @Test
    public void indicatorHealthCheckSuccess(CapturedOutput capturedOutput) {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        assertThat(capturedOutput.getOut()).contains(
            "Found 1 HealthIndicator implementation:successHealthIndicator");
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut()).contains(
            "Begin SOFABoot HealthIndicator readiness check");
        assertThat(capturedOutput.getOut()).contains(
            "SOFABoot HealthIndicator readiness check 1 item: successHealthIndicator");
        assertThat(capturedOutput.getOut()).contains(
            "HealthIndicator [successHealthIndicator] readiness check start");
        assertThat(capturedOutput.getOut()).contains(
            "SOFABoot HealthIndicator readiness check result: success");
        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("success");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void indicatorHealthCheckFailed(CapturedOutput capturedOutput) {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        beanMap.put("failHealthIndicator", failHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-21001: HealthIndicator[failHealthIndicator] readiness check fail; the status is: DOWN; the detail is: {\"reason\":\"error\"}");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-21000: SOFABoot HealthIndicator readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("fail");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{reason=error}");
    }

    @Test
    public void indicatorHealthCheckException(CapturedOutput capturedOutput) {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        beanMap.put("exceptionHealthIndicator", exceptionHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-21002: Error occurred while doing HealthIndicator[class com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessorTests$ExceptionHealthIndicator] readiness check");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-21000: SOFABoot HealthIndicator readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("exception");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("indicator exception");
    }

    @Test
    public void indicatorHealthCheckTimeout(CapturedOutput capturedOutput) {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        beanMap.put("timeoutHealthIndicator", timeoutHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains(
                "HealthIndicator[timeoutHealthIndicator] readiness check fail; the status is: UNKNOWN; the detail is: timeout, the timeout value is: 100ms");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-21000: SOFABoot HealthIndicator readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("timeout");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(health.getDetails().toString())
            .contains("java.util.concurrent.TimeoutException");
        assertThat(health.getDetails().toString()).contains("timeout=100");
    }

    @Test
    public void reactiveHealthIndicator() {
        Map<String, ReactiveHealthIndicator> beanMap = new HashMap<>();
        beanMap.put("reactiveHealthIndicator", reactiveHealthIndicator);
        Mockito.doReturn(new HashMap<>()).when(applicationContext)
            .getBeansOfType(HealthIndicator.class);
        Mockito.doReturn(beanMap).when(applicationContext)
            .getBeansOfType(ReactiveHealthIndicator.class);

        healthIndicatorProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("reactive");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().toString()).isEqualTo("{reactive=success}");
    }

    @Test
    public void excludeIndicators() {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        beanMap.put("failHealthIndicator", failHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        List<String> excludeList = new ArrayList<>();
        excludeList.add(failHealthIndicator.getClass().getName());
        healthIndicatorProcessor.initExcludedIndicators(excludeList);

        healthIndicatorProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("success");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void updateGlobalTimeout() {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("timeoutHealthIndicator", timeoutHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        healthIndicatorProcessor.setGlobalTimeout(200);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("timeout");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void updateHealthIndicatorConfig() {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("timeoutHealthIndicator", timeoutHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        Map<String, HealthCheckerConfig> healthIndicatorConfig = new HashMap<>();
        HealthCheckerConfig checkerConfig = new HealthCheckerConfig();
        checkerConfig.setTimeout(200);
        healthIndicatorConfig.put("timeoutHealthIndicator", checkerConfig);
        healthIndicatorProcessor.init();
        healthIndicatorProcessor.setHealthIndicatorConfig(healthIndicatorConfig);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("timeout");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void updateParallelCheckTimeout() {
        Map<String, HealthIndicator> beanMap = new HashMap<>();
        beanMap.put("successHealthIndicator", successHealthIndicator);
        beanMap.put("timeoutHealthIndicator", timeoutHealthIndicator);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthIndicator.class);

        healthIndicatorProcessor.init();
        healthIndicatorProcessor.setParallelCheck(true);
        healthIndicatorProcessor.setParallelCheckTimeout(50);
        healthIndicatorProcessor.setGlobalTimeout(200);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("parallelCheck");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{timeout=50}");
    }

    static class SuccessHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.up().build();
        }
    }

    static class FailHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.down().withDetail("reason", "error").build();
        }
    }

    static class ExceptionHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            throw new RuntimeException("indicator exception");
        }
    }

    static class TimeoutHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Health.up().build();
        }
    }

    static class TestReactiveHealthIndicator implements ReactiveHealthIndicator {

        @Override
        public Mono<Health> health() {
            return Mono.just(Health.up().withDetail("reactive", "success").build());
        }
    }
}
