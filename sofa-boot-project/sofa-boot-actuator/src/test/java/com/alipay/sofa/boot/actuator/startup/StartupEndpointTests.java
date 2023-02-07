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
package com.alipay.sofa.boot.actuator.startup;

import com.alipay.sofa.boot.startup.StartupReporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StartupEndPoint}.
 *
 * @author huzijie
 * @version StartupEndpointTests.java, v 0.1 2023年01月04日 11:51 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class StartupEndpointTests {

    @InjectMocks
    private StartupEndPoint startupEndPoint;

    @Mock
    private StartupReporter startupReporter;

    @Test
    public void startup() {
        StartupReporter.StartupStaticsModel staticsModel = new StartupReporter.StartupStaticsModel();
        staticsModel.setAppName("StartupEndpointTests");
        Mockito.doReturn(staticsModel).when(startupReporter).report();
        assertThat(startupEndPoint.startup().getAppName()).isEqualTo("StartupEndpointTests");
    }

    @Test
    public void startupForSpringBoot() {
        assertThatThrownBy(() -> startupEndPoint.startupForSpringBoot())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Please use GET method instead");
    }
}
