package com.alipay.sofa.boot.tracer.kafka;

import com.sofa.alipay.tracer.plugins.kafkamq.factories.SofaTracerKafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ConsumerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KafkaConsumerFactoryBeanPostProcessor}.
 *
 * @author huzijie
 * @version KafkaConsumerFactoryBeanPostProcessorTests.java, v 0.1 2023年01月09日 7:21 PM huzijie Exp $
 */
@SuppressWarnings("rawtypes")
public class KafkaConsumerFactoryBeanPostProcessorTests {

    private final KafkaConsumerFactoryBeanPostProcessor kafkaConsumerFactoryBeanPostProcessor = new KafkaConsumerFactoryBeanPostProcessor();

    @Test
    public void wrapKafkaConsumerFactoryBean() {
        ConsumerFactory consumerFactory = new EmptyConsumerFactory();
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessBeforeInitialization(consumerFactory, "consumerFactory");
        assertThat(bean).isNotEqualTo(consumerFactory);
        assertThat(bean).isInstanceOf(SofaTracerKafkaConsumerFactory.class);
    }

    @Test
    public void skipNotKafkaConsumerFactory() {
        Object object = new Object();
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessBeforeInitialization(object, "consumerFactory");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerKafkaConsumerFactory.class);
    }

    @Test
    public void skipTransformedKafkaConsumerFactory() {
        ConsumerFactory consumerFactory = new EmptyConsumerFactory();
        SofaTracerKafkaConsumerFactory sofaTracerKafkaConsumerFactory = new SofaTracerKafkaConsumerFactory(consumerFactory);
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessBeforeInitialization(sofaTracerKafkaConsumerFactory, "consumerFactory");
        assertThat(bean).isEqualTo(sofaTracerKafkaConsumerFactory);
    }

    static class EmptyConsumerFactory implements ConsumerFactory {

        @Override
        public Consumer createConsumer(String s, String s1, String s2) {
            return null;
        }

        @Override
        public boolean isAutoCommit() {
            return false;
        }
    }

}
