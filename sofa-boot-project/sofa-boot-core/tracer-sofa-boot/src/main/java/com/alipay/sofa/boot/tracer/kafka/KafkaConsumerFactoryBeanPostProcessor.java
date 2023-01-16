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

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.sofa.alipay.tracer.plugins.kafkamq.factories.SofaTracerKafkaConsumerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * Implementation of {@link BeanPostProcessor} to wrapper ConsumerFactory in {@link SofaTracerKafkaConsumerFactory}.
 *
 * @author chenchen6   2020/9/3 22:18
 * @author huzijie
 * @since 3.9.1
 */
@SingletonSofaPostProcessor
public class KafkaConsumerFactoryBeanPostProcessor implements BeanPostProcessor {

    @SuppressWarnings("rawtypes")
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        if (bean instanceof ConsumerFactory && !(bean instanceof SofaTracerKafkaConsumerFactory)) {
            return new SofaTracerKafkaConsumerFactory((ConsumerFactory) bean);
        }
        return bean;
    }
}
