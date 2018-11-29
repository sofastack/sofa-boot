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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;

import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.runtime.beans.impl.XmlAnnotationSampleService;
import com.alipay.sofa.runtime.beans.impl.XmlSampleServiceWithUniqueId;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.aop.SampleServiceAspect;
import com.alipay.sofa.runtime.integration.base.AbstractTestBase;
import com.alipay.sofa.runtime.integration.features.SampleServiceAnnotationImplWithMethod;
import com.alipay.sofa.runtime.integration.features.SampleServiceAnnotationImplWithUniqueId;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
public class IntegrationTest extends AbstractTestBase {
    @Test
    public void testSofaClientFactoryAnnotationTest() {
        Assert.assertNotNull(awareTest);
        Assert.assertNotNull(awareTest.getClientFactoryAware());
        Assert.assertNotNull(awareTest.getClientFactory());
        Assert.assertNotNull(awareTest.getReferenceClient());
        Assert.assertNotNull(awareTest.getServiceClient());
    }

    @Test
    public void testSofaRuntimeAwareTest() {
        Assert.assertNotNull(awareTest);
        Assert.assertNotNull(awareTest.getSofaRuntimeContext());

        SofaRuntimeContext sofaRuntimeContext = awareTest.getSofaRuntimeContext();
        Assert.assertTrue(sofaRuntimeContext.getAppName().equals("runtime-test"));
        Assert.assertTrue(sofaRuntimeContext.getClientFactory()
            .equals(awareTest.getClientFactory()));

    }

    @Test
    public void testHealthChecker() {
        Assert.assertNotNull(awareTest.getApplicationContext());

        ApplicationContext context = awareTest.getApplicationContext();
        Assert.assertNotNull(context.getBean("sofaComponentHealthChecker"));

        HealthChecker healthChecker = (HealthChecker) context.getBean("sofaComponentHealthChecker");
        Assert.assertTrue(healthChecker.isHealthy().getStatus().equals(Status.UP));
        Assert.assertEquals("SOFABoot-Components", healthChecker.getComponentName());
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT,
            healthChecker.getRetryCount());
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL,
            healthChecker.getRetryTimeInterval());
        Assert.assertEquals(true, healthChecker.isStrictCheck());
    }

    @Test
    public void testServiceAndReference() {
        Assert.assertEquals(awareTest.getSampleServiceAnnotationWithUniqueId().service(),
            "SampleServiceAnnotationImplWithUniqueId");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());

        Assert.assertEquals(awareTest.getSampleServiceAnnotationImplWithMethod().service(),
            "SampleServiceAnnotationImplWithMethod");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());

        // service published by serviceClient, not create spring bean, aop won't take effect.
        Assert.assertEquals(awareTest.getSampleServicePublishedByServiceClient().service(),
            "SampleServiceImpl published by service client.");

        Assert.assertEquals(
            ((SampleService) awareTest.getApplicationContext().getBean("xmlReference")).service(),
            "XmlSampleService");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());

        Assert.assertEquals(
            ((SampleService) awareTest.getApplicationContext().getBean("xmlReferenceWithUniqueId"))
                .service(), "XmlSampleServiceWithUniqueId");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());

        Assert.assertEquals(awareTest.getServiceWithoutInterface().service(),
            "ServiceWithoutInterface");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());

        Assert.assertEquals(awareTest.getXmlAnnotationSampleService().service(),
            "XmlAnnotationSampleService");
        Assert.assertTrue(SampleServiceAspect.isAspectInvoked());
    }

    @Test
    public void testFactoryBean() {
        ApplicationContext applicationContext = awareTest.getApplicationContext();
        ServiceFactoryBean serviceFactoryBean;
        ReferenceFactoryBean referenceFactoryBean;

        /**
         * {@link com.alipay.sofa.runtime.beans.impl.MethodBeanClassAnnotationSampleService}
         **/
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                         "methodBeanClassAnnotationSampleService"));
        Assert.assertTrue(serviceFactoryBean.isApiType());
        referenceFactoryBean = (ReferenceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                         "methodBeanClassAnnotationSampleService"));
        Assert.assertTrue(serviceFactoryBean.isApiType());

        /**
         * {@link com.alipay.sofa.runtime.integration.base.AbstractTestBase.IntegrationTestConfiguration.BeforeConfiguration}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                         "methodBeanMethodAnnotationSampleService"));
        Assert.assertTrue(serviceFactoryBean.isApiType());
        referenceFactoryBean = (ReferenceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                         "methodBeanMethodAnnotationSampleService"));
        Assert.assertTrue(referenceFactoryBean.isApiType());

        /**
         * {@link XmlAnnotationSampleService}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                         "xmlAnnotationSampleService"));
        Assert.assertTrue(serviceFactoryBean.isApiType());
        referenceFactoryBean = (ReferenceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                         "xmlAnnotationSampleService"));
        Assert.assertTrue(referenceFactoryBean.isApiType());

        /**
         * {@link com.alipay.sofa.runtime.beans.impl.XmlSampleService}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, ""));
        Assert.assertFalse(serviceFactoryBean.isApiType());
        referenceFactoryBean = (ReferenceFactoryBean) applicationContext.getBean("&xmlReference");
        Assert.assertFalse(referenceFactoryBean.isApiType());

        /**
         * {@link XmlSampleServiceWithUniqueId}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator
                         .generateSofaServiceBeanName(SampleService.class, "xml"));
        Assert.assertFalse(serviceFactoryBean.isApiType());
        referenceFactoryBean = (ReferenceFactoryBean) applicationContext
            .getBean("&xmlReferenceWithUniqueId");
        Assert.assertFalse(referenceFactoryBean.isApiType());

        /**
         * {@link SampleServiceAnnotationImplWithMethod}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                         "method"));
        Assert.assertTrue(serviceFactoryBean.isApiType());

        /**
         * {@link SampleServiceAnnotationImplWithUniqueId}
         */
        serviceFactoryBean = (ServiceFactoryBean) applicationContext
            .getBean("&"
                     + SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                         "annotation"));
        Assert.assertTrue(serviceFactoryBean.isApiType());
    }

    @Test
    public void testParameterSofaReference() {
        ApplicationContext applicationContext = awareTest.getApplicationContext();
        SampleService parameterAnnotationSampleService = (SampleService) applicationContext
            .getBean("parameterAnnotationSampleService");

        SampleService xmlAnnotationSampleService = (SampleService) applicationContext
            .getBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                "xmlAnnotationSampleService"));

        SampleService methodBeanClassAnnotationSampleService = (SampleService) applicationContext
            .getBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                "methodBeanClassAnnotationSampleService"));

        SampleService methodBeanMethodAnnotationSampleService = (SampleService) applicationContext
            .getBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(SampleService.class,
                "methodBeanMethodAnnotationSampleService"));

        Assert.assertEquals(
            parameterAnnotationSampleService.service(),
            xmlAnnotationSampleService.service() + "@"
                    + methodBeanClassAnnotationSampleService.service() + "@"
                    + methodBeanMethodAnnotationSampleService.service());
    }

    @Test
    public void testServiceFactoryBean() {
        ApplicationContext applicationContext = awareTest.getApplicationContext();
        applicationContext.getBeansOfType(ServiceFactoryBean.class).forEach((key, value) -> {
            Assert.assertTrue(key.startsWith("&ServiceFactoryBean#"));
        });
    }
}