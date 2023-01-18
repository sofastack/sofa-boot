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

import com.alipay.sofa.tracer.boot.rocketmq.processor.SofaTracerRocketMqConsumerPostProcessor;
import com.alipay.sofa.tracer.boot.rocketmq.processor.SofaTracerRocketMqProducerPostProcessor;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.support.RocketMQListenerContainer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SofaTracerRocketMqAutoConfiguration.
 *
 * @author linnan
 * @since 3.9.1
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RocketMQAutoConfiguration.class)
@ConditionalOnClass({ MQProducer.class, RocketMQListenerContainer.class,
                     SofaTracerRocketMqProducerPostProcessor.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.rocketmq", value = "enable", matchIfMissing = true)
public class SofaTracerRocketMqAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public SofaTracerRocketMqProducerPostProcessor sofaTracerRocketMqProducerPostProcessor() {
        return new SofaTracerRocketMqProducerPostProcessor();
    }

    @ConditionalOnMissingBean
    @Bean
    public SofaTracerRocketMqConsumerPostProcessor sofaTracerRocketMqConsumerPostProcessor() {
        return new SofaTracerRocketMqConsumerPostProcessor();
    }
}
