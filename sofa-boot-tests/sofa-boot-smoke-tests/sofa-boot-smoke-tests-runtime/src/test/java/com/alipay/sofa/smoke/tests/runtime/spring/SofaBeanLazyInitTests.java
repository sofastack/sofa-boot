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
package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaService} and {@link SofaReference} Bean Lazy Initialization.
 *
 * @author Jermaine Hua
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class, properties = "spring.main.lazy-initialization=true")
@Import(SofaBeanLazyInitTests.ServiceBeanAnnotationConfiguration.class)
public class SofaBeanLazyInitTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void checkSofaServiceBeanDefinitionLazyInitAttribute() {
        String beanName = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
            "sampleServiceImpl");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(applicationContext.getBean(beanName)).isInstanceOf(ServiceImpl.class);

        assertThat(
            ((GenericApplicationContext) applicationContext).getBeanFactory()
                .getBeanDefinition(beanName).isLazyInit()).isTrue();

    }

    @Test
    public void checkSofaReferenceBeanDefinitionLazyInitAttribute() {
        String beanName = SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
            "");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(
            ((GenericApplicationContext) applicationContext).getBeanFactory()
                .getBeanDefinition(beanName).isLazyInit()).isTrue();

    }

    @Configuration
    @Import({ SampleServiceImpl.class })
    static class ServiceBeanAnnotationConfiguration {
        @Bean
        public SampleService methodSampleService(@SofaReference SampleService sampleServiceImpl) {
            return () -> "methodSampleService";
        }
    }

    @SofaService
    @Component("sampleServiceImpl")
    static class SampleServiceImpl implements SampleService {

        @Override
        public String service() {
            return "sampleService";
        }
    }

}
