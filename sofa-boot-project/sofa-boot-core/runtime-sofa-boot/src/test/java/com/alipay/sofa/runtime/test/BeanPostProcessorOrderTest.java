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

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import com.alipay.sofa.runtime.test.beans.BeanPostProcessorOrderBean;
import com.alipay.sofa.runtime.test.beans.processor.HighOrderBeanPostProcessor;
import com.alipay.sofa.runtime.test.beans.processor.LowOrderBeanPostProcessor;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 *
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=BeanPostProcessorOrderTest")
public class BeanPostProcessorOrderTest {

    @Autowired
    private HighOrderBeanPostProcessor highOrderBeanPostProcessor;

    @Autowired
    private BeanPostProcessorOrderBean beanPostProcessorOrderBean;

    @Autowired
    private SofaRuntimeContext         sofaRuntimeContext;

    @Test
    public void testClientFactoryInject() {
        Assert.assertNotNull(highOrderBeanPostProcessor.getClientFactory());
    }

    @Test
    public void testSofaRuntimeContextInject() {
        Assert.assertNotNull(highOrderBeanPostProcessor.getSofaRuntimeContext());
    }

    @Test
    public void testShutDownHook() throws Exception {
        List<RuntimeShutdownAware> applicationShutdownAwares = getApplicationShutdownAwares();
        Assert.assertNotNull(applicationShutdownAwares);
        Assert.assertTrue(applicationShutdownAwares.contains(highOrderBeanPostProcessor));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShutDownHookReordered() throws Exception {
        List<RuntimeShutdownAware> applicationShutdownAwareList = getApplicationShutdownAwares();
        BeanPostProcessorOrderBean testBeanAware = null;
        for (RuntimeShutdownAware applicationShutdownAware : applicationShutdownAwareList) {
            if (BeanPostProcessorOrderBean.class.getName().equals(
                applicationShutdownAware.getClass().getName())) {
                testBeanAware = (BeanPostProcessorOrderBean) applicationShutdownAware;
            }
        }
        Assert.assertNotNull(testBeanAware);
        Assert.assertEquals(testBeanAware, beanPostProcessorOrderBean);
        Assert.assertTrue(testBeanAware.isEnhancedByLowOrderPostProcessor());
    }

    @Test
    public void testSofaReference() throws Exception {
        Assert.assertNotNull(highOrderBeanPostProcessor.getSampleService());
    }

    @SuppressWarnings("unchecked")
    private List<RuntimeShutdownAware> getApplicationShutdownAwares() throws Exception {
        SofaRuntimeManager sofaRuntimeManager = sofaRuntimeContext.getSofaRuntimeManager();
        Field applicationShutdownAwaresField = sofaRuntimeManager.getClass().getDeclaredField(
            "runtimeShutdownAwares");
        applicationShutdownAwaresField.setAccessible(true);
        return (List<RuntimeShutdownAware>) applicationShutdownAwaresField.get(sofaRuntimeManager);
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    static class BeanPostProcessorOrderTestConfiguration {
        @Bean
        public HighOrderBeanPostProcessor highOrderBeanPostProcessor() {
            return new HighOrderBeanPostProcessor();
        }

        @Bean
        public LowOrderBeanPostProcessor lowOrderBeanPostProcessor() {
            return new LowOrderBeanPostProcessor();
        }

        @Bean
        public DefaultSampleService defaultSampleService() {
            return new DefaultSampleService("defaultSampleService");
        }

        @Bean
        public BeanPostProcessorOrderBean beanPostProcessorOrderBean() {
            return new BeanPostProcessorOrderBean();
        }
    }
}