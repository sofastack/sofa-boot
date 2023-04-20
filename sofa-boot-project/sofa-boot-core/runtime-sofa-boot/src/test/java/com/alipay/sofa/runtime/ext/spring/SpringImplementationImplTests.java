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
package com.alipay.sofa.runtime.ext.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SpringImplementationImpl}.
 *
 * @author huzijie
 * @version SpringImplementationImplTests.java, v 0.1 2023年04月10日 3:31 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SpringImplementationImplTests {

    @Mock
    private ApplicationContext applicationContext;

    @Test
    void getTarget() {
        SpringImplementationImpl implementation = new SpringImplementationImpl("testBean",
            applicationContext);
        Object target = new Object();
        implementation.setTarget(target);
        assertThat(implementation.getTarget()).isSameAs(applicationContext.getBean("testBean"));
    }

    @Test
    void getTargetClass() {
        SpringImplementationImpl implementation = new SpringImplementationImpl("testBean",
            applicationContext);
        when(applicationContext.getBean("testBean")).thenReturn(new Object());
        assertThat(implementation.getTargetClass()).isEqualTo(
            applicationContext.getBean("testBean").getClass());
    }

    @Test
    void getName() {
        SpringImplementationImpl implementation = new SpringImplementationImpl("testBean",
            applicationContext);
        assertThat(implementation.getName()).isEqualTo("testBean");
    }
}
