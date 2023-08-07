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
package com.alipay.sofa.runtime.test.compatibility;

import com.alipay.sofa.boot.compatibility.CompatibilityNotMetException;
import com.alipay.sofa.runtime.compatibility.CompatibilityVerifierApplicationContextInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mock.env.MockEnvironment;

/**
 * @author huzijie
 * @version CompatibilityVerifierApplicationContextInitializerTest.java, v 0.1 2023年08月07日 12:11 PM huzijie Exp $
 */
public class CompatibilityVerifierApplicationContextInitializerTest {

    private final MockEnvironment           mockEnvironment    = new MockEnvironment();

    private final GenericApplicationContext applicationContext = new GenericXmlApplicationContext();

    @Before
    public void setUp() {
        applicationContext.setEnvironment(mockEnvironment);
    }

    @Test
    public void enableKey() {
        CompatibilityVerifierApplicationContextInitializer initializer = new CompatibilityVerifierApplicationContextInitializer();
        try {
            initializer.initialize(applicationContext);
            Assert.fail();
        } catch (CompatibilityNotMetException e) {
            Assert.assertTrue(e.getMessage().contains(
                "compatible = false, description = 'test error', action = 'test action'"));
        }

        Assert.assertTrue(TestCompatibilityVerifier.invoked);
    }

    @Test
    public void disableKey() {
        mockEnvironment.setProperty("sofa.boot.compatibility-verifier.enabled", "false");
        CompatibilityVerifierApplicationContextInitializer initializer = new CompatibilityVerifierApplicationContextInitializer();
        initializer.initialize(applicationContext);

        Assert.assertFalse(TestCompatibilityVerifier.invoked);
    }

    @Before
    public void clear() {
        TestCompatibilityVerifier.invoked = false;
    }
}
