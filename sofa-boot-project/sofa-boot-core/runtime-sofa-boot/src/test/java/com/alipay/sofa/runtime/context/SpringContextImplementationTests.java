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
package com.alipay.sofa.runtime.context;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringContextImplementation}.
 *
 * @author huzijie
 * @version SpringContextImplementationTests.java, v 0.1 2023年04月10日 11:36 AM huzijie Exp $
 */
public class SpringContextImplementationTests {

    @Test
    void getNameShouldReturnDisplayNameOfApplicationContext() {
        // Arrange
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        Mockito.when(applicationContext.getDisplayName()).thenReturn("TestContext");
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        String result = implementation.getName();

        // Assert
        assertThat(result).isEqualTo("TestContext");
    }

    @Test
    void getTargetShouldReturnApplicationContext() {
        // Arrange
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        Object result = implementation.getTarget();

        // Assert
        assertThat(result).isEqualTo(applicationContext);
    }

    @Test
    void getTargetClassShouldReturnClassOfApplicationContext() {
        // Arrange
        ApplicationContext applicationContext = new GenericApplicationContext();
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        Class<?> result = implementation.getTargetClass();

        // Assert
        assertThat(result).isEqualTo(applicationContext.getClass());
    }

    @Test
    void isSingletonShouldReturnFalse() {
        // Arrange
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        boolean result = implementation.isSingleton();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isLazyInitShouldReturnFalse() {
        // Arrange
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        boolean result = implementation.isLazyInit();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void setTargetShouldSetApplicationContext() {
        // Arrange
        ApplicationContext applicationContext1 = Mockito.mock(ApplicationContext.class);
        ApplicationContext applicationContext2 = Mockito.mock(ApplicationContext.class);
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext1);

        // Act
        implementation.setTarget(applicationContext2);

        // Assert
        assertThat(implementation.getTarget()).isEqualTo(applicationContext2);
    }

    @Test
    void isFactoryShouldReturnFalse() {
        // Arrange
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        SpringContextImplementation implementation = new SpringContextImplementation(
            applicationContext);

        // Act
        boolean result = implementation.isFactory();

        // Assert
        assertThat(result).isFalse();
    }

}
