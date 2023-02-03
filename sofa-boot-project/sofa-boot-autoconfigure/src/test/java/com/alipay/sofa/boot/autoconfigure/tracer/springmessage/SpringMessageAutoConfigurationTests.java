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
package com.alipay.sofa.boot.autoconfigure.tracer.springmessage;

import com.alipay.sofa.boot.tracer.springmessage.SpringMessageTracerBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringMessageAutoConfiguration}.
 *
 * @author huzijie
 * @version SpringMessageAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class SpringMessageAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SpringMessageAutoConfiguration.class));

    @Test
    public void registerSpringMessageBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(SpringMessageTracerBeanPostProcessor.class));
    }

    @Test
    public void noSpringMessageBeansWhenAbstractMessageChannelClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(AbstractMessageChannel.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringMessageTracerBeanPostProcessor.class));
    }

    @Test
    public void noSpringMessageBeansWhenChannelInterceptorClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(ChannelInterceptor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringMessageTracerBeanPostProcessor.class));
    }

    @Test
    public void noSpringMessageBeansWhenDirectWithAbstractMessageChannelClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(AbstractMessageChannel.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringMessageTracerBeanPostProcessor.class));
    }

    @Test
    public void noSpringMessageBeansWhenSpringMessageTracerBeanPostProcessorClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SpringMessageTracerBeanPostProcessor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringMessageTracerBeanPostProcessor.class));
    }

    @Test
    public void noSpringMessageBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.springmessage.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpringMessageTracerBeanPostProcessor.class));
    }
}
