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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ReadinessCheckCallbackProcessor}.
 *
 * @author huzijie
 * @version ReadinessCheckCallbackProcessorTest.java, v 0.1 2023年01月05日 6:12 PM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class ReadinessCheckCallbackProcessorTest {

    static {
        LogOutPutUtils.openOutPutForLoggers(ReadinessCheckCallbackProcessor.class);
    }

    private final ReadinessCheckCallback    successCheckCallBack   = new SuccessReadinessCheckCallback();

    private final ReadinessCheckCallback    failCheckCallBack      = new FailReadinessCheckCallback();

    private final ReadinessCheckCallback    exceptionCheckCallBack = new ExceptionReadinessCheckCallback();

    @InjectMocks
    private ReadinessCheckCallbackProcessor readinessCheckCallbackProcessor;

    @Mock
    private ApplicationContext              applicationContext;

    @Test
    public void applicationContextNull() {
        assertThatThrownBy(() -> new ReadinessCheckCallbackProcessor().init()).hasMessage("Application must not be null");
    }

    @Test
    public void readinessCheckCallbackSuccess(CapturedOutput capturedOutput) {
        Map<String, ReadinessCheckCallback> beanMap = new HashMap<>();
        beanMap.put("successCheckCallBack", successCheckCallBack);
        Mockito.doReturn(beanMap).when(applicationContext)
            .getBeansOfType(ReadinessCheckCallback.class);

        readinessCheckCallbackProcessor.init();

        assertThat(capturedOutput.getOut()).contains(
            "Found 1 ReadinessCheckCallback implementation: successCheckCallBack");
        HashMap<String, Health> callbackDetails = new HashMap<>();
        boolean result = readinessCheckCallbackProcessor.readinessCheckCallback(callbackDetails);

        assertThat(capturedOutput.getOut())
            .contains("Begin ReadinessCheckCallback readiness check");
        assertThat(capturedOutput.getOut()).contains(
            "ReadinessCheckCallback [successCheckCallBack] check start");
        assertThat(capturedOutput.getOut()).contains(
            "ReadinessCheckCallback readiness check result: success");
        assertThat(result).isTrue();
        assertThat(callbackDetails.size()).isEqualTo(1);
        Health health = callbackDetails.get("successCheckCallBack");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void readinessCheckCallbackFailed(CapturedOutput capturedOutput) {
        Map<String, ReadinessCheckCallback> beanMap = new HashMap<>();
        beanMap.put("successCheckCallBack", successCheckCallBack);
        beanMap.put("failCheckCallBack", failCheckCallBack);
        Mockito.doReturn(beanMap).when(applicationContext)
            .getBeansOfType(ReadinessCheckCallback.class);

        readinessCheckCallbackProcessor.init();
        HashMap<String, Health> callbackDetails = new HashMap<>();
        boolean result = readinessCheckCallbackProcessor.readinessCheckCallback(callbackDetails);

        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-24001: SOFABoot ReadinessCheckCallback[failCheckCallBack] check failed, the details is: {\"reason\":\"error\"}");
        assertThat(capturedOutput.getOut()).contains(
            "successCheckCallBack is skipped due to the failure of failCheckCallBack");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-24000: ReadinessCheckCallback readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(callbackDetails.size()).isEqualTo(2);
        Health health = callbackDetails.get("failCheckCallBack");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{reason=error}");
    }

    @Test
    public void readinessCheckCallbackException(CapturedOutput capturedOutput) {
        Map<String, ReadinessCheckCallback> beanMap = new HashMap<>();
        beanMap.put("successCheckCallBack", successCheckCallBack);
        beanMap.put("exceptionCheckCallBack", exceptionCheckCallBack);
        Mockito.doReturn(beanMap).when(applicationContext)
            .getBeansOfType(ReadinessCheckCallback.class);

        readinessCheckCallbackProcessor.init();
        HashMap<String, Health> callbackDetails = new HashMap<>();
        boolean result = readinessCheckCallbackProcessor.readinessCheckCallback(callbackDetails);

        assertThat(capturedOutput.getOut())
            .contains(
                "SOFA-BOOT-01-24002: Error occurred while doing ReadinessCheckCallback[exceptionCheckCallBack] check");
        assertThat(capturedOutput.getOut()).contains(
            "successCheckCallBack is skipped due to the failure of exceptionCheckCallBack");
        assertThat(capturedOutput.getOut()).contains(
            "SOFA-BOOT-01-24000: ReadinessCheckCallback readiness check result: failed");
        assertThat(result).isFalse();
        assertThat(callbackDetails.size()).isEqualTo(2);
        Health health = callbackDetails.get("exceptionCheckCallBack");
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).contains("readiness check callback exception");
    }

    static class SuccessReadinessCheckCallback implements ReadinessCheckCallback {

        @Override
        public Health onHealthy(ApplicationContext applicationContext) {
            return Health.up().build();
        }
    }

    static class FailReadinessCheckCallback implements ReadinessCheckCallback {

        @Override
        public Health onHealthy(ApplicationContext applicationContext) {
            return Health.down().withDetail("reason", "error").build();
        }
    }

    static class ExceptionReadinessCheckCallback implements ReadinessCheckCallback {

        @Override
        public Health onHealthy(ApplicationContext applicationContext) {
            throw new RuntimeException("readiness check callback exception");
        }
    }
}
