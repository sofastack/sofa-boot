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
package com.alipay.sofa.tracer.test.rocketmq.processor;

import com.alipay.sofa.tracer.boot.rocketmq.processor.SofaTracerRocketMqConsumerPostProcessor;
import com.alipay.sofa.tracer.plugins.rocketmq.interceptor.SofaTracerConsumeMessageHook;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author linnan
 * @since 3.9.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=SofaTracerRocketMqConsumerPostProcessorTest")
public class SofaTracerRocketMqConsumerPostProcessorTest {

    @Autowired
    private DefaultRocketMQListenerContainer defaultRocketMQListenerContainer;

    @Test
    public void postProcessAfterInitialization() {
        DefaultMQPushConsumer consumer = defaultRocketMQListenerContainer.getConsumer();
        DefaultMQPushConsumerImpl defaultMQPushConsumerImpl = consumer
            .getDefaultMQPushConsumerImpl();
        Assert.assertTrue(defaultMQPushConsumerImpl.hasHook());
        Field field = ReflectionUtils.findField(DefaultMQPushConsumerImpl.class,
            "consumeMessageHookList");
        Assert.assertNotNull(field);
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, defaultMQPushConsumerImpl);
        Assert.assertTrue(value instanceof ArrayList);
        ArrayList<ConsumeMessageHook> consumeMessageHookList = (ArrayList<ConsumeMessageHook>) value;
        ConsumeMessageHook consumeMessageHook = consumeMessageHookList.get(consumeMessageHookList
            .size() - 1);
        Assert.assertTrue(consumeMessageHook instanceof SofaTracerConsumeMessageHook);
    }

    @Configuration(proxyBeanMethods = false)
    static class SofaTracerRocketMqConsumerPostProcessorTestConfiguration {
        @Bean
        public SofaTracerRocketMqConsumerPostProcessor sofaTracerRocketMqConsumerPostProcessor() {
            return new SofaTracerRocketMqConsumerPostProcessor();
        }

        @Bean
        public DefaultRocketMQListenerContainer defaultRocketMQListenerContainer() {
            MockListener mockListener = new MockListener();
            RocketMQMessageListener annotation = AnnotationUtils.findAnnotation(MockListener.class,
                RocketMQMessageListener.class);
            DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            container.setConsumer(consumer);
            container.setConsumerGroup(annotation.consumerGroup());
            container.setNameServer("127.0.0.1:9876");
            container.setTopic(annotation.topic());
            container.setRocketMQMessageListener(annotation);
            container.setRocketMQListener(mockListener);
            return container;
        }
    }

    @RocketMQMessageListener(consumerGroup = "sofa-group", topic = "sofa-topic")
    static class MockListener implements RocketMQListener<String> {
        @Override
        public void onMessage(String message) {
        }
    }
}
