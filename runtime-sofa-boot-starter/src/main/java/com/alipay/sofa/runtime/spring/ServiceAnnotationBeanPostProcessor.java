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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaBinding;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.binding.SofaServiceDefinition;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.config.SofaRuntimeProperties;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author xuanbei 18/5/9
 */
public class ServiceAnnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private SofaRuntimeContext      sofaRuntimeContext;
    private SofaRuntimeProperties   sofaRuntimeProperties;
    private BindingAdapterFactory   bindingAdapterFactory;
    private BindingConverterFactory bindingConverterFactory;

    public ServiceAnnotationBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext,
                                              SofaRuntimeProperties sofaRuntimeProperties,
                                              BindingAdapterFactory bindingAdapterFactory,
                                              BindingConverterFactory bindingConverterFactory) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.sofaRuntimeProperties = sofaRuntimeProperties;
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.bindingConverterFactory = bindingConverterFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        processSofaReference(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        processSofaService(bean, beanName);
        return bean;
    }

    private void processSofaService(Object bean, String beanName) {
        final Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);

        SofaService sofaServiceAnnotation = beanClass.getAnnotation(SofaService.class);

        if (sofaServiceAnnotation == null) {
            return;
        }

        Class<?> interfaceType = sofaServiceAnnotation.interfaceType();

        if (interfaceType.equals(void.class)) {
            Class<?> interfaces[] = beanClass.getInterfaces();

            if (interfaces == null || interfaces.length == 0 || interfaces.length > 1) {
                throw new ServiceRuntimeException(
                    "Bean " + beanName
                            + " does not has any interface or has more than one interface.");
            }

            interfaceType = interfaces[0];
        }

        Implementation implementation = new DefaultImplementation(bean);
        Service service = new ServiceImpl(sofaServiceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, bean);

        for (SofaBinding sofaBinding : sofaServiceAnnotation.bindings()) {
            if ("jvm".equals(sofaBinding.bindingType())) {
                service.addBinding(new JvmBinding());
            } else {
                BindingConverterContext bindingConverterContext = new BindingConverterContext();
                bindingConverterContext.setInBinding(false);
                bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
                bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
                SofaServiceDefinition sofaServiceDefinition = convertSofaService(
                    sofaServiceAnnotation, sofaBinding);
                service.addBinding(bindingConverterFactory.getBindingConverter(
                    new BindingType(sofaBinding.bindingType())).convert(sofaServiceDefinition,
                    bindingConverterContext));
            }
        }

        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            bindingAdapterFactory, sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    private void processSofaReference(final Object bean) {
        final Class<?> beanClass = bean.getClass();

        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                SofaReference sofaReferenceAnnotation = field.getAnnotation(SofaReference.class);

                if (sofaReferenceAnnotation == null) {
                    return;
                }

                Class<?> interfaceType = sofaReferenceAnnotation.interfaceType();

                if (interfaceType.equals(void.class)) {
                    interfaceType = field.getType();
                }

                Reference reference = new ReferenceImpl(sofaReferenceAnnotation.uniqueId(),
                    interfaceType, InterfaceMode.annotation, sofaReferenceAnnotation.jvmFirst());
                if ("jvm".equals(sofaReferenceAnnotation.binding().bindingType())) {
                    reference.addBinding(new JvmBinding());
                } else {
                    BindingConverterContext bindingConverterContext = new BindingConverterContext();
                    bindingConverterContext.setInBinding(false);
                    bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
                    bindingConverterContext.setAppClassLoader(sofaRuntimeContext
                        .getAppClassLoader());
                    SofaServiceDefinition sofaServiceDefinition = convertSofaService(
                        sofaReferenceAnnotation, sofaReferenceAnnotation.binding());
                    reference.addBinding(bindingConverterFactory.getBindingConverter(
                        new BindingType(sofaReferenceAnnotation.binding().bindingType())).convert(
                        sofaServiceDefinition, bindingConverterContext));
                }
                Object proxy = ReferenceRegisterHelper.registerReference(reference,
                    bindingAdapterFactory, sofaRuntimeProperties, sofaRuntimeContext);
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
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private SofaServiceDefinition convertSofaService(SofaReference sofaReferenceAnnotation,
                                                     SofaBinding sofaBinding) {
        SofaServiceDefinition sofaServiceDefinition = convertSofaBinding(sofaBinding);
        sofaServiceDefinition.setInterfaceType(sofaReferenceAnnotation.interfaceType());
        sofaServiceDefinition.setUniqueId(sofaReferenceAnnotation.uniqueId());
        sofaServiceDefinition.setJvmFirst(sofaReferenceAnnotation.jvmFirst());
        return sofaServiceDefinition;
    }

    private SofaServiceDefinition convertSofaService(SofaService sofaServiceAnnotation,
                                                     SofaBinding sofaBinding) {
        SofaServiceDefinition sofaServiceDefinition = convertSofaBinding(sofaBinding);
        sofaServiceDefinition.setInterfaceType(sofaServiceAnnotation.interfaceType());
        sofaServiceDefinition.setUniqueId(sofaServiceAnnotation.uniqueId());
        return sofaServiceDefinition;
    }

    private SofaServiceDefinition convertSofaBinding(SofaBinding sofaBinding) {
        SofaServiceDefinition sofaServiceDefinition = new SofaServiceDefinition();
        sofaServiceDefinition.setAddressWaitTime(sofaBinding.addressWaitTime());
        sofaServiceDefinition.setBindingType(sofaBinding.bindingType());
        sofaServiceDefinition.setCallBackHandler(sofaBinding.callBackHandler());
        sofaServiceDefinition.setDirectUrl(sofaBinding.directUrl());
        sofaServiceDefinition.setFilters(sofaBinding.filters());
        sofaServiceDefinition.setInvokeType(sofaBinding.invokeType());
        sofaServiceDefinition.setRegistry(sofaBinding.registry());
        sofaServiceDefinition.setRetries(sofaBinding.retries());
        sofaServiceDefinition.setTimeout(sofaBinding.timeout());
        sofaServiceDefinition.setUserThreadPool(sofaBinding.userThreadPool());
        sofaServiceDefinition.setWarmUpTime(sofaBinding.warmUpTime());
        sofaServiceDefinition.setWarmUpWeight(sofaBinding.warmUpWeight());
        sofaServiceDefinition.setWeight(sofaBinding.weight());
        return sofaServiceDefinition;
    }
}