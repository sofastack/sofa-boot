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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author huzijie
 * @version CompositeCompatibilityVerifierTests.java, v 0.1 2023年08月07日 12:00 PM huzijie Exp $
 */
public class CompositeCompatibilityVerifierTests {

    @Test
    public void empty() {
        CompositeCompatibilityVerifier verifier = new CompositeCompatibilityVerifier(
            new ArrayList<>());
        verifier.verifyCompatibilities();
    }

    @Test
    public void pass() {
        CompatibilityVerifier compatibilityVerifier = VerificationResult::compatible;
        List<CompatibilityVerifier> verifiers = new ArrayList<>();
        verifiers.add(compatibilityVerifier);
        CompositeCompatibilityVerifier verifier = new CompositeCompatibilityVerifier(verifiers);
        verifier.verifyCompatibilities();
    }

    @Test
    public void notPass() {
        CompatibilityVerifier compatibilityVerifier = () -> VerificationResult.notCompatible("verify error", "do action");
        List<CompatibilityVerifier> verifiers = new ArrayList<>();
        verifiers.add(compatibilityVerifier);
        CompositeCompatibilityVerifier verifier = new CompositeCompatibilityVerifier(verifiers);
        assertThatThrownBy(verifier::verifyCompatibilities).isInstanceOf(CompatibilityNotMetException.class)
                .hasMessageContaining("description: verify error")
                .hasMessageContaining("action: do action");
    }
}
