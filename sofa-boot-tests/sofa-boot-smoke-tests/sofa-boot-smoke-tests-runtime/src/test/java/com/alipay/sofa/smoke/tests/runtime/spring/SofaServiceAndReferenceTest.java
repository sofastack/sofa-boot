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
///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alipay.sofa.smoke.tests.runtime.spring;
//
//import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
//import com.alipay.sofa.runtime.api.annotation.SofaReference;
//import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
//import com.alipay.sofa.runtime.api.annotation.SofaService;
//import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
//import com.alipay.sofa.runtime.api.binding.BindingType;
//import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
//import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
//import com.alipay.sofa.smoke.tests.runtime.impl.SampleServiceImpl;
//import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.WebApplicationType;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author qilong.zql
// * @since  3.1.0
// */
//public class SofaServiceAndReferenceTest {
//    private SampleService sampleService;
//
//    public SampleService getSampleService() {
//        return sampleService;
//    }
//
//    @SofaReference
//    private static SampleService staticSampleService;
//
//    public static SampleService getStaticSampleService() {
//        return staticSampleService;
//    }
//
//    @SofaReference
//    public void setSampleService(SampleService sampleService) {
//        this.sampleService = sampleService;
//    }
//
//    @Test
//    public void testSofaReferenceOnMethodParameter() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//        Throwable throwable = null;
//        try {
//            SpringApplication springApplication = new SpringApplication(
//                TestSofaReferenceConfiguration.class, SofaRuntimeAutoConfiguration.class);
//            springApplication.setWebApplicationType(WebApplicationType.NONE);
//            springApplication.setDefaultProperties(properties);
//            springApplication.run();
//        } catch (Throwable t) {
//            throwable = t;
//            Assert.assertTrue(t.getMessage().contains(
//                "Only jvm type of @SofaReference on parameter is supported."));
//        }
//        Assert.assertNotNull(throwable);
//    }
//
//    @Test
//    public void testSofaReferenceOnSingleParameterMethod() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//        SpringApplication springApplication = new SpringApplication(
//            TestSofaReferenceOnMethodConfiguration.class, SofaRuntimeAutoConfiguration.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.setDefaultProperties(properties);
//        ApplicationContext ctx = springApplication.run();
//        SofaServiceAndReferenceTest sofaServiceAndReferenceTest = ctx
//            .getBean(SofaServiceAndReferenceTest.class);
//        sampleService = sofaServiceAndReferenceTest.getSampleService();
//        Assert.assertNotNull(sampleService);
//        Assert.assertEquals("TestSofaReferenceOnMethodConfiguration", sampleService.service());
//    }
//
//    @Test
//    public void testMultiSofaServiceWithSameInterfaceAndUniqueId() throws IOException {
//        String logRootPath = StringUtils.hasText(System.getProperty("logging.path")) ? System
//            .getProperty("logging.path") : "./logs";
//        File sofaLog = new File(logRootPath + File.separator + "sofa-runtime" + File.separator
//                                + "sofa-default.log");
//        FileUtils.write(sofaLog, "", System.getProperty("file.encoding"));
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//        properties.put("logging.path", logRootPath);
//
//        SpringApplication springApplication = new SpringApplication(
//            TestSofaServiceConfiguration.class, SofaRuntimeAutoConfiguration.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.setDefaultProperties(properties);
//        springApplication.run();
//
//        String content = FileUtils.readFileToString(sofaLog, System.getProperty("file.encoding"));
//        Assert.assertTrue(content.contains("SofaService was already registered: "
//                                           + SofaBeanNameGenerator.generateSofaServiceBeanName(
//                                               SampleService.class, "")));
//    }
//
//    @Test
//    public void testMultiSofaServiceFactoryMethod() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//
//        SpringApplication springApplication = new SpringApplication(
//            MultiSofaServiceConfiguration.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.setDefaultProperties(properties);
//        springApplication.run();
//    }
//
//    @Test
//    public void testSofaServiceWithMultipleBindings() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//        SpringApplication springApplication = new SpringApplication(
//            MultipleBindingsSofaServiceConfiguration.class, SofaRuntimeAutoConfiguration.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.setDefaultProperties(properties);
//        ApplicationContext ctx = springApplication.run();
//        ServiceFactoryBean bean = ctx.getBean(ServiceFactoryBean.class);
//        Assert.assertEquals(2, bean.getBindings().size());
//        Assert.assertEquals(new BindingType("jvm"), bean.getBindings().get(0).getBindingType());
//        Assert.assertEquals(new BindingType("jvm"), bean.getBindings().get(1).getBindingType());
//    }
//
//    @Test
//    public void testSofaReferenceOnStaticField() throws IOException, NoSuchFieldException {
//        String logRootPath = StringUtils.hasText(System.getProperty("logging.path")) ? System
//            .getProperty("logging.path") : "./logs";
//        File sofaLog = new File(logRootPath + File.separator + "sofa-runtime" + File.separator
//                                + "sofa-default.log");
//        FileUtils.write(sofaLog, "", System.getProperty("file.encoding"));
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("spring.application.name", "SofaServiceAndReferenceTest");
//        properties.put("logging.path", logRootPath);
//        SpringApplication springApplication = new SpringApplication(
//            TestSofaReferenceOnMethodConfiguration.class, SofaRuntimeAutoConfiguration.class);
//        springApplication.setWebApplicationType(WebApplicationType.NONE);
//        springApplication.setDefaultProperties(properties);
//        springApplication.run();
//
//        SampleService staticSampleService = SofaServiceAndReferenceTest.getStaticSampleService();
//        Assert.assertNull(staticSampleService);
//        String content = FileUtils.readFileToString(sofaLog, System.getProperty("file.encoding"));
//        Assert.assertTrue(content
//            .contains("SofaReference annotation is not supported on static fields: "
//                      + SofaServiceAndReferenceTest.class.getDeclaredField("staticSampleService")));
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    static class MultipleBindingsSofaServiceConfiguration {
//        /**
//         * since the sofa-boot does not have any binding converter implementation,
//         * we can use two jvm bindings for now.
//         */
//        @Bean
//        @SofaService(bindings = { @SofaServiceBinding, @SofaServiceBinding })
//        SampleService sampleService() {
//            return new SampleServiceImpl();
//        }
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    static class TestSofaReferenceConfiguration {
//        @Bean
//        public SampleService sampleService(@SofaReference(uniqueId = "rpc", binding = @SofaReferenceBinding(bindingType = "bolt")) SampleService sampleService) {
//            return new SampleServiceImpl("TestSofaReferenceConfiguration");
//        }
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    static class TestSofaReferenceOnMethodConfiguration {
//        @Bean
//        public SofaServiceAndReferenceTest sofaServiceAndReferenceTest() {
//            return new SofaServiceAndReferenceTest();
//        }
//
//        @Bean
//        @SofaService
//        public SampleService sampleService() {
//            return new SampleServiceImpl("TestSofaReferenceOnMethodConfiguration");
//        }
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    @EnableAutoConfiguration
//    static class TestSofaServiceConfiguration {
//        @Bean
//        @SofaService
//        public SampleService sampleService() {
//            return new SampleServiceImpl();
//        }
//
//        @Bean
//        @SofaService
//        public SampleService duplicateSampleService() {
//            return new SampleServiceImpl();
//        }
//    }
//
//    static class MultiSofaServiceConfiguration {
//
//        @Bean("multiSofaService")
//        @SofaService
//        SampleService service() {
//            return new SampleServiceImpl();
//        }
//
//        @Bean("multiSofaService")
//        @SofaService
//        SampleService service(@Value("$spring.application.name") String appName) {
//            return new SampleServiceImpl();
//        }
//    }
//
//}