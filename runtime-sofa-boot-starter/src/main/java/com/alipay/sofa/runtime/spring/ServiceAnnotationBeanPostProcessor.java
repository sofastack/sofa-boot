/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.SpringImplementationImpl;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author xuanbei 18/3/2
 */
public class ServiceAnnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private SofaRuntimeContext sofaRuntimeContext;
    private ApplicationContext applicationContext;

    public ServiceAnnotationBeanPostProcessor(SofaRuntimeContext sofaRuntimeContext,
                                              ApplicationContext applicationContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.applicationContext = applicationContext;
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

        Implementation implementation = new SpringImplementationImpl(beanName, applicationContext);
        implementation.setTarget(bean);
        Service service = new ServiceImpl(sofaServiceAnnotation.uniqueId(), interfaceType,
            InterfaceMode.annotation, bean);
        service.addBinding(new JvmBinding());
        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            sofaRuntimeContext);
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
                    interfaceType, InterfaceMode.annotation, false, false);
                reference.addBinding(new JvmBinding());
                Object proxy = ReferenceRegisterHelper.registerReference(reference,
                    sofaRuntimeContext);
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
}
