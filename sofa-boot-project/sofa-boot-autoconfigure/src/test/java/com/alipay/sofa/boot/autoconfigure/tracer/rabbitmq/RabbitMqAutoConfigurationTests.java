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

import com.alipay.sofa.boot.tracer.kafka.KafkaConsumerFactoryBeanPostProcessor;
import com.alipay.sofa.boot.tracer.kafka.KafkaProducerBeanFactoryPostProcessor;
import com.alipay.sofa.boot.tracer.rabbitmq.RabbitMqBeanPostProcessor;
import com.sofa.alipay.tracer.plugins.kafkamq.aspect.KafkaListenerSofaTracerAspect;
import com.sofa.alipay.tracer.plugins.rabbitmq.aspect.SofaTracerSendMessageAspect;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RabbitMqAutoConfiguration}.
 *
 * @author huzijie
 * @version RabbitMqAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class RabbitMqAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(RabbitMqAutoConfiguration.class));

    @Test
    public void registerRabbitMqBeans() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .run((context) -> assertThat(context)
                        .hasSingleBean(SofaTracerSendMessageAspect.class)
                        .hasSingleBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noSofaTracerSendMessageAspectBeanWhenRabbitTemplateBeanNotExist() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaTracerSendMessageAspect.class)
                        .hasSingleBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noRabbitMqBeansWhenMessageClassNotExist() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .withClassLoader(new FilteredClassLoader(Message.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaTracerSendMessageAspect.class)
                        .doesNotHaveBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noRabbitMqBeansWhenRabbitTemplateClassNotExist() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .withClassLoader(new FilteredClassLoader(RabbitTemplate.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaTracerSendMessageAspect.class)
                        .doesNotHaveBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noRabbitMqBeansWhenSofaTracerSendMessageAspectClassNotExist() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .withClassLoader(new FilteredClassLoader(SofaTracerSendMessageAspect.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaTracerSendMessageAspect.class)
                        .doesNotHaveBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noRabbitMqBeansWhenRabbitMqBeanPostProcessorClassNotExist() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .withClassLoader(new FilteredClassLoader(RabbitMqBeanPostProcessor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaTracerSendMessageAspect.class)
                        .doesNotHaveBean(RabbitMqBeanPostProcessor.class));
    }

    @Test
    public void noRabbitMqBeansWhenPropertySetFalse() {
        this.contextRunner
                .withUserConfiguration(RabbitTemplateConfiguration.class)
                .withPropertyValues("sofa.boot.tracer.rabbitmq.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .doesNotHaveBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(KafkaListenerSofaTracerAspect.class));
    }

    @Configuration(proxyBeanMethods = false)
    static class RabbitTemplateConfiguration {

        @Bean
        public RabbitTemplate rabbitTemplate() {
            RabbitTemplate rabbitTemplate = new RabbitTemplate();
            rabbitTemplate.setConnectionFactory(new CachingConnectionFactory());
            return rabbitTemplate;
        }
    }

}
