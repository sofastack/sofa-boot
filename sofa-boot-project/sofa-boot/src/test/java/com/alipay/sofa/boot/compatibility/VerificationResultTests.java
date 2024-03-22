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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VerificationResult}.
 *
 * @author JPSINH27
 * @version VerificationResultTests, v 0.1 2024年03月02日 10:20 PM
 */
public class VerificationResultTests {

    @Test
    public void testEquals_SameDescriptionAndAction_ReturnsTrue() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action");
        VerificationResult result2 = VerificationResult.notCompatible("Error", "Take action");
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    public void testEquals_DifferentDescriptions_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error 1", "Take action");
        VerificationResult result2 = VerificationResult.notCompatible("Error 2", "Take action");
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    public void testEquals_DifferentActions_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action 1");
        VerificationResult result2 = VerificationResult.notCompatible("Error", "Take action 2");
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    public void testEquals_ComparingWithNull_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action");
        assertThat(result1).isNotEqualTo(null);
    }
}