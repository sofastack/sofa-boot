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

import java.util.Collection;

/**
 * Abstract class for {@link AbstractSwitchableCompatibilityVerifier} to verify jar compatibility.
 *
 * @author huzijie
 * @version AbstractJarVersionVerifier.java, v 0.1 2023年08月03日 5:14 PM huzijie Exp $
 */
public abstract class AbstractJarVersionVerifier extends AbstractSwitchableCompatibilityVerifier {

    public AbstractJarVersionVerifier(Environment environment) {
        super(environment);
    }

    @Override
    public CompatibilityPredicate compatibilityPredicate() {
        return () -> {
            Collection<CompatibilityPredicate> compatibilityPredicates = getJarCompatibilityPredicates();
            if (compatibilityPredicates == null) {
                return true;
            }
            return compatibilityPredicates.stream().allMatch(CompatibilityPredicate::isCompatible);
        };
    }

    @Override
    public String errorDescription() {
        return String.format("SOFABoot is not compatible with jar [%s] for current version.",
            name());
    }

    @Override
    public String action() {
        return String.format(
            "Change [%s] to appropriate version,"
                    + "you can visit this doc [%s] and find an appropriate version,"
                    + "If you want to disable this check, just set the property [%s=false].",
            name(), doc(), this.enableKey);
    }

    public abstract Collection<CompatibilityPredicate> getJarCompatibilityPredicates();

    public abstract String name();

    public abstract String doc();
}
