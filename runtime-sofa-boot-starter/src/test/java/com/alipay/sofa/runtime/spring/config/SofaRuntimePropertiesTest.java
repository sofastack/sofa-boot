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
package com.alipay.sofa.runtime.spring.config;

import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SofaRuntimePropertiesTest {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @After
    public void closeContext() {
        this.applicationContext.close();
    }

    @Test
    public void testDisableJvmFirstProperty() {
        assertFalse(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));

        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.disableJvmFirst=true");
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        this.applicationContext.refresh();
        SofaRuntimeConfigurationProperties configurationProperties = this.applicationContext
            .getBean(SofaRuntimeConfigurationProperties.class);

        assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));
        assertTrue(configurationProperties.isDisableJvmFirst());
    }

    @Test
    public void testSkipJvmReferenceHealthCheckProperty() {
        assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));

        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.skipJvmReferenceHealthCheck=true");
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        this.applicationContext.refresh();
        SofaRuntimeConfigurationProperties configurationProperties = this.applicationContext
            .getBean(SofaRuntimeConfigurationProperties.class);

        assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        assertTrue(configurationProperties.isSkipJvmReferenceHealthCheck());
    }

}