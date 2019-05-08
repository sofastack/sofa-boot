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

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

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

import java.util.Collection;

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
        Assert.assertTrue(referenceFactoryBean.isApiType());

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

    @Test
    public void testReferenceBinding() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ReferenceComponent serializeTrueViaAnnotation = null;
        ReferenceComponent defaultSerializeFalseViaAnnotation = null;
        ReferenceComponent defaultElement = null;
        ReferenceComponent element = null;
        ReferenceComponent noneUniqueId = null;
        Collection<ComponentInfo> componentInfos = componentManager
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        for (ComponentInfo componentInfo : componentInfos) {
            String rawName = componentInfo.getName().getRawName();
            if (rawName.contains(getReferenceComponentName(SampleService.class,
                "serializeTrueViaAnnotation").getRawName())) {
                serializeTrueViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(getReferenceComponentName(SampleService.class,
                "defaultSerializeFalseViaAnnotation").getRawName())) {
                defaultSerializeFalseViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(getReferenceComponentName(SampleService.class,
                "default-element").getRawName())) {
                defaultElement = (ReferenceComponent) componentInfo;
            } else if (componentInfo.getName().getRawName()
                .contains(getReferenceComponentName(SampleService.class, "element").getRawName())) {
                element = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(":#")
                       && rawName.contains(getReferenceComponentName(SampleService.class, "")
                           .getRawName())) {
                noneUniqueId = (ReferenceComponent) componentInfo;
            }
        }
        Assert.assertNotNull(serializeTrueViaAnnotation);
        Assert.assertNotNull(defaultSerializeFalseViaAnnotation);
        Assert.assertNotNull(defaultElement);
        Assert.assertNotNull(element);
        Assert.assertNotNull(noneUniqueId);

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeTrueViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultSerializeFalseViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultElement.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) element.getReference().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) noneUniqueId.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());
    }

    @Test
    public void testServiceBinding() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ServiceComponent serializeFalseViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class,
                "serializeFalseViaAnnotation"));
        ServiceComponent defaultSerializeTrueViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class,
                "defaultSerializeTrueViaAnnotation"));
        ServiceComponent defaultElement = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, "default-element"));
        ServiceComponent element = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, "element"));
        ServiceComponent noneUniqueId = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, ""));

        Assert.assertNotNull(serializeFalseViaAnnotation);
        Assert.assertNotNull(defaultSerializeTrueViaAnnotation);
        Assert.assertNotNull(defaultElement);
        Assert.assertNotNull(element);
        Assert.assertNotNull(noneUniqueId);

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeFalseViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultSerializeTrueViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultElement.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) element.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) noneUniqueId.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());
    }

    private ComponentName getServiceComponentName(Class clazz, String uniqueId) {
        return ComponentNameFactory.createComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
            clazz, uniqueId);
    }

    private ComponentName getReferenceComponentName(Class clazz, String uniqueId) {
        return ComponentNameFactory.createComponentName(
            ReferenceComponent.REFERENCE_COMPONENT_TYPE, clazz, uniqueId);
    }
}