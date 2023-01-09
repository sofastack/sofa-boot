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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.sofa.tracer.boot.kafka.processor.KafkaConsumerFactoryPostProcessor;
import com.alipay.sofa.tracer.boot.kafka.processor.KafkaProducerFactoryPostProcessor;
import com.sofa.alipay.tracer.plugins.kafkamq.aspect.KafkaListenerSofaTracerAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author chenchen6  2020/9/2 21:56
 * @since 3.9.1
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnClass({ ProducerFactory.class, ConsumerFactory.class,
                     KafkaConsumerFactoryPostProcessor.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(name = "com.alipay.tracer.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class SofaTracerKafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KafkaConsumerFactoryPostProcessor kafkaConsumerFactoryPostProcessor() {
        return new KafkaConsumerFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaProducerFactoryPostProcessor kafkaProducerFactoryPostProcessor() {
        return new KafkaProducerFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaListenerSofaTracerAspect kafkaListenerSofaTracerAspect() {
        return new KafkaListenerSofaTracerAspect();
    }
}
