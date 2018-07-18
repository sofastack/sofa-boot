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

import com.alipay.sofa.ark.spi.service.PriorityOrdered;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.spring.ClientFactoryBeanPostProcessor;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SofaRuntimePropertiesTest {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @After
    public void closeContext() {
        this.applicationContext.close();
    }

    @Test
    public void testProperties() {
        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.disableJvmFirst=true");
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(MockBeanPostProcessor.class.getCanonicalName());
        this.applicationContext.registerBeanDefinition("mockBpp", beanDefinition);
        this.applicationContext.refresh();
        this.applicationContext.getBeansOfType(BeanPostProcessor.class);

        assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));

        List<BeanPostProcessor> beanPostProcessors = ((AbstractAutowireCapableBeanFactory) this.applicationContext
            .getBeanFactory()).getBeanPostProcessors();
        int clientFactoryBeanPostProcessorIndex = getIndexOfType(beanPostProcessors,
            ClientFactoryBeanPostProcessor.class);
        int mockBeanPostProcessorIndex = getIndexOfType(beanPostProcessors,
            MockBeanPostProcessor.class);
        Assert.assertTrue(clientFactoryBeanPostProcessorIndex != -1);
        Assert.assertTrue(mockBeanPostProcessorIndex != -1);
        Assert.assertTrue(clientFactoryBeanPostProcessorIndex == mockBeanPostProcessorIndex + 1);
    }

    private int getIndexOfType(List<BeanPostProcessor> beanPostProcessors, Class type) {
        for (BeanPostProcessor bpp : beanPostProcessors) {
            if (bpp.getClass().isAssignableFrom(type)) {
                return beanPostProcessors.indexOf(bpp);
            }
        }
        return -1;
    }

    public static class MockBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

        @Override
        public int getPriority() {
            return LOWEST_PRECEDENCE - 1;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                                   throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                                  throws BeansException {
            return bean;
        }
    }
}