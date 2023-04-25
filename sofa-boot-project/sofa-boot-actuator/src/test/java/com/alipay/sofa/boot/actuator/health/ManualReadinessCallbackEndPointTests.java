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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ManualReadinessCallbackEndpoint}.
 *
 * @author huzijie
 * @version ManualReadinessCallbackEndPointTests.java, v 0.1 2023年01月04日 3:58 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ManualReadinessCallbackEndPointTests {

    @InjectMocks
    private ManualReadinessCallbackEndpoint manualReadinessCallbackEndPoint;

    @Mock
    private ReadinessCheckListener          readinessCheckListener;

    @Test
    public void trigger() {
        ReadinessCheckListener.ManualReadinessCallbackResult mockResult = new ReadinessCheckListener.ManualReadinessCallbackResult(
            true, "trigger Success");
        Mockito.doReturn(mockResult).when(readinessCheckListener).triggerReadinessCallback();
        ReadinessCheckListener.ManualReadinessCallbackResult result = manualReadinessCallbackEndPoint
            .trigger();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDetails()).isEqualTo("trigger Success");
    }
}
