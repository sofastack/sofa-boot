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
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link HealthCheckerProcessor}.
 * 
 * @author huzijie
 * @version HealthCheckerProcessorTests.java, v 0.1 2023年01月06日 11:07 AM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class HealthCheckerProcessorTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(HealthCheckerProcessor.class);
    }

    private final ExecutorService  executorService           = Executors.newSingleThreadExecutor();

    private final HealthChecker    successHealthChecker      = new SuccessHealthChecker();

    private final HealthChecker    failHealthChecker         = new FailHealthChecker();

    private final HealthChecker    exceptionHealthChecker    = new ExceptionHealthChecker();

    private final HealthChecker    timeoutHealthChecker      = new TimeoutHealthChecker();

    private final HealthChecker    nonReadinessHealthChecker = new NonReadinessHealthChecker();

    private final HealthChecker    retryHealthChecker        = new RetryHealthChecker();

    @InjectMocks
    private HealthCheckerProcessor healthCheckerProcessor;

    @Mock
    private ApplicationContext     applicationContext;

    @BeforeEach
    public void setUp() {
        healthCheckerProcessor.setGlobalTimeout(100);
        healthCheckerProcessor.setHealthCheckExecutor(executorService);
    }

    @Test
    public void applicationContextNull() {
        HealthCheckerProcessor healthCheckerProcessor = new HealthCheckerProcessor();
        healthCheckerProcessor.setHealthCheckExecutor(executorService);
        assertThatThrownBy(healthCheckerProcessor::init).hasMessage("Application must not be null");
    }

    @Test
    public void healthCheckExecutorNull() {
        HealthCheckerProcessor healthCheckerProcessor = new HealthCheckerProcessor();
        healthCheckerProcessor.setApplicationContext(applicationContext);
        assertThatThrownBy(healthCheckerProcessor::init).hasMessage("HealthCheckExecutor must not be null");
    }

    @Test
    public void readinessHealthCheckSuccess(CapturedOutput capturedOutput) {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        assertThat(capturedOutput.getOut()).contains(
            "Found 1 HealthChecker implementation:successHealthChecker");
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains("Begin SOFABoot HealthChecker readiness check");
        assertThat(capturedOutput.getOut()).contains(
            "SOFABoot HealthChecker readiness check 1 item: success");
        assertThat(capturedOutput.getOut()).contains(
            "HealthChecker [successHealthChecker] readiness check start");
        assertThat(capturedOutput.getOut()).contains(
            "SOFABoot HealthChecker readiness check result: success");
        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("successHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void readinessHealthCheckFailed(CapturedOutput capturedOutput) {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        beanMap.put("failHealthChecker", failHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();

        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-23001: HealthChecker[failHealthChecker] readiness check fail with 0 retry; fail details:{\"reason\":\"error\"}; strict mode:true");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-23000: SOFABoot HealthChecker readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("failHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{reason=error}");
    }

    @Test
    public void readinessHealthCheckException(CapturedOutput capturedOutput) {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        beanMap.put("exceptionHealthChecker", exceptionHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(capturedOutput.getOut())
            .contains(
                "Exception occurred while wait the result of HealthChecker[exceptionHealthChecker] readiness check");
        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-23001: HealthChecker[exceptionHealthChecker] readiness check fail with 0 retry; fail details:{\"error\":\"java.util.concurrent.ExecutionException: java.lang.RuntimeException: indicator exception\"}; strict mode:true");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-23000: SOFABoot HealthChecker readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("exceptionHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("indicator exception");
    }

    @Test
    public void readinessHealthCheckTimeout(CapturedOutput capturedOutput) {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        beanMap.put("timeoutHealthChecker", timeoutHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);
        assertThat(capturedOutput.getOut())
            .contains(
                "Timeout occurred while doing HealthChecker[timeoutHealthChecker] readiness check, the timeout value is: 100ms");
        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-23001: HealthChecker[timeoutHealthChecker] readiness check fail with 0 retry; fail details:{\"error\":\"java.util.concurrent.TimeoutException: null\",\"timeout\":100}; strict mode:true");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-23000: SOFABoot HealthChecker readiness check result: failed");

        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("timeoutHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(health.getDetails().toString())
            .contains("java.util.concurrent.TimeoutException");
        assertThat(health.getDetails().toString()).contains("timeout=100");
    }

    @Test
    public void noReadinessHealthCheck() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        beanMap.put("noReadinessHealthChecker", nonReadinessHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("noReadinessHealthChecker");
        assertThat(health).isNull();
    }

    @Test
    public void noStrictReadinessHealthCheckFailed() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("failHealthChecker", failHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        Map<String, HealthCheckerConfig> healthCheckerConfig = new HashMap<>();
        HealthCheckerConfig checkerConfig = new HealthCheckerConfig();
        checkerConfig.setStrictCheck(false);
        healthCheckerConfig.put("fail", checkerConfig);
        healthCheckerProcessor.init();
        healthCheckerProcessor.setHealthCheckerConfigs(healthCheckerConfig);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("failHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    public void updateGlobalTimeout() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("timeoutHealthChecker", timeoutHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        healthCheckerProcessor.setGlobalTimeout(200);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("timeoutHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void updateHealthCheckerConfig() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("timeoutHealthChecker", timeoutHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        Map<String, HealthCheckerConfig> healthCheckerConfig = new HashMap<>();
        HealthCheckerConfig checkerConfig = new HealthCheckerConfig();
        checkerConfig.setTimeout(200);
        healthCheckerConfig.put("timeout", checkerConfig);
        healthCheckerProcessor.init();
        healthCheckerProcessor.setHealthCheckerConfigs(healthCheckerConfig);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("timeoutHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void updateParallelCheckTimeout() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("successHealthChecker", successHealthChecker);
        beanMap.put("timeoutHealthChecker", timeoutHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        healthCheckerProcessor.init();
        healthCheckerProcessor.setParallelCheck(true);
        healthCheckerProcessor.setParallelCheckTimeout(50);
        healthCheckerProcessor.setGlobalTimeout(200);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isFalse();
        assertThat(healthMap.size()).isEqualTo(2);
        Health health = healthMap.get("parallelCheck");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{timeout=50}");
    }

    @Test
    public void updateRetryHealthCheck() {
        Map<String, HealthChecker> beanMap = new HashMap<>();
        beanMap.put("retryHealthChecker", retryHealthChecker);
        Mockito.doReturn(beanMap).when(applicationContext).getBeansOfType(HealthChecker.class);

        Map<String, HealthCheckerConfig> healthCheckerConfig = new HashMap<>();
        HealthCheckerConfig checkerConfig = new HealthCheckerConfig();
        checkerConfig.setRetryCount(10);
        checkerConfig.setRetryTimeInterval(10L);
        healthCheckerConfig.put("retry", checkerConfig);
        healthCheckerProcessor.init();
        healthCheckerProcessor.setHealthCheckerConfigs(healthCheckerConfig);
        HashMap<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);

        assertThat(result).isTrue();
        assertThat(healthMap.size()).isEqualTo(1);
        Health health = healthMap.get("retryHealthChecker");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(((RetryHealthChecker) retryHealthChecker).getCost() > 50L).isTrue();
    }

    static class SuccessHealthChecker implements HealthChecker {

        @Override
        public Health isHealthy() {
            return Health.up().build();
        }

        @Override
        public String getComponentName() {
            return "success";
        }
    }

    static class FailHealthChecker implements HealthChecker {

        @Override
        public Health isHealthy() {
            return Health.down().withDetail("reason", "error").build();
        }

        @Override
        public String getComponentName() {
            return "fail";
        }
    }

    static class ExceptionHealthChecker implements HealthChecker {

        @Override
        public Health isHealthy() {
            throw new RuntimeException("indicator exception");
        }

        @Override
        public String getComponentName() {
            return "exception";
        }
    }

    static class TimeoutHealthChecker implements HealthChecker {

        @Override
        public Health isHealthy() {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Health.up().build();
        }

        @Override
        public String getComponentName() {
            return "timeout";
        }
    }

    static class NonReadinessHealthChecker implements HealthChecker, NonReadinessCheck {

        @Override
        public Health isHealthy() {
            return Health.up().build();
        }

        @Override
        public String getComponentName() {
            return "nonReadiness";
        }
    }

    static class RetryHealthChecker implements HealthChecker {

        private int  retry;

        private long startTime;

        private long endTime;

        @Override
        public Health isHealthy() {
            if (retry == 0) {
                startTime = System.currentTimeMillis();
            }
            if (retry < 5) {
                retry++;
                return Health.down().build();
            } else {
                endTime = System.currentTimeMillis();
                return Health.up().build();
            }
        }

        public long getCost() {
            return endTime - startTime;
        }

        @Override
        public String getComponentName() {
            return "retry";
        }
    }
}
