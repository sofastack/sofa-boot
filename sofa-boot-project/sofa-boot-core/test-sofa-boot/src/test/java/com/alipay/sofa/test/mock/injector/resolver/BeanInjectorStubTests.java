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
package com.alipay.sofa.test.mock.injector.resolver;

import com.alipay.sofa.test.mock.injector.definition.MockDefinition;
import com.alipay.sofa.test.mock.injector.definition.SpyDefinition;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import com.alipay.sofa.test.mock.injector.example.RealExampleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BeanInjectorStub}.
 *
 * @author huzijie
 * @version BeanInjectorStubTests.java, v 0.1 2023年08月21日 4:27 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class BeanInjectorStubTests {

    @Mock
    private MockDefinition       mockDefinition;

    @Mock
    private SpyDefinition        spyDefinition;

    private final Field          field          = ReflectionUtils.findField(TargetClass.class,
                                                    "exampleService");

    private final ExampleService exampleService = new RealExampleService("real");

    @Test
    public void mockBeanInjectorStub() {
        TargetClass targetClass = new TargetClass();
        BeanInjectorStub beanInjectorStub = new BeanInjectorStub(mockDefinition, field, targetClass);
        assertThat(targetClass.getExampleService()).isNull();

        when(mockDefinition.createMock()).thenReturn(exampleService);
        beanInjectorStub.inject();

        assertThat(targetClass.getExampleService()).isEqualTo(exampleService);

        beanInjectorStub.reset();
        assertThat(targetClass.getExampleService()).isNull();
    }

    @Test
    public void spyBeanInjectorStub() {
        TargetClass targetClass = new TargetClass();
        RealExampleService realExampleService = new RealExampleService("real");
        targetClass.setExampleService(realExampleService);
        BeanInjectorStub beanInjectorStub = new BeanInjectorStub(spyDefinition, field, targetClass);

        when(spyDefinition.createSpy(any())).thenReturn(exampleService);
        beanInjectorStub.inject();

        assertThat(targetClass.getExampleService()).isEqualTo(exampleService);

        beanInjectorStub.reset();
        assertThat(targetClass.getExampleService()).isEqualTo(realExampleService);
    }

    static class TargetClass {

        public void setExampleService(ExampleService exampleService) {
            this.exampleService = exampleService;
        }

        private ExampleService exampleService;

        public ExampleService getExampleService() {
            return exampleService;
        }
    }
}
