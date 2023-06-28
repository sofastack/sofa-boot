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
package com.alipay.sofa.boot.autoconfigure.tracer.rabbitmq;

import com.alipay.sofa.boot.tracer.rabbitmq.RabbitMqBeanPostProcessor;
import com.sofa.alipay.tracer.plugins.rabbitmq.aspect.SofaTracerSendMessageAspect;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for rabbitmq.
 *
 * @author chenchen6  2020/8/09 20:44
 * @author huzijie
 * @since  3.9.1
 */
@AutoConfiguration(after = RabbitAutoConfiguration.class)
@ConditionalOnClass({ Message.class, RabbitTemplate.class, SofaTracerSendMessageAspect.class,
                     RabbitMqBeanPostProcessor.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RabbitMqAutoConfiguration {

    @Bean
    @ConditionalOnBean(RabbitTemplate.class)
    public SofaTracerSendMessageAspect rabbitMqSendTracingAspect(RabbitTemplate rabbitTemplate) {
        return new SofaTracerSendMessageAspect(rabbitTemplate.getExchange(),
            rabbitTemplate.getRoutingKey(), rabbitTemplate.getMessageConverter(), rabbitTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public static RabbitMqBeanPostProcessor sofaTracerRabbitMqBeanPostProcessor() {
        return new RabbitMqBeanPostProcessor();
    }
}
