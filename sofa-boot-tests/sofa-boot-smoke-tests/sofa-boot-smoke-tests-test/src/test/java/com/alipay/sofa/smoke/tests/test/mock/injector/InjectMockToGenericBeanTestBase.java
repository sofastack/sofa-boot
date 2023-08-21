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
import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MockBeanInjector} with generic bean.
 *
 * @author huzijie
 * @version InjectMockToGenericBeanTestBase.java, v 0.1 2023年08月21日 7:53 PM huzijie Exp $
 */
@SpringBootTest(classes = TestSofaBootApplication.class)
@Import(InjectMockToGenericBeanTestBase.Config.class)
abstract class InjectMockToGenericBeanTestBase<T extends InjectMockToGenericBeanTestBase.Thing<U>, U extends InjectMockToGenericBeanTestBase.Something> {

    @MockBeanInjector(field = "something", name = "thing")
    private U                  something;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @SuppressWarnings("unchecked")
    public void checkMock() {
        T bean = (T) this.applicationContext.getBean(Thing.class);
        assertThat(bean.getSomething()).isEqualTo(something);
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {

        @Bean
        ThingImpl thing() {
            return new ThingImpl();
        }

    }

    abstract static class Thing<T extends Something> {

        protected T something;

        T getSomething() {
            return this.something;
        }

        void setSomething(T something) {
            this.something = something;
        }

    }

    static class SomethingImpl extends Something {

    }

    static class ThingImpl extends Thing<SomethingImpl> {

    }

    static class Something {

    }

}
