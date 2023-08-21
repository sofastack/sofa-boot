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
package com.alipay.sofa.test.mock.injector;

import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.cache.DefaultContextCache;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Ensure mock not dirty application context cache.
 *
 * @author huzijie
 * @version InjectorMockApplicationContextCacheTests.java, v 0.1 2023年08月21日 7:32 PM huzijie Exp $
 */
public class InjectorMockApplicationContextCacheTests {

    private final DefaultContextCache                    contextCache = new DefaultContextCache();

    private final DefaultCacheAwareContextLoaderDelegate delegate     = new DefaultCacheAwareContextLoaderDelegate(
                                                                          this.contextCache);

    @AfterEach
    @SuppressWarnings("unchecked")
    void clearCache() {
        Map<MergedContextConfiguration, ApplicationContext> contexts = (Map<MergedContextConfiguration, ApplicationContext>) ReflectionTestUtils
                .getField(this.contextCache, "contextMap");
        for (ApplicationContext context : contexts.values()) {
            if (context instanceof ConfigurableApplicationContext configurableContext) {
                configurableContext.close();
            }
        }
        this.contextCache.clear();
    }

    @Test
    void useCacheWhenResolveInjectorMockBeanAnnotation() {
        bootstrapContext(TestClass.class);
        assertThat(this.contextCache.size()).isOne();
        bootstrapContext(MockedBeanTestClass.class);
        assertThat(this.contextCache.size()).isOne();
    }

    @SuppressWarnings("rawtypes")
    private void bootstrapContext(Class<?> testClass) {
        SpringBootTestContextBootstrapper bootstrapper = new SpringBootTestContextBootstrapper();
        BootstrapContext bootstrapContext = mock(BootstrapContext.class);
        given((Class) bootstrapContext.getTestClass()).willReturn(testClass);
        bootstrapper.setBootstrapContext(bootstrapContext);
        given(bootstrapContext.getCacheAwareContextLoaderDelegate()).willReturn(this.delegate);
        TestContext testContext = bootstrapper.buildTestContext();
        testContext.getApplicationContext();
    }

    @SpringBootTest(classes = TestConfiguration.class, properties = "spring.application.name=test")
    static class TestClass {

    }

    @SpringBootTest(classes = TestConfiguration.class, properties = "spring.application.name=test")
    static class MockedBeanTestClass {

        @MockBeanInjector(field = "testBean", type = InjectBean.class)
        private TestBean testBean;

    }

    @Configuration
    static class TestConfiguration {

        @Bean
        TestBean testBean() {
            return new TestBean();
        }

        @Bean
        InjectBean injectBean() {
            return new InjectBean();
        }

    }

    static class TestBean {

    }

    static class InjectBean {

        private TestBean testBean;
    }
}
