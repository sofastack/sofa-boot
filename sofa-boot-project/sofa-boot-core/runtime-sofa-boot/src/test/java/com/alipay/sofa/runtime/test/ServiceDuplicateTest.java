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

import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.ServiceBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.ClientFactoryBean;
import com.alipay.sofa.runtime.test.beans.service.SofaServiceBeanService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.alipay.sofa.runtime.service.component.ServiceComponent.SERVICE_COMPONENT_TYPE;

/**
 * ServiceDuplicateTest
 *
 * @author xunfang
 * @version ServiceDuplicateTest.java, v 0.1 2023/7/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.application.name=ServiceDuplicateTest",
                                  "com.alipay.sofa.boot.service-duplicate=false" })
@Import({ SofaServiceBeanService.class, ClientFactoryBean.class })
public class ServiceDuplicateTest implements BeanFactoryAware {
    @Autowired
    private SampleService                   sofaServiceBeanService;

    @Autowired
    private ClientFactoryBean               clientFactoryBean;

    @Autowired
    private ApplicationContext              ctx;

    private BeanFactory                     beanFactory;

    @Autowired
    private ServiceBeanFactoryPostProcessor serviceBeanFactoryPostProcessor;

    @Test
    public void testAnnotationServiceDuplicate() {
        SofaRuntimeConfigurationProperties configurationProperties = ctx
            .getBean(SofaRuntimeConfigurationProperties.class);
        Assert.assertFalse(configurationProperties.isServiceDuplicate());

        String serviceId = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
            "sofaServiceBeanService", null);

        try {
            serviceBeanFactoryPostProcessor
                .postProcessBeanFactory((ConfigurableListableBeanFactory) beanFactory);
            throw new IllegalStateException("Service: " + serviceId
                                            + " cannot be registered duplicate");
        } catch (ServiceRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains(ErrorCode.convert("01-00203", serviceId)));
        }

    }

    @Test
    public void testDynamicServiceDuplicate() {
        SofaRuntimeConfigurationProperties configurationProperties = ctx
            .getBean(SofaRuntimeConfigurationProperties.class);
        Assert.assertFalse(configurationProperties.isServiceDuplicate());

        ServiceClient serviceClient = clientFactoryBean.getClientFactory().getClient(
            ServiceClient.class);
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(sofaServiceBeanService);
        serviceParam.setInterfaceType(SampleService.class);
        serviceParam.setUniqueId("sofaServiceBeanService");
        ComponentName componentName = ComponentNameFactory.createComponentName(
            SERVICE_COMPONENT_TYPE, SampleService.class, "sofaServiceBeanService");
        try {
            serviceClient.service(serviceParam);
            throw new IllegalStateException("Service: " + componentName
                                            + " cannot be registered duplicate");
        } catch (ServiceRuntimeException e) {
            Assert
                .assertTrue(e.getMessage().contains(ErrorCode.convert("01-03002", componentName)));
        }

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import({ RuntimeConfiguration.class })
    static class ServiceBeanTestConfiguration {

    }
}
