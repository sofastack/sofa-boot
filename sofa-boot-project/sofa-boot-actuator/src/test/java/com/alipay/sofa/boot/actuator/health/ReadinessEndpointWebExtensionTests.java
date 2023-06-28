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
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReadinessEndpoint}.
 *
 * @author huzijie
 * @version ReadinessEndpointWebExtensionTests.java, v 0.1 2023年01月06日 3:15 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ReadinessEndpointWebExtensionTests {

    @InjectMocks
    private ReadinessEndpointWebExtension endpointWebExtension;

    @Mock
    private ReadinessEndpoint             readinessEndpoint;

    @Mock
    private HttpCodeStatusMapper          statusCodeMapper;

    @Test
    public void getHealthSuccess() {
        Mockito.doReturn(Health.up().build()).when(readinessEndpoint).health(null);
        Mockito.doReturn(200).when(statusCodeMapper).getStatusCode(Status.UP);
        assertThat(endpointWebExtension.getHealth(null).getStatus()).isEqualTo(200);
    }

    @Test
    public void getHealthFail() {
        Mockito.doReturn(Health.down().build()).when(readinessEndpoint).health(null);
        Mockito.doReturn(500).when(statusCodeMapper).getStatusCode(Status.DOWN);
        assertThat(endpointWebExtension.getHealth(null).getStatus()).isEqualTo(500);
    }
}
