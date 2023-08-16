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
package com.alipay.sofa.boot.compatibility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author huzijie
 * @version CompatibilityVerifierApplicationContextInitializerTests.java, v 0.1 2023年08月07日 12:11 PM huzijie Exp $
 */
public class CompatibilityVerifierApplicationContextInitializerTests {

    private final MockEnvironment           mockEnvironment    = new MockEnvironment();

    private final GenericApplicationContext applicationContext = new GenericXmlApplicationContext();

    @BeforeEach
    public void setUp() {
        TestCompatibilityVerifier.invoked = false;
        applicationContext.setEnvironment(mockEnvironment);
        mockEnvironment.setProperty("enable.test.compatibility", "true");
    }

    @Test
    public void enableKey() {
        CompatibilityVerifierApplicationContextInitializer initializer = new CompatibilityVerifierApplicationContextInitializer();
        assertThatThrownBy(() -> initializer.initialize(applicationContext))
                .isInstanceOf(CompatibilityNotMetException.class)
                .hasMessageContaining("description: test error")
                .hasMessageContaining("action: test action");
        assertThat(TestCompatibilityVerifier.invoked).isTrue();
    }

    @Test
    public void disableKey() {
        mockEnvironment.setProperty("sofa.boot.compatibility-verifier.enabled", "false");
        CompatibilityVerifierApplicationContextInitializer initializer = new CompatibilityVerifierApplicationContextInitializer();
        initializer.initialize(applicationContext);

        assertThat(TestCompatibilityVerifier.invoked).isFalse();
    }

    @AfterEach
    public void clear() {
        TestCompatibilityVerifier.invoked = false;
    }
}
