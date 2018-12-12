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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashSet;

import com.alipay.sofa.runtime.spring.ReferenceAnnotationBeanPostProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;

/**
 * @author xuanbei 18/5/15
 */
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReferenceRegisterHelper.class, ReferenceAnnotationBeanPostProcessor.class })
public class ReferenceAnnotationBeanPostProcessorTest {
    @Test
    public void testCreateReferenceProxy() throws Exception {
        Class clazz = Class
            .forName("com.alipay.sofa.runtime.spring.ReferenceAnnotationBeanPostProcessor");
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
            ReferenceAnnotationBeanPostProcessorTest.class.getClassLoader());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(
            applicationContext.getBean(SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
                SofaRuntimeContext.class)).thenReturn(sofaRuntimeContext);

        boolean hasException = false;
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
        ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor = new ReferenceAnnotationBeanPostProcessor(
            applicationContext, sofaRuntimeContext, null, bindingConverterFactory);
        try {
            createReferenceProxy.invoke(referenceAnnotationBeanPostProcessor, sofaReference,
                ReferenceAnnotationBeanPostProcessorTest.class);
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
            ReferenceAnnotationBeanPostProcessorTest.class, InterfaceMode.annotation, true);
        PowerMockito
            .whenNew(ReferenceImpl.class)
            .withArguments("uniqueId", ReferenceAnnotationBeanPostProcessorTest.class,
                InterfaceMode.annotation, true).thenReturn(referenceImpl);
        createReferenceProxy.invoke(referenceAnnotationBeanPostProcessor, sofaReference,
            ReferenceAnnotationBeanPostProcessorTest.class);

        ReferenceRegisterHelper.registerReference(referenceImpl, null, sofaRuntimeContext);
    }
}
