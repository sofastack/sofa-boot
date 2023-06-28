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
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessAfterInitialization(
            consumerFactory, "consumerFactory");
        assertThat(bean).isNotEqualTo(consumerFactory);
        assertThat(bean).isInstanceOf(SofaTracerKafkaConsumerFactory.class);
    }

    @Test
    public void skipNotKafkaConsumerFactory() {
        Object object = new Object();
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessAfterInitialization(object,
            "consumerFactory");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerKafkaConsumerFactory.class);
    }

    @Test
    public void skipTransformedKafkaConsumerFactory() {
        ConsumerFactory consumerFactory = new EmptyConsumerFactory();
        SofaTracerKafkaConsumerFactory sofaTracerKafkaConsumerFactory = new SofaTracerKafkaConsumerFactory(
            consumerFactory);
        Object bean = kafkaConsumerFactoryBeanPostProcessor.postProcessAfterInitialization(
            sofaTracerKafkaConsumerFactory, "consumerFactory");
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
