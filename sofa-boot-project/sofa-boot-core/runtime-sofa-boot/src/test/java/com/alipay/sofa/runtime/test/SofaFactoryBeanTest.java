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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.beans.service.ServiceWithoutInterface;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=SofaFactoryBeanTest")
public class SofaFactoryBeanTest {

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
    public void testParameterSampleService() {
        Assert.assertEquals(serviceViaBeanMethod, parameterSampleService);
        Assert.assertNotEquals(sampleService, parameterSampleService);
    }

    @Test
    public void testServiceFactoryBean() throws Exception{
        ServiceFactoryBean serviceFactoryBean;
        ReferenceFactoryBean referenceFactoryBean;

        // Configuration
        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, "serviceViaBeanMethod"));
        Assert.assertTrue(serviceFactoryBean.isApiType());

        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(ServiceWithoutInterface.class, "serviceWithoutInterface"));
        Assert.assertTrue(serviceFactoryBean.isApiType());

        referenceFactoryBean = (ReferenceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class, "serviceViaBeanMethod"));
        Assert.assertTrue(referenceFactoryBean.isApiType());
        Assert.assertEquals(serviceViaBeanMethod, referenceFactoryBean.getObject());

        // xml
        serviceFactoryBean = (ServiceFactoryBean)ctx.getBean("&" + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, ""));
        Assert.assertFalse(serviceFactoryBean.isApiType());

        referenceFactoryBean = (ReferenceFactoryBean)ctx.getBean("&xmlServiceWithoutInterface");
        Assert.assertFalse(referenceFactoryBean.isApiType());
        Assert.assertEquals(serviceWithoutInterface, referenceFactoryBean.getObject());

        ctx.getBeansOfType(ServiceFactoryBean.class).forEach((key, value) -> {
            Assert.assertTrue(key.startsWith("&ServiceFactoryBean#"));
        });

        ctx.getBeansOfType(ReferenceFactoryBean.class).forEach((key, value) -> {
            if (value.isApiType()) {
                Assert.assertTrue(key.startsWith("&ReferenceFactoryBean#"));
            }
        });
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    @ImportResource("classpath*:META-INF/service/test-service.xml")
    static class SofaFactoryBeanTestConfiguration {
        @Bean
        @SofaService(uniqueId = "serviceViaBeanMethod")
        public SampleService sampleService() {
            return new DefaultSampleService();
        }

        @Bean
        public ServiceWithoutInterface serviceWithoutInterface(@SofaReference(uniqueId = "serviceViaBeanMethod") SampleService sampleService) {
            parameterSampleService = sampleService;
            return new ServiceWithoutInterface();
        }
    }
}