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

import org.springframework.core.env.Environment;

/**
 * Abstract class for {@link CompatibilityVerifier} to support switch.
 * 
 * @author huzijie
 * @version AbstractSwitchableCompatibilityVerifier.java, v 0.1 2023年08月03日 6:10 PM huzijie Exp $
 */
public abstract class AbstractSwitchableCompatibilityVerifier implements CompatibilityVerifier {

    private static final String ENABLE_KEY_FORMAT = "sofa.boot.compatibility-verifier.%s.enabled";

    protected final Environment environment;

    protected String            enableKey;

    public AbstractSwitchableCompatibilityVerifier(Environment environment) {
        this.environment = environment;
    }

    @Override
    public VerificationResult verify() {
        this.enableKey = String.format(ENABLE_KEY_FORMAT, enableKey());
        if (!Boolean.parseBoolean(environment.getProperty(enableKey, "true"))) {
            return VerificationResult.compatible();
        }

        CompatibilityPredicate compatibilityPredicate = compatibilityPredicate();
        boolean matches = compatibilityPredicate.isCompatible();
        if (matches) {
            return VerificationResult.compatible();
        }
        return VerificationResult.notCompatible(errorDescription(), action());
    }

    public abstract CompatibilityPredicate compatibilityPredicate();

    public abstract String errorDescription();

    public abstract String action();

    public abstract String enableKey();
}
