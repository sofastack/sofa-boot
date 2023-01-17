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

import com.alipay.sofa.boot.annotation.PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder;
import com.alipay.sofa.boot.annotation.PlaceHolderBinder;
import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.boot.log.SofaLogger;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

/**
 * Responsible to inject field annotated by @SofaReference and
 * invoke setXX method annotated by @SofaReference
 *
 * @author xuanbei 18/5/9
 */
@SingletonSofaPostProcessor
public class ReferenceAnnotationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, PriorityOrdered {

    private final PlaceHolderBinder binder = new DefaultPlaceHolderBinder();

    private final SofaRuntimeContext      sofaRuntimeContext;

    private final BindingAdapterFactory   bindingAdapterFactory;

    private final BindingConverterFactory bindingConverterFactory;

    private Environment             environment;

    private ApplicationContext      applicationContext;
    /**
     * To construct a ReferenceAnnotationBeanPostProcessor via a Spring Bean
     * sofaRuntimeContext and sofaRuntimeProperties will be obtained from applicationContext
     * @param sofaRuntimeContext
     * @param bindingAdapterFactory
     * @param bindingConverterFactory
     */
    public ReferenceAnnotationBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext,
                                                BindingAdapterFactory bindingAdapterFactory,
                                                BindingConverterFactory bindingConverterFactory) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.bindingConverterFactory = bindingConverterFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        processSofaReference(bean);
        return bean;
    }

    private void processSofaReference(final Object bean) {
        final Class<?> beanClass = bean.getClass();

        ReflectionUtils.doWithFields(beanClass, field -> {
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
        }, field -> {
            if (!field.isAnnotationPresent(SofaReference.class)) {
                return false;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                SofaLogger.warn(
                    "SofaReference annotation is not supported on static fields: {}", field);
                return false;
            }
            return true;
        });

        ReflectionUtils.doWithMethods(beanClass, method -> {
            Class<?>[] parameterTypes = method.getParameterTypes();
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
        }, method -> method.isAnnotationPresent(SofaReference.class));
    }

    private Object createReferenceProxy(SofaReference sofaReferenceAnnotation,
                                        Class<?> interfaceType) {
        Reference reference = new ReferenceImpl(sofaReferenceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, sofaReferenceAnnotation.jvmFirst());
        BindingConverter bindingConverter = bindingConverterFactory
            .getBindingConverter(new BindingType(sofaReferenceAnnotation.binding().bindingType()));
        if (bindingConverter == null) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00200", sofaReferenceAnnotation
                .binding().bindingType()));
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
            sofaRuntimeContext, applicationContext);
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class DefaultPlaceHolderBinder implements PlaceHolderBinder {
        @Override
        public String bind(String text) {
            return environment.resolvePlaceholders(text);
        }
    }
}
