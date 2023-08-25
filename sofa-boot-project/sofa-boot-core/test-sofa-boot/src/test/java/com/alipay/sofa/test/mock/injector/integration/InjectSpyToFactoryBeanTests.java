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
package com.alipay.sofa.test.mock.injector.integration;

import com.alipay.sofa.test.mock.injector.annotation.SpyBeanInjector;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import com.alipay.sofa.test.mock.injector.example.ExampleServiceCaller;
import com.alipay.sofa.test.mock.injector.example.RealExampleService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link SpyBeanInjector} with factory bean.
 *
 * @author huzijie
 * @version InjectSpyToFactoryBeanTests.java, v 0.1 2023年08月21日 8:24 PM huzijie Exp $
 */
@SpringBootTest(classes = TestSofaBootApplication.class)
@RunWith(SpringRunner.class)
@Import(InjectSpyToFactoryBeanTests.Config.class)
public class InjectSpyToFactoryBeanTests {

    @SpyBeanInjector(field = "service", type = ExampleServiceCaller.class)
    private ExampleService     exampleService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void checkSpy() {
        when(exampleService.greeting()).thenReturn("aspy");
        ExampleServiceCaller bean = this.applicationContext.getBean(ExampleServiceCaller.class);
        Assertions.assertThat(bean.sayGreeting()).isEqualTo("aspy");
        Assertions.assertThat(bean.sayHello()).isEqualTo("hello");
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {

        @Bean
        TestFactoryBean testFactoryBean() {
            return new TestFactoryBean();
        }

    }

    static class TestFactoryBean implements FactoryBean<ExampleServiceCaller> {

        private final ExampleServiceCaller exampleServiceCaller;

        public TestFactoryBean() {
            exampleServiceCaller = new ExampleServiceCaller();
            exampleServiceCaller.setService(new RealExampleService("greeting"));
        }

        @Override
        public ExampleServiceCaller getObject() {
            return exampleServiceCaller;
        }

        @Override
        public Class<?> getObjectType() {
            return ExampleServiceCaller.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

    }
}
