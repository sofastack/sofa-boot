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
package com.alipay.sofa.boot.autoconfigure.tracer.rocketmq;

import com.alipay.sofa.boot.tracer.rocketmq.RocketMqConsumerPostProcessor;
import com.alipay.sofa.boot.tracer.rocketmq.RocketMqProducerPostProcessor;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.support.RocketMQListenerContainer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for rocketmq.
 *
 * @author linnan
 * @since 3.9.1
 */
@AutoConfiguration(before = RocketMQAutoConfiguration.class)
@ConditionalOnClass({ MQProducer.class, RocketMQListenerContainer.class, RocketMqProducerPostProcessor.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.rocketmq.enabled", havingValue = "enable", matchIfMissing = true)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RocketMqProducerPostProcessor sofaTracerRocketMqProducerPostProcessor() {
        return new RocketMqProducerPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RocketMqConsumerPostProcessor sofaTracerRocketMqConsumerPostProcessor() {
        return new RocketMqConsumerPostProcessor();
    }
}
