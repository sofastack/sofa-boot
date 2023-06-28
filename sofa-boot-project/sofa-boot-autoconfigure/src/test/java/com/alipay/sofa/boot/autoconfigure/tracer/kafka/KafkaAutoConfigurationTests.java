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
package com.alipay.sofa.boot.autoconfigure.tracer.kafka;

import com.alipay.sofa.boot.tracer.kafka.KafkaConsumerFactoryBeanPostProcessor;
import com.alipay.sofa.boot.tracer.kafka.KafkaProducerBeanFactoryPostProcessor;
import com.sofa.alipay.tracer.plugins.kafkamq.aspect.KafkaListenerSofaTracerAspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.ProducerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KafkaAutoConfiguration}.
 *
 * @author huzijie
 * @version KafkaAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class KafkaAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(KafkaAutoConfiguration.class));

    @Test
    public void registerKafkaBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .hasSingleBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .hasSingleBean(KafkaListenerSofaTracerAspect.class));
    }

    @Test
    public void noKafkaBeansWhenProducerFactoryClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(ProducerFactory.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .doesNotHaveBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(KafkaListenerSofaTracerAspect.class));
    }

    @Test
    public void noKafkaBeansWhenSofaTracerIntroductionInterceptorClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(KafkaConsumerFactoryBeanPostProcessor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .doesNotHaveBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(KafkaListenerSofaTracerAspect.class));
    }

    @Test
    public void noKafkaBeansWhenTracerAnnotationClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(KafkaListenerSofaTracerAspect.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .doesNotHaveBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(KafkaListenerSofaTracerAspect.class));
    }

    @Test
    public void noKafkaBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.kafka.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(KafkaConsumerFactoryBeanPostProcessor.class)
                        .doesNotHaveBean(KafkaProducerBeanFactoryPostProcessor.class)
                        .doesNotHaveBean(KafkaListenerSofaTracerAspect.class));
    }
}
