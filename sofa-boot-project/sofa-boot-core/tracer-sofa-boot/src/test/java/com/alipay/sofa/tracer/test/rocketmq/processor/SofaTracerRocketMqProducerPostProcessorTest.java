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

import com.alipay.sofa.tracer.boot.rocketmq.processor.SofaTracerRocketMqProducerPostProcessor;
import com.alipay.sofa.tracer.plugins.rocketmq.interceptor.SofaTracerSendMessageHook;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@TestPropertySource(properties = "spring.application.name=SofaTracerRocketMqProducerPostProcessorTest")
public class SofaTracerRocketMqProducerPostProcessorTest {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Test
    public void postProcessAfterInitialization() {
        DefaultMQProducerImpl producer = defaultMQProducer.getDefaultMQProducerImpl();
        Assert.assertTrue(producer.hasSendMessageHook());
        Field field = ReflectionUtils.findField(DefaultMQProducerImpl.class,
                "sendMessageHookList");
        Assert.assertNotNull(field);
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, producer);
        Assert.assertTrue(value instanceof ArrayList);
        ArrayList<SendMessageHook> sendMessageHookList = (ArrayList<SendMessageHook>) value;
        SendMessageHook sendMessageHook = sendMessageHookList.get(sendMessageHookList.size() - 1);
        Assert.assertTrue(sendMessageHook instanceof SofaTracerSendMessageHook);
    }

    @Configuration(proxyBeanMethods = false)
    static class SofaTracerRocketMqProducerPostProcessorTestConfiguration {
        @Bean
        public SofaTracerRocketMqProducerPostProcessor sofaTracerRocketMqProducerPostProcessor() {
            return new SofaTracerRocketMqProducerPostProcessor();
        }

        @Bean
        public DefaultMQProducer defaultMQProducer() {
            return new DefaultMQProducer();
        }
    }
}
