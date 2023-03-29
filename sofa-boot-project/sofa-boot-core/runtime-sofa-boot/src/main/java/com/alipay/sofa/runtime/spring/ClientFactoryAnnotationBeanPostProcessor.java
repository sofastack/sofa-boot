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

import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.spring.singleton.SingletonSofaPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * {@link ClientFactoryAware}
 * {@link SofaClientFactory} handler
 *
 * @author xuanbei 18/3/2
 */
@SingletonSofaPostProcessor
public class ClientFactoryAnnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private ClientFactory clientFactory;

    public ClientFactoryAnnotationBeanPostProcessor(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName)
                                                                                     throws BeansException {
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
                    throw new RuntimeException(ErrorCode.convert("01-02000"));
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
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
