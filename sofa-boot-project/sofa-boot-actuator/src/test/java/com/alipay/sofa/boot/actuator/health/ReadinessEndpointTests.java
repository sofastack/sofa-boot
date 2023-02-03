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
 * Tests for {@link ReadinessEndpoint}.
 *
 * @author huzijie
 * @version ReadinessEndpointTests.java, v 0.1 2023年01月04日 12:06 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ReadinessEndpointTests {

    @InjectMocks
    private ReadinessEndpoint      readinessEndpoint;

    @Mock
    private ReadinessCheckListener readinessCheckListener;

    @Test
    public void healthWithOutDetails() {
        Health health = Health.down().withDetail("db", "error").build();
        Mockito.doReturn(health).when(readinessCheckListener).aggregateReadinessHealth();
        Health result = readinessEndpoint.health("false");
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
        assertThat(result.getDetails()).isEmpty();
    }

    @Test
    public void healthWithDetails() {
        Health health = Health.up().withDetail("db", "success").build();
        Mockito.doReturn(health).when(readinessCheckListener).aggregateReadinessHealth();
        Health result = readinessEndpoint.health("true");
        assertThat(result.getStatus()).isEqualTo(Status.UP);
        assertThat(result.getDetails().toString()).isEqualTo("{db=success}");
    }
}
