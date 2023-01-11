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

import com.sofa.alipay.tracer.plugins.rabbitmq.interceptor.SofaTracerConsumeInterceptor;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RabbitMqBeanPostProcessor}.
 *
 * @author huzijie
 * @version RabbitMqBeanPostProcessorTests.java, v 0.1 2023年01月09日 8:00 PM huzijie Exp $
 */
@SuppressWarnings("rawtypes")
public class RabbitMqBeanPostProcessorTests {

    private final RabbitMqBeanPostProcessor rabbitMqBeanPostProcessor = new RabbitMqBeanPostProcessor();

    @Test
    public void registerTracingInterceptorToAbstractRabbitListenerContainerFactory() {
        AbstractRabbitListenerContainerFactory containerFactory = new EmptyRabbitListenerContainerFactory();
        Object bean = rabbitMqBeanPostProcessor.postProcessBeforeInitialization(containerFactory, "containerFactory");
        assertThat(bean).isEqualTo(containerFactory);
        assertThat(containerFactory.getAdviceChain()).anyMatch(advice -> advice instanceof SofaTracerConsumeInterceptor);

        bean = rabbitMqBeanPostProcessor.postProcessBeforeInitialization(containerFactory, "containerFactory");
        assertThat(bean).isEqualTo(containerFactory);
        assertThat(containerFactory.getAdviceChain())
                .anyMatch(advice -> advice instanceof SofaTracerConsumeInterceptor).hasSize(1);
    }

    @Test
    public void registerTracingInterceptorToAbstractMessageListenerContainer() {
        EmptyMessageListenerContainer container = new EmptyMessageListenerContainer();
        Object bean = rabbitMqBeanPostProcessor.postProcessBeforeInitialization(container, "container");
        assertThat(bean).isEqualTo(container);
        assertThat(container.getAdviceChain()).anyMatch(advice -> advice instanceof SofaTracerConsumeInterceptor);

        bean = rabbitMqBeanPostProcessor.postProcessBeforeInitialization(container, "container");
        assertThat(bean).isEqualTo(container);
        assertThat(container.getAdviceChain())
                .anyMatch(advice -> advice instanceof SofaTracerConsumeInterceptor).hasSize(1);
    }

    static class EmptyRabbitListenerContainerFactory extends AbstractRabbitListenerContainerFactory {

        @Override
        protected AbstractMessageListenerContainer createContainerInstance() {
            return null;
        }
    }

    static class EmptyMessageListenerContainer extends AbstractMessageListenerContainer {

        @Override
        protected void doInitialize() {

        }

        @Override
        protected void doShutdown() {

        }

        @Override
        public Advice[] getAdviceChain() {
            return super.getAdviceChain();
        }
    }

}
