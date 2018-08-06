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
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author xuanbei 18/5/9
 */
public class ServiceAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered,
                                               ApplicationContextAware {
    private SofaRuntimeContext      sofaRuntimeContext;
    private BindingAdapterFactory   bindingAdapterFactory;
    private BindingConverterFactory bindingConverterFactory;
    private ApplicationContext      applicationContext;

    /**
     * To construct a ServiceAnnotationBeanPostProcessor via a Spring Bean
     * sofaRuntimeContext and sofaRuntimeProperties will be obtained from applicationContext
     * @param bindingAdapterFactory
     * @param bindingConverterFactory
     */
    public ServiceAnnotationBeanPostProcessor(BindingAdapterFactory bindingAdapterFactory,
                                              BindingConverterFactory bindingConverterFactory) {
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

            if (interfaces == null || interfaces.length == 0) {
                interfaceType = beanClass;
            } else if (interfaces.length == 1) {
                interfaceType = interfaces[0];
            } else {
                throw new ServiceRuntimeException("Bean " + beanName
                                                  + " has more than one interface.");
            }
        }

        Implementation implementation = new DefaultImplementation(bean);
        Service service = new ServiceImpl(sofaServiceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, bean);

        for (SofaServiceBinding sofaServiceBinding : sofaServiceAnnotation.bindings()) {
            handleSofaServiceBinding(service, sofaServiceAnnotation, sofaServiceBinding);
        }

        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            bindingAdapterFactory, getSofaRuntimeContext());
        getSofaRuntimeContext().getComponentManager().register(componentInfo);
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
            public void doWith(Method method) throws IllegalArgumentException,
                                             IllegalAccessException {
                Class[] parameterTypes = method.getParameterTypes();
                Assert.isTrue(parameterTypes.length == 1,
                    "method should have one and only one parameter.");

                SofaReference sofaReferenceAnnotation = method.getAnnotation(SofaReference.class);
                if (sofaReferenceAnnotation == null) {
                    return;
                }

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

    private void handleSofaServiceBinding(Service service, SofaService sofaServiceAnnotation,
                                          SofaServiceBinding sofaServiceBinding) {
        if (JvmBinding.JVM_BINDING_TYPE.getType().equals(sofaServiceBinding.bindingType())) {
            service.addBinding(new JvmBinding());
        } else {
            BindingConverter bindingConverter = bindingConverterFactory
                .getBindingConverter(new BindingType(sofaServiceBinding.bindingType()));
            if (bindingConverter == null) {
                throw new ServiceRuntimeException(
                    "Can not found binding converter for binding type "
                            + sofaServiceBinding.bindingType());
            }

            BindingConverterContext bindingConverterContext = new BindingConverterContext();
            bindingConverterContext.setInBinding(false);
            bindingConverterContext.setApplicationContext(applicationContext);
            bindingConverterContext.setAppName(getSofaRuntimeContext().getAppName());
            bindingConverterContext.setAppClassLoader(getSofaRuntimeContext().getAppClassLoader());
            Binding binding = bindingConverter.convert(sofaServiceAnnotation, sofaServiceBinding,
                bindingConverterContext);
            service.addBinding(binding);
        }
    }

    private Object createReferenceProxy(SofaReference sofaReferenceAnnotation,
                                        Class<?> interfaceType) {
        Reference reference = new ReferenceImpl(sofaReferenceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, sofaReferenceAnnotation.jvmFirst());
        if (JvmBinding.JVM_BINDING_TYPE.getType().equals(
            sofaReferenceAnnotation.binding().bindingType())) {
            reference.addBinding(new JvmBinding());
        } else {
            BindingConverter bindingConverter = bindingConverterFactory
                .getBindingConverter(new BindingType(sofaReferenceAnnotation.binding()
                    .bindingType()));
            if (bindingConverter == null) {
                throw new ServiceRuntimeException(
                    "Can not found binding converter for binding type "
                            + sofaReferenceAnnotation.binding().bindingType());
            }

            BindingConverterContext bindingConverterContext = new BindingConverterContext();
            bindingConverterContext.setInBinding(true);
            bindingConverterContext.setApplicationContext(applicationContext);
            bindingConverterContext.setAppName(getSofaRuntimeContext().getAppName());
            bindingConverterContext.setAppClassLoader(getSofaRuntimeContext().getAppClassLoader());
            Binding binding = bindingConverter.convert(sofaReferenceAnnotation,
                sofaReferenceAnnotation.binding(), bindingConverterContext);
            reference.addBinding(binding);
        }
        return ReferenceRegisterHelper.registerReference(reference, bindingAdapterFactory,
            getSofaRuntimeContext());
    }

    private SofaRuntimeContext getSofaRuntimeContext() {
        if (sofaRuntimeContext == null) {
            sofaRuntimeContext = applicationContext.getBean(
                SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID,
                SofaRuntimeContext.class);
        }
        return sofaRuntimeContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}