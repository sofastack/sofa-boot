package com.alipay.sofa.boot.tracer.kafka;

import com.sofa.alipay.tracer.plugins.kafkamq.factories.SofaTracerKafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ProducerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KafkaProducerBeanFactoryPostProcessor}.
 *
 * @author huzijie
 * @version KafkaProducerBeanFactoryPostProcessorTests.java, v 0.1 2023年01月09日 7:21 PM huzijie Exp $
 */
@SuppressWarnings("rawtypes")
public class KafkaProducerBeanFactoryPostProcessorTests {

    private final KafkaProducerBeanFactoryPostProcessor kafkaProducerFactoryBeanPostProcessor = new KafkaProducerBeanFactoryPostProcessor();

    @Test
    public void wrapKafkaConsumerFactoryBean() {
        ProducerFactory producerFactory = new EmptyProducerFactory();
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessBeforeInitialization(producerFactory, "producerFactory");
        assertThat(bean).isNotEqualTo(producerFactory);
        assertThat(bean).isInstanceOf(SofaTracerKafkaProducerFactory.class);
    }

    @Test
    public void skipNotKafkaConsumerFactory() {
        Object object = new Object();
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessBeforeInitialization(object, "producerFactory");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerKafkaProducerFactory.class);
    }

    @Test
    public void skipTransformedKafkaConsumerFactory() {
        ProducerFactory producerFactory = new EmptyProducerFactory();
        SofaTracerKafkaProducerFactory sofaTracerKafkaProducerFactory = new SofaTracerKafkaProducerFactory(producerFactory);
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessBeforeInitialization(sofaTracerKafkaProducerFactory, "producerFactory");
        assertThat(bean).isEqualTo(sofaTracerKafkaProducerFactory);
    }

    static class EmptyProducerFactory implements ProducerFactory {

        @Override
        public Producer createProducer() {
            return null;
        }
    }

}
