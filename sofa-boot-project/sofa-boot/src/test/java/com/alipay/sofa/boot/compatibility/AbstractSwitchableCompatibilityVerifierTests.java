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

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version AbstractSwitchableCompatibilityVerifierTests.java, v 0.1 2023年08月07日 11:56 AM huzijie Exp $
 */
public class AbstractSwitchableCompatibilityVerifierTests {

    private final MockEnvironment mockEnvironment = new MockEnvironment();

    @Test
    public void enableKey() {
        mockEnvironment.setProperty("sofa.boot.compatibility-verifier.test.enabled", "true");
        TestSwitchableCompatibilityVerifier verifier = new TestSwitchableCompatibilityVerifier(
            mockEnvironment);
        assertThat(verifier.verify().isNotCompatible()).isTrue();
    }

    @Test
    public void disableKey() {
        mockEnvironment.setProperty("sofa.boot.compatibility-verifier.test.enabled", "false");
        TestSwitchableCompatibilityVerifier verifier = new TestSwitchableCompatibilityVerifier(
            mockEnvironment);
        assertThat(verifier.verify().isNotCompatible()).isFalse();
    }

    public static class TestSwitchableCompatibilityVerifier extends
                                                           AbstractSwitchableCompatibilityVerifier {

        public TestSwitchableCompatibilityVerifier(Environment environment) {
            super(environment);
        }

        @Override
        public CompatibilityPredicate compatibilityPredicate() {
            return () -> false;
        }

        @Override
        public String errorDescription() {
            return "adad";
        }

        @Override
        public String action() {
            return "dad";
        }

        @Override
        public String enableKey() {
            return "test";
        }
    }
}
