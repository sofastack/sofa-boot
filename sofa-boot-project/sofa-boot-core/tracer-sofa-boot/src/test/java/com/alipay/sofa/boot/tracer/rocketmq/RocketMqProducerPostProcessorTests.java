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
package com.alipay.sofa.boot.tracer.rocketmq;

import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RocketMqProducerPostProcessor}.
 *
 * @author huzijie
 * @version RocketMqProducerPostProcessorTests.java, v 0.1 2023年01月09日 8:24 PM huzijie Exp $
 */
public class RocketMqProducerPostProcessorTests {

    @Test
    public void customize() {
        RocketMqProducerPostProcessor rocketMqProducerPostProcessor = new RocketMqProducerPostProcessor();
        rocketMqProducerPostProcessor.setAppName("testApp");
        DefaultMQProducer producer = new DefaultMQProducer();
        Object bean = rocketMqProducerPostProcessor.postProcessAfterInitialization(producer, "producer");
        DefaultMQProducerImpl defaultMQProducerImpl = producer.getDefaultMQProducerImpl();
        assertThat(bean).isEqualTo(producer);

        Field field = ReflectionUtils.findField(DefaultMQProducerImpl.class, "sendMessageHookList");
        ReflectionUtils.makeAccessible(field);
        ArrayList<SendMessageHook> hooks = (ArrayList<SendMessageHook>) ReflectionUtils.getField(field, defaultMQProducerImpl);
        assertThat(hooks).anyMatch(hook -> hook instanceof SendMessageHook);
    }
}
