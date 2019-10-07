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
package com.alipay.sofa.runtime.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alipay.sofa.boot.util.StringUtils;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.MultiSofaServiceConfiguration;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 * @author qilong.zql
 * @since  3.1.0
 */
public class SofaServiceAndReferenceTest {
    private SampleService sampleService;

    public SampleService getSampleService() {
        return sampleService;
    }

    @SofaReference
    public void setSampleService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Test
    public void testSofaReferenceOnMethodParameter() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
        Throwable throwable = null;
        try {
            SpringApplication springApplication = new SpringApplication(
                TestSofaReferenceConfiguration.class, RuntimeConfiguration.class);
            springApplication.setWebApplicationType(WebApplicationType.NONE);
            springApplication.setDefaultProperties(properties);
            springApplication.run();
        } catch (Throwable t) {
            throwable = t;
            Assert.assertEquals("Only jvm type of @SofaReference on parameter is supported.",
                t.getMessage());
        }
        Assert.assertNotNull(throwable);
    }

    @Test
    public void testSofaReferenceOnSingleParameterMethod() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
        SpringApplication springApplication = new SpringApplication(
            TestSofaReferenceOnMethodConfiguration.class, RuntimeConfiguration.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setDefaultProperties(properties);
        ApplicationContext ctx = springApplication.run();
        SofaServiceAndReferenceTest sofaServiceAndReferenceTest = ctx
            .getBean(SofaServiceAndReferenceTest.class);
        Assert.assertNotNull(sofaServiceAndReferenceTest.getSampleService());
    }

    @Test
    public void testMultiSofaServiceWithSameInterfaceAndUniqueId() throws IOException {
        String logRootPath = StringUtils.hasText(System.getProperty("logging.path")) ? System
            .getProperty("logging.path") : "./logs";
        File sofaLog = new File(logRootPath + File.separator + "sofa-runtime" + File.separator
                                + "common-error.log");
        FileUtils.write(sofaLog, "", System.getProperty("file.encoding"));
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
        properties.put("logging.path", logRootPath);

        SpringApplication springApplication = new SpringApplication(
            TestSofaServiceConfiguration.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setDefaultProperties(properties);
        springApplication.run();

        String content = FileUtils.readFileToString(sofaLog, System.getProperty("file.encoding"));
        Assert.assertTrue(content.contains("SofaService was already registered: "
                                           + SofaBeanNameGenerator.generateSofaServiceBeanName(
                                               SampleService.class, "")));
        FileUtils.deleteDirectory(new File("./logs"));
    }

    @Test
    public void testMultiSofaServiceFactoryMethod() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "SofaServiceAndReferenceTest");

        SpringApplication springApplication = new SpringApplication(
            MultiSofaServiceConfiguration.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setDefaultProperties(properties);
        springApplication.run();
    }

    @Test
    public void testSofaServiceWithMultipleBindings() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
        SpringApplication springApplication = new SpringApplication(
            MultipleBindingsSofaServiceConfiguration.class, RuntimeConfiguration.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setDefaultProperties(properties);
        ApplicationContext ctx = springApplication.run();
        ServiceFactoryBean bean = ctx.getBean(ServiceFactoryBean.class);
        Assert.assertEquals(2, bean.getBindings().size());
        Assert.assertEquals(new BindingType("jvm"), bean.getBindings().get(0).getBindingType());
        Assert.assertEquals(new BindingType("jvm"), bean.getBindings().get(1).getBindingType());
    }

    @Configuration
    static class MultipleBindingsSofaServiceConfiguration {
        /**
         * since the sofa-boot does not have any binding converter implementation,
         * we can use two jvm bindings for now.
         */
        @Bean
        @SofaService(bindings = { @SofaServiceBinding, @SofaServiceBinding })
        SampleService sampleService() {
            return new DefaultSampleService();
        }
    }

    @Configuration
    static class TestSofaReferenceConfiguration {
        @Bean
        public SampleService sampleService(@SofaReference(uniqueId = "rpc", binding = @SofaReferenceBinding(bindingType = "bolt")) SampleService sampleService) {
            return new DefaultSampleService("TestSofaReferenceConfiguration");
        }
    }

    @Configuration
    static class TestSofaReferenceOnMethodConfiguration {
        @Bean
        public SofaServiceAndReferenceTest sofaServiceAndReferenceTest() {
            return new SofaServiceAndReferenceTest();
        }
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    @EnableAutoConfiguration
    static class TestSofaServiceConfiguration {
        @Bean
        @SofaService
        public SampleService sampleService() {
            return new DefaultSampleService();
        }

        @Bean
        @SofaService
        public SampleService duplicateSampleService() {
            return new DefaultSampleService();
        }
    }

}