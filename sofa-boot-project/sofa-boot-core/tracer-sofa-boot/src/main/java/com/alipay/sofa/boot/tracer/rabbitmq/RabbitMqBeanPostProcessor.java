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
package com.alipay.sofa.boot.tracer.rabbitmq;

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.sofa.alipay.tracer.plugins.rabbitmq.interceptor.SofaTracerConsumeInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Implementation of {@link BeanPostProcessor} to wrapper register tracing interceptor.
 *
 * @author chenchen6  2020/8/09 20:44
 * @author huzijie
 * @since  3.9.1
 */
@SingletonSofaPostProcessor
public class RabbitMqBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (bean instanceof AbstractRabbitListenerContainerFactory factory) {
            registerTracingInterceptor(factory);
        } else if (bean instanceof AbstractMessageListenerContainer container) {
            registerTracingInterceptor(container);
        }
        return bean;
    }

    @SuppressWarnings("rawtypes")
    private void registerTracingInterceptor(AbstractRabbitListenerContainerFactory factory) {
        Advice[] chain = factory.getAdviceChain();
        Advice[] adviceChainWithTracing = getAdviceChainOrAddInterceptorToChain(chain);
        factory.setAdviceChain(adviceChainWithTracing);
    }

    private void registerTracingInterceptor(AbstractMessageListenerContainer container) {
        Field adviceChainField = ReflectionUtils.findField(AbstractMessageListenerContainer.class,
            "adviceChain");
        ReflectionUtils.makeAccessible(adviceChainField);
        Advice[] chain = (Advice[]) ReflectionUtils.getField(adviceChainField, container);
        Advice[] adviceChainWithTracing = getAdviceChainOrAddInterceptorToChain(chain);
        container.setAdviceChain(adviceChainWithTracing);
    }

    private Advice[] getAdviceChainOrAddInterceptorToChain(Advice... existingAdviceChain) {
        if (existingAdviceChain == null) {
            return new Advice[] { new SofaTracerConsumeInterceptor() };
        }

        for (Advice advice : existingAdviceChain) {
            if (advice instanceof SofaTracerConsumeInterceptor) {
                return existingAdviceChain;
            }
        }

        Advice[] newChain = new Advice[existingAdviceChain.length + 1];
        System.arraycopy(existingAdviceChain, 0, newChain, 0, existingAdviceChain.length);
        newChain[existingAdviceChain.length] = new SofaTracerConsumeInterceptor();

        return newChain;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
