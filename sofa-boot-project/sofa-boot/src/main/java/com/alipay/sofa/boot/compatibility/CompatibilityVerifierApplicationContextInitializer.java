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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * Implements for {@link ApplicationContextInitializer} to verify compatibilities.
 *
 * @author huzijie
 * @version CompatibilityVerifierApplicationContextInitializer.java, v 0.1 2023年08月03日 4:44 PM huzijie Exp $
 */
public class CompatibilityVerifierApplicationContextInitializer
                                                               implements
                                                               ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String COMPATIBILITY_VERIFIER_ENABLED = "sofa.boot.compatibility-verifier.enabled";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        if (!Boolean.parseBoolean(environment.getProperty(COMPATIBILITY_VERIFIER_ENABLED, "true"))) {
            Logger logger = LoggerFactory
                .getLogger(CompatibilityVerifierApplicationContextInitializer.class);
            logger.info("Skip SOFABoot compatibility Verifier");
            return;
        }

        // Load all CompatibilityVerifier and verify.
        CompositeCompatibilityVerifier compositeCompatibilityVerifier = new CompositeCompatibilityVerifier(
            getCompatibilityVerifierInstances(environment));
        compositeCompatibilityVerifier.verifyCompatibilities();
    }

    private List<CompatibilityVerifier> getCompatibilityVerifierInstances(Environment environment) {

        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver
            .of(Environment.class, environment);
        SpringFactoriesLoader springFactoriesLoader = SpringFactoriesLoader
            .forDefaultResourceLocation();
        // Use names and ensure unique to protect against duplicates
        List<CompatibilityVerifier> instances = springFactoriesLoader.load(
            CompatibilityVerifier.class, argumentResolver);
        AnnotationAwareOrderComparator.sort(instances);
        return instances;
    }
}
