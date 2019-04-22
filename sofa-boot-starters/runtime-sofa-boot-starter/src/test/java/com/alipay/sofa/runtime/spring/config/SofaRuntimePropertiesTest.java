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
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SofaRuntimePropertiesTest {

    private ConfigurableApplicationContext applicationContext;

    @After
    public void closeContext() {
        this.applicationContext.close();
    }

    @Test
    public void testDisableJvmFirstProperty() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("com.alipay.sofa.boot.disableJvmFirst", "true");
        SpringApplication springApplication = new SpringApplication(EmptyConfiguration.class);
        springApplication.setDefaultProperties(properties);
        this.applicationContext = springApplication.run(new String[] {});
        SofaRuntimeConfigurationProperties configurationProperties = this.applicationContext
            .getBean(SofaRuntimeConfigurationProperties.class);

        assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));
        assertTrue(configurationProperties.isDisableJvmFirst());
    }

    @Test
    public void testSkipJvmReferenceHealthCheckProperty() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("com.alipay.sofa.boot.skipJvmReferenceHealthCheck", "true");
        SpringApplication springApplication = new SpringApplication(EmptyConfiguration.class);
        springApplication.setDefaultProperties(properties);
        this.applicationContext = springApplication.run(new String[] {});
        SofaRuntimeConfigurationProperties configurationProperties = this.applicationContext
            .getBean(SofaRuntimeConfigurationProperties.class);

        assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        assertTrue(configurationProperties.isSkipJvmReferenceHealthCheck());
    }

    @EnableAutoConfiguration
    @Configuration
    static class EmptyConfiguration {
    }

}