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
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessAfterInitialization(
            producerFactory, "producerFactory");
        assertThat(bean).isNotEqualTo(producerFactory);
        assertThat(bean).isInstanceOf(SofaTracerKafkaProducerFactory.class);
    }

    @Test
    public void skipNotKafkaConsumerFactory() {
        Object object = new Object();
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessAfterInitialization(object,
            "producerFactory");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerKafkaProducerFactory.class);
    }

    @Test
    public void skipTransformedKafkaConsumerFactory() {
        ProducerFactory producerFactory = new EmptyProducerFactory();
        SofaTracerKafkaProducerFactory sofaTracerKafkaProducerFactory = new SofaTracerKafkaProducerFactory(
            producerFactory);
        Object bean = kafkaProducerFactoryBeanPostProcessor.postProcessAfterInitialization(
            sofaTracerKafkaProducerFactory, "producerFactory");
        assertThat(bean).isEqualTo(sofaTracerKafkaProducerFactory);
    }

    static class EmptyProducerFactory implements ProducerFactory {

        @Override
        public Producer createProducer() {
            return null;
        }
    }

}
