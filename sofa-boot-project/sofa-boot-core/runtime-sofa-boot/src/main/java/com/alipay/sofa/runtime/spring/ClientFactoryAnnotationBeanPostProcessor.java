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

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.impl.ClientFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

/**
 * Implementation of {@link BeanPostProcessor} to inject value to field with {@link SofaClientFactory}.
 *
 * @author xuanbei 18/3/2
 */
@SingletonSofaPostProcessor
public class ClientFactoryAnnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    private final ClientFactory clientFactory;

    public ClientFactoryAnnotationBeanPostProcessor(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName)
                                                                                     throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (ClientFactory.class.isAssignableFrom(field.getType())) {
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
        }, field -> !Modifier.isStatic(field.getModifiers())
               && field.isAnnotationPresent(SofaClientFactory.class));

        return bean;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
