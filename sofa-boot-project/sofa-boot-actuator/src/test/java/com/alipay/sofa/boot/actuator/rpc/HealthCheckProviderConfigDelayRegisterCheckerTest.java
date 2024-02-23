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
package com.alipay.sofa.boot.actuator.rpc;

import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.availability.ReadinessState;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link HealthCheckProviderConfigDelayRegisterChecker}
 */
@ExtendWith(MockitoExtension.class)
public class HealthCheckProviderConfigDelayRegisterCheckerTest {

    @InjectMocks
    private HealthCheckProviderConfigDelayRegisterChecker healthCheckProviderConfigDelayRegisterChecker;

    @Mock
    private ReadinessCheckListener                        readinessCheckListener;

    @Test
    public void testAllowRegister() {
        // Setup
        Mockito.doReturn(ReadinessState.ACCEPTING_TRAFFIC).when(readinessCheckListener)
            .getReadinessState();
        // Run the test
        boolean result = healthCheckProviderConfigDelayRegisterChecker.allowRegister();

        // Verify the results
        assertTrue(result);
    }

}
