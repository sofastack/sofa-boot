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

import java.util.ArrayList;
import java.util.List;

/**
 * Composite compatibility verifier.
 *
 * @author huzijie
 * @version CompositeCompatibilityVerifier.java, v 0.1 2023年08月03日 4:40 PM huzijie Exp $
 */
public class CompositeCompatibilityVerifier {

    private final List<CompatibilityVerifier> verifiers;

    public CompositeCompatibilityVerifier(List<CompatibilityVerifier> verifiers) {
        this.verifiers = verifiers;
    }

    public void verifyCompatibilities() {
        List<VerificationResult> errors = verifierErrors();
        if (errors.isEmpty()) {
            return;
        }
        String errorMessage = errors.stream().map(VerificationResult::toErrorMessage).toList().toString();
        throw new CompatibilityNotMetException(errors, errorMessage);
    }

    private List<VerificationResult> verifierErrors() {
        List<VerificationResult> errors = new ArrayList<>();
        for (CompatibilityVerifier verifier : this.verifiers) {
            VerificationResult result = verifier.verify();
            if (result.isNotCompatible()) {
                errors.add(result);
            }
        }
        return errors;
    }
}
