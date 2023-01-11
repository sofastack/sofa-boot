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

import com.alipay.sofa.tracer.plugins.rocketmq.interceptor.SofaTracerConsumeMessageHook;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RocketMqConsumerPostProcessor}.
 *
 * @author huzijie
 * @version RocketMqConsumerPostProcessorTests.java, v 0.1 2023年01月09日 8:24 PM huzijie Exp $
 */
public class RocketMqConsumerPostProcessorTests {

    @Test
    public void customize() {
        RocketMqConsumerPostProcessor rocketMqConsumerPostProcessor = new RocketMqConsumerPostProcessor();
        rocketMqConsumerPostProcessor.setAppName("testApp");
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();
        container.setConsumer(new DefaultMQPushConsumer());
        Object bean = rocketMqConsumerPostProcessor.postProcessAfterInitialization(container, "container");
        DefaultMQPushConsumerImpl defaultMQPushConsumer = container.getConsumer().getDefaultMQPushConsumerImpl();
        assertThat(bean).isEqualTo(container);

        Field field = ReflectionUtils.findField(DefaultMQPushConsumerImpl.class, "consumeMessageHookList");
        ReflectionUtils.makeAccessible(field);
        ArrayList<ConsumeMessageHook> hooks = (ArrayList<ConsumeMessageHook>) ReflectionUtils.getField(field, defaultMQPushConsumer);
        assertThat(hooks).anyMatch(hook -> hook instanceof SofaTracerConsumeMessageHook);
    }
}
