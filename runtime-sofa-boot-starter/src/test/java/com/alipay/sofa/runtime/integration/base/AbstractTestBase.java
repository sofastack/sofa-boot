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
package com.alipay.sofa.runtime.integration.base;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.beans.impl.MethodBeanClassAnnotationSampleService;
import com.alipay.sofa.runtime.beans.impl.MethodBeanMethodAnnotationSampleService;
import com.alipay.sofa.runtime.beans.impl.ParameterAnnotationSampleService;
import com.alipay.sofa.runtime.beans.impl.SampleServiceImpl;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.features.AwareTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
public abstract class AbstractTestBase extends TestBase {

    public AwareTest awareTest;

    @Before
    public void before() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        properties.put("mix-xml-annotation-unique-id", "xmlAnnotationSampleService");
        properties.put("spring.jmx.enabled", "false");
        initApplicationContext(properties, IntegrationTestConfiguration.class);
        awareTest = applicationContext.getBean(AwareTest.class);
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportResource({ "classpath*:META-INF/spring/*.xml" })
    @ComponentScan({ "com.alipay.sofa.runtime.integration.features" })
    static class IntegrationTestConfiguration {
        @Configuration
        static class BeforeConfiguration {
            @Bean
            MethodBeanClassAnnotationSampleService methodBeanClassAnnotationSampleService() {
                return new MethodBeanClassAnnotationSampleService();
            }

            @Bean({ "name1", "name2" })
            @SofaService(uniqueId = "methodBeanMethodAnnotationSampleService")
            SampleService methodBeanMethodAnnotationSampleService() {
                return new MethodBeanMethodAnnotationSampleService();
            }

            @Bean("multiService")
            SampleService service() {
                return new SampleServiceImpl("");
            }

            @Bean("multiService")
            SampleService service(@Value("$spring.application.name") String appName) {
                return new SampleServiceImpl("");
            }
        }

        @Configuration
        @AutoConfigureAfter(BeforeConfiguration.class)
        static class AfterConfiguration {
            @Bean
            SampleService parameterAnnotationSampleService(@SofaReference(uniqueId = "${mix-xml-annotation-unique-id}") SampleService service1,
                                                           @SofaReference(uniqueId = "methodBeanClassAnnotationSampleService") SampleService service2,
                                                           @SofaReference(uniqueId = "methodBeanMethodAnnotationSampleService") SampleService service3) {
                return new ParameterAnnotationSampleService(service1, service2, service3);
            }
        }
    }

}