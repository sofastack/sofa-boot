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
