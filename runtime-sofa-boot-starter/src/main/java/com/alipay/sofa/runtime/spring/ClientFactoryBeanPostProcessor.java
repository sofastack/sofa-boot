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

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *  {@link ClientFactoryAware}
 * {@link SofaClientFactory} handler
 *
 * @author xuanbei 18/3/2
 */
public class ClientFactoryBeanPostProcessor implements BeanPostProcessor {

    private ClientFactory clientFactory;

    public ClientFactoryBeanPostProcessor(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName)
        throws BeansException {
        if (bean instanceof ClientFactoryAware) {
            ((ClientFactoryAware) bean).setClientFactory(clientFactory);
        }

        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.getType().equals(ClientFactory.class)) {
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, bean, clientFactory);
                } else if ((clientFactory instanceof ClientFactoryImpl)
                    && ((ClientFactoryImpl) clientFactory).getAllClientTypes().contains(
                        field.getType())) {
                    Object client = clientFactory.getClient(field.getType());

                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, bean, client);
                } else {
                    throw new RuntimeException(
                        "Field annotated by ClientFactorySetter must be of type"
                            + " ClientFactory or client store in the ClientFactory.");
                }
            }
        }, new ReflectionUtils.FieldFilter() {

            @Override
            public boolean matches(Field field) {
                return !Modifier.isStatic(field.getModifiers())
                    && field.isAnnotationPresent(SofaClientFactory.class);
            }
        });

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        return bean;
    }
}
