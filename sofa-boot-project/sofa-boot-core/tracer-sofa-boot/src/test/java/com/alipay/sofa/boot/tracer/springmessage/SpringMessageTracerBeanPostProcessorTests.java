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
package com.alipay.sofa.boot.tracer.springmessage;

import com.alipay.sofa.tracer.plugins.message.interceptor.SofaTracerChannelInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringMessageTracerBeanPostProcessor}.
 *
 * @author huzijie
 * @version SpringMessageTracerBeanPostProcessorTests.java, v 0.1 2023年01月09日 8:49 PM huzijie Exp $
 */
public class SpringMessageTracerBeanPostProcessorTests {

    @Test
    public void addInterceptor() {
        SpringMessageTracerBeanPostProcessor springMessageTracerBeanPostProcessor = new SpringMessageTracerBeanPostProcessor();
        springMessageTracerBeanPostProcessor.setAppName("testApp");
        AbstractMessageChannel channel = new EmptyMessageChannel();
        Object bean = springMessageTracerBeanPostProcessor.postProcessBeforeInitialization(channel, "channel");
        assertThat(bean).isEqualTo(channel);
        assertThat(channel.getInterceptors()).anyMatch(channelInterceptor -> channelInterceptor instanceof SofaTracerChannelInterceptor);
    }

    static class EmptyMessageChannel extends AbstractMessageChannel {

        @Override
        protected boolean doSend(Message<?> message, long timeout) {
            return false;
        }
    }
}
