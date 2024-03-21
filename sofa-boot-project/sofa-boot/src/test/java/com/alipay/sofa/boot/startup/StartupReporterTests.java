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
package com.alipay.sofa.boot.startup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link StartupReporter}.
 *
 * @author JPSINH27
 * @version StartupReporterTests.java, v 0.1 2024年01月03日 10:19 PM
 */
public class StartupReporterTests {

    @Mock
    ConfigurableApplicationContext mockContext;

    @Mock
    ConfigurableEnvironment        mockEnvironment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testApplicationBootFinish() {
        StartupReporter startupReporter = new StartupReporter();
        assertDoesNotThrow(startupReporter::applicationBootFinish);
    }

    @Test
    public void testAddCommonStartupStat() {
        StartupReporter startupReporter = new StartupReporter();
        BaseStat baseStat = new BaseStat();
        assertDoesNotThrow(() -> {
            startupReporter.addCommonStartupStat(baseStat);
        });
    }

    @Test
    public void testDrainStartupStaticsModel() {
        StartupReporter startupReporter = new StartupReporter();
        assertNotNull(startupReporter.drainStartupStaticsModel());
    }

}