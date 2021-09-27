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

import com.alipay.sofa.tracer.boot.rabbitmq.processor.SofaTracerRabbitMqBeanPostProcessor;
import com.sofa.alipay.tracer.plugins.rabbitmq.aspect.SofaTracerSendMessageAspect;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * SofaTracerRabbitMqAutoConfiguration.
 *
 * @author chenchen6  2020/8/09 20:44
 * @since  3.9.1
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RabbitAutoConfiguration.class)
@ConditionalOnClass({ Message.class, RabbitTemplate.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.rabbitmq", value = "enable", matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SofaTracerRabbitMqAutoConfiguration {

    @ConditionalOnBean(RabbitTemplate.class)
    @Bean
    public SofaTracerSendMessageAspect rabbitMqSendTracingAspect(RabbitTemplate rabbitTemplate) {
        return new SofaTracerSendMessageAspect(rabbitTemplate.getExchange(),
            rabbitTemplate.getRoutingKey(), rabbitTemplate.getMessageConverter(), rabbitTemplate);
    }

    @ConditionalOnMissingBean
    @Bean
    public SofaTracerRabbitMqBeanPostProcessor sofaTracerRabbitMqBeanPostProcessor() {
        return new SofaTracerRabbitMqBeanPostProcessor();
    }
}
