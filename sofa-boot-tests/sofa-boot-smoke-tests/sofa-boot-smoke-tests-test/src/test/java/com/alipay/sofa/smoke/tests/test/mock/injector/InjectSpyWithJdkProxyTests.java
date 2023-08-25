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
package com.alipay.sofa.smoke.tests.test.mock.injector;

import com.alipay.sofa.smoke.tests.test.TestSofaBootApplication;
import com.alipay.sofa.test.mock.injector.annotation.SpyBeanInjector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SpyBeanInjector} with a JDK proxy.
 *
 * @author huzijie
 * @version InjectSpyWithJdkProxyTests.java, v 0.1 2023年08月21日 8:35 PM huzijie Exp $
 */
@SpringBootTest(classes = TestSofaBootApplication.class)
@Import(InjectSpyWithJdkProxyTests.Config.class)
public class InjectSpyWithJdkProxyTests {

    @SpyBeanInjector(field = "service", type = ExampleServiceCaller.class)
    private ExampleService     exampleService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void checkSpy() {
        when(exampleService.greeting()).thenReturn("aspy");
        ExampleServiceCaller bean = this.applicationContext.getBean(ExampleServiceCaller.class);
        assertThat(bean.sayGreeting()).isEqualTo("aspy");
        assertThat(bean.sayHello()).isEqualTo("jdkProxy");
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {

        @Bean
        ExampleServiceCaller exampleServiceCaller() {
            ExampleServiceCaller exampleServiceCaller = new ExampleServiceCaller();
            exampleServiceCaller.setService(exampleService());
            return exampleServiceCaller;
        }

        private ExampleService exampleService() {
            return (ExampleService) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] { ExampleService.class }, (proxy, method, args) -> "jdkProxy");
        }
    }
}
