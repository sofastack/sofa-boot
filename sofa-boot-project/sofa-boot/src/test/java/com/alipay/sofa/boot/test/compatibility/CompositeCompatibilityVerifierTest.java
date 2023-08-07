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
package com.alipay.sofa.boot.test.compatibility;

import com.alipay.sofa.boot.compatibility.CompatibilityNotMetException;
import com.alipay.sofa.boot.compatibility.CompatibilityVerifier;
import com.alipay.sofa.boot.compatibility.CompositeCompatibilityVerifier;
import com.alipay.sofa.boot.compatibility.VerificationResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huzijie
 * @version CompositeCompatibilityVerifierTest.java, v 0.1 2023年08月07日 12:00 PM huzijie Exp $
 */
public class CompositeCompatibilityVerifierTest {

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
        try {
            verifier.verifyCompatibilities();
            Assert.fail();
        } catch (CompatibilityNotMetException e) {
            Assert.assertTrue(e.getMessage().contains("description = 'verify error', action = 'do action'"));
        }
    }
}
