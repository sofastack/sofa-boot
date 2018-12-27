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
package com.alipay.sofa.runtime.integration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.beans.impl.SampleServiceImpl;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.base.TestBase;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * @author qilong.zql
 * @since  3.1.0
 */
public class TestSofaServiceAndReferenceException extends TestBase {

    @Test
    public void testSofaReferenceOnMethodParameter() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        Throwable throwable = null;
        try {
            initApplicationContext(properties, TestSofaReferenceConfiguration.class);
        } catch (Throwable t) {
            throwable = t;
        }
        Assert.assertTrue(throwable instanceof IllegalArgumentException);
        Assert.assertEquals("Only jvm type of @SofaReference on parameter is supported.",
            throwable.getMessage());
    }

    @Test
    public void testMultiSofaServiceWithSameInterfaceAndUniqueId() throws IOException {
        File sofaLog = new File(System.getProperty("user.home") + File.separator + "logs"
                                + File.separator + "sofa-runtime" + File.separator
                                + "common-error.log");
        FileUtils.write(sofaLog, "", System.getProperty("file.encoding"));
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        initApplicationContext(properties, TestSofaServiceConfiguration.class);
        String content = FileUtils.readFileToString(sofaLog, System.getProperty("file.encoding"));
        Assert.assertTrue(content.contains("SofaService was already registered: "
                                           + SofaBeanNameGenerator.generateSofaServiceBeanName(
                                               SampleService.class, "")));
    }

    @Test(expected = FatalBeanException.class)
    public void testMultiSofaServiceFactoryMethod() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        properties.put("multiSofaService", "true");
        initApplicationContext(properties, EmptyConfiguration.class);
    }

    @Test(expected = FatalBeanException.class)
    public void testMultiSofaReferenceFactoryMethod() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        properties.put("multiSofaReference", "true");
        initApplicationContext(properties, EmptyConfiguration.class);
    }

    @EnableAutoConfiguration
    @Configuration
    static class EmptyConfiguration {
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestSofaReferenceConfiguration {
        @Bean
        SampleService sampleService(@SofaReference(uniqueId = "rpc", binding = @SofaReferenceBinding(bindingType = "bolt")) SampleService sampleService) {
            return new SampleServiceImpl("test");
        }
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestSofaServiceConfiguration {
        @Bean
        @SofaService
        SampleService sampleService() {
            return new SampleServiceImpl("test");
        }

        @Bean
        @SofaService
        SampleService duplicateSampleService() {
            return new SampleServiceImpl("test");
        }
    }

}