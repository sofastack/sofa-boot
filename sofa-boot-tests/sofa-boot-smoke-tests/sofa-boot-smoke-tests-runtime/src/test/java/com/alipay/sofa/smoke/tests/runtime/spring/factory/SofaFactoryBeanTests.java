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
package com.alipay.sofa.smoke.tests.runtime.spring.factory;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.spring.SofaRuntimeAwareProcessor;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.impl.SampleServiceImpl;
import com.alipay.sofa.smoke.tests.runtime.impl.ServiceWithoutInterface;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaRuntimeAwareProcessor}.
 *
 * @author huzijie
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaFactoryBeanTests.SofaFactoryBeanTestConfiguration.class)
public class SofaFactoryBeanTests {

    static SampleService            parameterSampleService = null;

    @SofaReference(uniqueId = "serviceViaBeanMethod")
    private SampleService           serviceViaBeanMethod;

    @Autowired
    private SampleService           sampleService;

    @SofaReference(uniqueId = "serviceWithoutInterface")
    private ServiceWithoutInterface serviceWithoutInterface;

    @Autowired
    private ApplicationContext      ctx;

    @Test
    public void parameterSampleService() {
        assertThat(serviceViaBeanMethod).isEqualTo(parameterSampleService);
        assertThat(sampleService).isNotEqualTo(parameterSampleService);
    }

    @Test
    public void serviceFactoryBean() throws Exception{
        ServiceFactoryBean serviceFactoryBean;
        ReferenceFactoryBean referenceFactoryBean;

        // Configuration
        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, "serviceViaBeanMethod", "sampleService"));
        assertThat(serviceFactoryBean.isApiType()).isTrue();

        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(ServiceWithoutInterface.class, "serviceWithoutInterface", "serviceWithoutInterface"));
        assertThat(serviceFactoryBean.isApiType()).isTrue();

        referenceFactoryBean = (ReferenceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class, "serviceViaBeanMethod"));
        assertThat(referenceFactoryBean.isApiType()).isTrue();
        assertThat(serviceViaBeanMethod).isEqualTo(referenceFactoryBean.getObject());

        // xml
        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, "", "xmlSampleService"));
        assertThat(serviceFactoryBean.isApiType()).isFalse();

        referenceFactoryBean = (ReferenceFactoryBean)ctx.getBean("&xmlServiceWithoutInterface");
        assertThat(referenceFactoryBean.isApiType()).isFalse();
        assertThat(serviceWithoutInterface).isEqualTo(referenceFactoryBean.getObject());

        ctx.getBeansOfType(ServiceFactoryBean.class).forEach((key, value) -> {
            assertThat(key.startsWith("&ServiceFactoryBean#")).isTrue();
        });

        ctx.getBeansOfType(ReferenceFactoryBean.class).forEach((key, value) -> {
            if (value.isApiType()) {
                assertThat(key.startsWith("&ReferenceFactoryBean#")).isTrue();
            }
        });
    }

    @TestConfiguration
    @ImportResource("classpath*:spring/service/test-service.xml")
    static class SofaFactoryBeanTestConfiguration {
        @Bean
        @SofaService(uniqueId = "serviceViaBeanMethod")
        public SampleService sampleService() {
            return new SampleServiceImpl();
        }

        @Bean
        public ServiceWithoutInterface serviceWithoutInterface(@SofaReference(uniqueId = "serviceViaBeanMethod") SampleService sampleService) {
            parameterSampleService = sampleService;
            return new ServiceWithoutInterface();
        }
    }
}