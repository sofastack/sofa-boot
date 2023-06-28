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
package com.alipay.sofa.boot.autoconfigure.tracer.kafka;

import com.alipay.sofa.boot.tracer.kafka.KafkaConsumerFactoryBeanPostProcessor;
import com.alipay.sofa.boot.tracer.kafka.KafkaProducerBeanFactoryPostProcessor;
import com.sofa.alipay.tracer.plugins.kafkamq.aspect.KafkaListenerSofaTracerAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.ProducerFactory;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Kafka.
 *
 * @author chenchen6  2020/9/2 21:56
 * @author huzijie
 * @since 3.9.1
 */
@AutoConfiguration(after = org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ ProducerFactory.class, KafkaConsumerFactoryBeanPostProcessor.class,
                     KafkaListenerSofaTracerAspect.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static KafkaConsumerFactoryBeanPostProcessor kafkaConsumerFactoryPostProcessor() {
        return new KafkaConsumerFactoryBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public static KafkaProducerBeanFactoryPostProcessor kafkaProducerFactoryPostProcessor() {
        return new KafkaProducerBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaListenerSofaTracerAspect kafkaListenerSofaTracerAspect() {
        return new KafkaListenerSofaTracerAspect();
    }
}
