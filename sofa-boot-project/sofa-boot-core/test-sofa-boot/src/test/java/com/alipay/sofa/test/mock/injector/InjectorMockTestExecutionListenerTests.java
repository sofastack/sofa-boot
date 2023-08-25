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
import com.alipay.sofa.test.mock.injector.definition.MockDefinition;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import com.alipay.sofa.test.mock.injector.resolver.BeanInjectorStub;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.TestContext;

import java.util.Set;

import static com.alipay.sofa.test.mock.injector.InjectorMockTestExecutionListener.STUBBED_DEFINITIONS;
import static com.alipay.sofa.test.mock.injector.InjectorMockTestExecutionListener.STUBBED_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link InjectorMockTestExecutionListener}.
 *
 * @author huzijie
 * @version InjectorMockTestExecutionListenerTests.java, v 0.1 2023年08月21日 4:41 PM huzijie Exp $
 */
public class InjectorMockTestExecutionListenerTests {

    private final InjectorMockTestExecutionListener listener = new InjectorMockTestExecutionListener();

    @Test
    public void prepareTestInstanceShouldInjectMockBean() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            TargetBean.class);

        WithMockBean instance = new WithMockBean();
        TestContext testContext = mockTestContext(instance);
        given(testContext.getApplicationContext()).willReturn(applicationContext);
        this.listener.prepareTestInstance(testContext);
        ExampleService mock = instance.getMockBean();
        assertThat(mock).isNotNull();
        assertThat(Mockito.mockingDetails(mock).isMock()).isTrue();

        TargetBean targetBean = applicationContext.getBean(TargetBean.class);
        ExampleService injectField = targetBean.getFieldA();
        assertThat(mock).isEqualTo(injectField);

        verify(testContext, times(2)).setAttribute(anyString(), any());
    }

    @Test
    public void prepareTestInstanceWhenInjectTargetAlreadyExist() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TargetBean.class);

        WithMockBean instance = new WithMockBean();
        TestContext testContext = mockTestContext(instance);
        given(testContext.getApplicationContext()).willReturn(applicationContext);
        this.listener.prepareTestInstance(testContext);
        assertThatIllegalStateException().isThrownBy(() -> this.listener.prepareTestInstance(testContext))
                .withMessageContaining("The existing value");
    }

    @Test
    public void beforeTestMethodShouldRestMock() {
        WithMockBean instance = new WithMockBean();
        TestContext testContext = mockTestContext(instance);

        MockDefinition definition = createMockDefinition(ExampleService.class, MockReset.BEFORE);
        ExampleService mock = definition.createMock();
        when(mock.greeting()).thenReturn("abc");
        assertThat(mock.greeting()).isEqualTo("abc");

        given(testContext.getAttribute(eq(STUBBED_DEFINITIONS))).willReturn(Set.of(definition));
        this.listener.beforeTestMethod(testContext);

        assertThat(mock.greeting()).isEqualTo(null);
    }

    @Test
    public void afterTestMethodShouldRestMock() {
        WithMockBean instance = new WithMockBean();
        TestContext testContext = mockTestContext(instance);

        MockDefinition definition = createMockDefinition(ExampleService.class, MockReset.AFTER);
        ExampleService mock = definition.createMock();
        when(mock.greeting()).thenReturn("abc");
        assertThat(mock.greeting()).isEqualTo("abc");

        given(testContext.getAttribute(eq(STUBBED_DEFINITIONS))).willReturn(Set.of(definition));
        this.listener.afterTestMethod(testContext);

        assertThat(mock.greeting()).isEqualTo(null);
    }

    @Test
    public void afterTestClassShouldRestInjectStubs() {
        WithMockBean instance = new WithMockBean();
        TestContext testContext = mockTestContext(instance);

        BeanInjectorStub beanInjectorStub = mock(BeanInjectorStub.class);

        given(testContext.getAttribute(eq(STUBBED_FIELDS))).willReturn(Set.of(beanInjectorStub));
        this.listener.afterTestMethod(testContext);

        verify(beanInjectorStub, only()).reset();
    }

    private MockDefinition createMockDefinition(Class<?> clazz, MockReset mockReset) {
        return new MockDefinition(ResolvableType.forClass(clazz), null, null, null, null, null,
            null, false, mockReset, null);
    }

    private TestContext mockTestContext(Object instance) {
        TestContext testContext = mock(TestContext.class);
        given(testContext.getTestInstance()).willReturn(instance);
        given(testContext.getTestClass()).willReturn((Class) instance.getClass());
        return testContext;
    }

    @Configuration
    static class TargetBean {

        private ExampleService fieldA;

        public ExampleService getFieldA() {
            return fieldA;
        }
    }

    static class WithMockBean {

        public ExampleService getMockBean() {
            return mockBean;
        }

        @MockBeanInjector(field = "fieldA", type = TargetBean.class)
        private ExampleService mockBean;
    }
}
