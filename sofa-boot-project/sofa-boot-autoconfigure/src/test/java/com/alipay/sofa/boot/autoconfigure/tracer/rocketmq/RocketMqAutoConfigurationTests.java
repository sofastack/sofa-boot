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
import org.apache.rocketmq.spring.support.RocketMQListenerContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RocketMqAutoConfiguration}.
 *
 * @author huzijie
 * @version RocketMqAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class RocketMqAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(RocketMqAutoConfiguration.class));

    @Test
    public void registerRocketMqBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(RocketMqProducerPostProcessor.class)
                        .hasSingleBean(RocketMqConsumerPostProcessor.class));
    }

    @Test
    public void noRocketMqBeansWhenMQProducerClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(MQProducer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RocketMqProducerPostProcessor.class)
                        .doesNotHaveBean(RocketMqConsumerPostProcessor.class));
    }

    @Test
    public void noRocketMqBeansWhenRocketMQListenerContainerClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(RocketMQListenerContainer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RocketMqProducerPostProcessor.class)
                        .doesNotHaveBean(RocketMqConsumerPostProcessor.class));
    }

    @Test
    public void noRocketMqBeansWhenRocketMqProducerPostProcessorClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(RocketMqProducerPostProcessor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RocketMqProducerPostProcessor.class)
                        .doesNotHaveBean(RocketMqConsumerPostProcessor.class));
    }

    @Test
    public void noRocketMqBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.rocketmq.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RocketMqProducerPostProcessor.class)
                        .doesNotHaveBean(RocketMqConsumerPostProcessor.class));
    }
}
