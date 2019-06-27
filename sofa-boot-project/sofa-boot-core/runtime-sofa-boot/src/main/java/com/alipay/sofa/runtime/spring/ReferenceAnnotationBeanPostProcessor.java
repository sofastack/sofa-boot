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
package com.alipay.sofa.runtime.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.alipay.sofa.boot.annotation.PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder;
import com.alipay.sofa.boot.annotation.PlaceHolderBinder;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;

/**
 * Responsible to inject field annotated by @SofaReference and
 * invoke setXX method annotated by @SofaReference
 *
 * @author xuanbei 18/5/9
 */
public class ReferenceAnnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private final PlaceHolderBinder binder = new DefaultPlaceHolderBinder();
    private ApplicationContext      applicationContext;
    private SofaRuntimeContext      sofaRuntimeContext;
    private BindingAdapterFactory   bindingAdapterFactory;
    private BindingConverterFactory bindingConverterFactory;
    private Environment             environment;

    /**
     * To construct a ReferenceAnnotationBeanPostProcessor via a Spring Bean
     * sofaRuntimeContext and sofaRuntimeProperties will be obtained from applicationContext
     * @param applicationContext
     * @param sofaRuntimeContext
     * @param bindingAdapterFactory
     * @param bindingConverterFactory
     */
    public ReferenceAnnotationBeanPostProcessor(ApplicationContext applicationContext,
                                                SofaRuntimeContext sofaRuntimeContext,
                                                BindingAdapterFactory bindingAdapterFactory,
                                                BindingConverterFactory bindingConverterFactory) {
        this.applicationContext = applicationContext;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.bindingConverterFactory = bindingConverterFactory;
        this.environment = applicationContext.getEnvironment();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        processSofaReference(bean);
        return bean;
    }

    private void processSofaReference(final Object bean) {
        final Class<?> beanClass = bean.getClass();

        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {

            @Override
            @SuppressWarnings("unchecked")
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                AnnotationWrapperBuilder<SofaReference> builder = AnnotationWrapperBuilder.wrap(
                    field.getAnnotation(SofaReference.class)).withBinder(binder);
                SofaReference sofaReferenceAnnotation = builder.build();

                if (sofaReferenceAnnotation == null) {
                    return;
                }

                Class<?> interfaceType = sofaReferenceAnnotation.interfaceType();
                if (interfaceType.equals(void.class)) {
                    interfaceType = field.getType();
                }

                Object proxy = createReferenceProxy(sofaReferenceAnnotation, interfaceType);
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, bean, proxy);
            }
        }, new ReflectionUtils.FieldFilter() {

            @Override
            public boolean matches(Field field) {
                return !Modifier.isStatic(field.getModifiers())
                       && field.isAnnotationPresent(SofaReference.class);
            }
        });

        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void doWith(Method method) throws IllegalArgumentException,
                                             IllegalAccessException {
                Class[] parameterTypes = method.getParameterTypes();
                Assert.isTrue(parameterTypes.length == 1,
                    "method should have one and only one parameter.");

                SofaReference sofaReferenceAnnotation = method.getAnnotation(SofaReference.class);
                if (sofaReferenceAnnotation == null) {
                    return;
                }
                AnnotationWrapperBuilder<SofaReference> builder = AnnotationWrapperBuilder.wrap(
                    sofaReferenceAnnotation).withBinder(binder);
                sofaReferenceAnnotation = builder.build();

                Class<?> interfaceType = sofaReferenceAnnotation.interfaceType();
                if (interfaceType.equals(void.class)) {
                    interfaceType = parameterTypes[0];
                }

                Object proxy = createReferenceProxy(sofaReferenceAnnotation, interfaceType);
                ReflectionUtils.invokeMethod(method, bean, proxy);
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return method.isAnnotationPresent(SofaReference.class);
            }
        });
    }

    private Object createReferenceProxy(SofaReference sofaReferenceAnnotation,
                                        Class<?> interfaceType) {
        Reference reference = new ReferenceImpl(sofaReferenceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, sofaReferenceAnnotation.jvmFirst());
        BindingConverter bindingConverter = bindingConverterFactory
            .getBindingConverter(new BindingType(sofaReferenceAnnotation.binding().bindingType()));
        if (bindingConverter == null) {
            throw new ServiceRuntimeException("Can not found binding converter for binding type "
                                              + sofaReferenceAnnotation.binding().bindingType());
        }

        BindingConverterContext bindingConverterContext = new BindingConverterContext();
        bindingConverterContext.setInBinding(true);
        bindingConverterContext.setApplicationContext(applicationContext);
        bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
        bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
        Binding binding = bindingConverter.convert(sofaReferenceAnnotation,
            sofaReferenceAnnotation.binding(), bindingConverterContext);
        reference.addBinding(binding);
        return ReferenceRegisterHelper.registerReference(reference, bindingAdapterFactory,
            sofaRuntimeContext);
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    class DefaultPlaceHolderBinder implements PlaceHolderBinder {
        @Override
        public String bind(String text) {
            return environment.resolvePlaceholders(text);
        }
    }
}