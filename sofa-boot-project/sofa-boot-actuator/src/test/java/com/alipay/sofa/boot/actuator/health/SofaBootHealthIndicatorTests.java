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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaBootHealthIndicator}.
 *
 * @author huzijie
 * @version SofaBootHealthIndicatorTests.java, v 0.1 2023年01月04日 12:11 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaBootHealthIndicatorTests {

    @InjectMocks
    private SofaBootHealthIndicator sofaBootHealthIndicator;

    @Mock
    private HealthCheckerProcessor  healthCheckerProcessor;

    @Mock
    private ReadinessCheckListener  readinessCheckListener;

    @Test
    public void ReadinessResultFail() {
        Mockito.doReturn(false).when(readinessCheckListener).isReadinessCheckFinish();
        Health health = sofaBootHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo(
            "{HEALTH-CHECK-NOT-READY=App is still in startup process, please try later!}");
    }

    @Test
    public void healthResultUp() {
        Mockito.doReturn(true).when(readinessCheckListener).isReadinessCheckFinish();
        Mockito.doReturn(true).when(healthCheckerProcessor).livenessHealthCheck(Mockito.anyMap());
        Health health = sofaBootHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails().toString()).isEqualTo("{SOFABOOT_HEALTH-INDICATOR={}}");
    }

    @Test
    public void healthResultDown() {
        Mockito.doReturn(true).when(readinessCheckListener).isReadinessCheckFinish();
        Mockito.doReturn(false).when(healthCheckerProcessor).livenessHealthCheck(Mockito.anyMap());
        Health health = sofaBootHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{SOFABOOT_HEALTH-INDICATOR={}}");
    }
}
