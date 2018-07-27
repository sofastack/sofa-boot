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
package com.alipay.sofa.isle;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author xuanbei 18/5/15
 */
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReferenceRegisterHelper.class, ServiceAnnotationBeanPostProcessor.class })
public class ServiceAnnotationBeanPostProcessorTest {
    @Test
    public void testCreateReferenceProxy() throws Exception {
        Class clazz = Class
            .forName("com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor");
        Method createReferenceProxy = clazz.getDeclaredMethod("createReferenceProxy",
            SofaReference.class, Class.class);
        createReferenceProxy.setAccessible(true);

        SofaReference sofaReference = mock(SofaReference.class);
        when(sofaReference.uniqueId()).thenReturn("uniqueId");
        when(sofaReference.jvmFirst()).thenReturn(true);

        SofaReferenceBinding sofaReferenceBinding = mock(SofaReferenceBinding.class);
        when(sofaReference.binding()).thenReturn(sofaReferenceBinding);
        when(sofaReferenceBinding.bindingType()).thenReturn("bolt");

        SofaRuntimeContext sofaRuntimeContext = mock(SofaRuntimeContext.class);
        when(sofaRuntimeContext.getAppName()).thenReturn("testcase");
        when(sofaRuntimeContext.getAppClassLoader()).thenReturn(
            ServiceAnnotationBeanPostProcessorTest.class.getClassLoader());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(
            applicationContext.getBean(SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
                SofaRuntimeContext.class)).thenReturn(sofaRuntimeContext);

        boolean hasException = false;
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor = new ServiceAnnotationBeanPostProcessor(
            null, bindingConverterFactory);
        serviceAnnotationBeanPostProcessor.setApplicationContext(applicationContext);
        try {
            createReferenceProxy.invoke(serviceAnnotationBeanPostProcessor, sofaReference,
                ServiceAnnotationBeanPostProcessorTest.class);
        } catch (Exception e) {
            Assert.assertEquals("Can not found binding converter for binding type bolt", e
                .getCause().getMessage());
            hasException = true;
        }
        Assert.assertTrue(hasException);

        BindingConverter bindingConverter = mock(BindingConverter.class);
        when(bindingConverter.supportBindingType()).thenReturn(new BindingType("bolt"));
        when(bindingConverter.supportTagName()).thenReturn("binding:bolt");
        HashSet<BindingConverter> bindingConverters = new HashSet<>();
        bindingConverters.add(bindingConverter);
        bindingConverterFactory.addBindingConverters(bindingConverters);

        // use power mockito mock static
        PowerMockito.mockStatic(ReferenceRegisterHelper.class);
        ReferenceImpl referenceImpl = new ReferenceImpl("uniqueId",
            ServiceAnnotationBeanPostProcessorTest.class, InterfaceMode.annotation, true);
        PowerMockito
            .whenNew(ReferenceImpl.class)
            .withArguments("uniqueId", ServiceAnnotationBeanPostProcessorTest.class,
                InterfaceMode.annotation, true).thenReturn(referenceImpl);
        createReferenceProxy.invoke(serviceAnnotationBeanPostProcessor, sofaReference,
            ServiceAnnotationBeanPostProcessorTest.class);

        PowerMockito.verifyStatic();
        ReferenceRegisterHelper.registerReference(referenceImpl, null, sofaRuntimeContext);
    }

    @Test
    public void testHandleSofaServiceBinding() throws Exception {
        Class clazz = Class
            .forName("com.alipay.sofa.runtime.spring.ServiceAnnotationBeanPostProcessor");
        Method createReferenceProxy = clazz.getDeclaredMethod("handleSofaServiceBinding",
            Service.class, SofaService.class, SofaServiceBinding.class);
        createReferenceProxy.setAccessible(true);

        Service service = mock(Service.class);
        SofaService sofaService = mock(SofaService.class);
        SofaServiceBinding sofaServiceBinding = mock(SofaServiceBinding.class);

        when(sofaServiceBinding.bindingType()).thenReturn("bolt");

        SofaRuntimeContext sofaRuntimeContext = mock(SofaRuntimeContext.class);
        when(sofaRuntimeContext.getAppName()).thenReturn("testcase");
        when(sofaRuntimeContext.getAppClassLoader()).thenReturn(
            ServiceAnnotationBeanPostProcessorTest.class.getClassLoader());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(
            applicationContext.getBean(SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
                SofaRuntimeContext.class)).thenReturn(sofaRuntimeContext);

        boolean hasException = false;
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor = new ServiceAnnotationBeanPostProcessor(
            null, bindingConverterFactory);
        serviceAnnotationBeanPostProcessor.setApplicationContext(applicationContext);
        try {
            createReferenceProxy.invoke(serviceAnnotationBeanPostProcessor, service, sofaService,
                sofaServiceBinding);
        } catch (Exception e) {
            Assert.assertEquals("Can not found binding converter for binding type bolt", e
                .getCause().getMessage());
            hasException = true;
        }
        Assert.assertTrue(hasException);

        BindingConverter bindingConverter = mock(BindingConverter.class);
        when(bindingConverter.supportBindingType()).thenReturn(new BindingType("bolt"));
        when(bindingConverter.supportTagName()).thenReturn("binding:bolt");
        HashSet<BindingConverter> bindingConverters = new HashSet<>();
        bindingConverters.add(bindingConverter);
        bindingConverterFactory.addBindingConverters(bindingConverters);

        createReferenceProxy.invoke(serviceAnnotationBeanPostProcessor, service, sofaService,
            sofaServiceBinding);
    }
}
