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
package com.alipay.sofa.runtime.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.test.beans.StateMessage;
import com.alipay.sofa.runtime.test.beans.processor.FactoryBeanPostProcessor;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.application.name=StaticBeanPostProcessorTest")
public class StaticBeanPostProcessorTest {

    @Test
    public void testStaticBeanPostProcessor() {
        Assert.isTrue("aop".equals(StateMessage.getFactoryMessage()));
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(RuntimeConfiguration.class)
    static class BeanPostProcessorTestConfiguration {
        @Bean
        static public FactoryBeanPostProcessor factoryBeanPostProcessor() {
            return new FactoryBeanPostProcessor();
        }

        @Bean
        @SofaService
        public DefaultSampleService defaultSampleService() {
            return new DefaultSampleService("defaultSampleService");
        }
    }
}