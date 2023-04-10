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
package com.alipay.sofa.runtime.filter;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JvmFilterContext}.
 *
 * @author huzijie
 * @version JvmFilterContextTests.java, v 0.1 2023年04月10日 11:39 AM huzijie Exp $
 */
public class JvmFilterContextTests {

    @Test
    void getInvokeResultShouldReturnSetInvokeResult() {
        // Arrange
        Object invokeResult = new Object();
        JvmFilterContext context = new JvmFilterContext();

        // Act
        context.setInvokeResult(invokeResult);
        Object result = context.getInvokeResult();

        // Assert
        assertThat(result).isEqualTo(invokeResult);
    }

    @Test
    void getSofaRuntimeContextShouldReturnSetSofaRuntimeContext() {
        // Arrange
        SofaRuntimeContext sofaRuntimeContext = Mockito.mock(SofaRuntimeContext.class);
        JvmFilterContext context = new JvmFilterContext();

        // Act
        context.setSofaRuntimeContext(sofaRuntimeContext);
        SofaRuntimeContext result = context.getSofaRuntimeContext();

        // Assert
        assertThat(result).isEqualTo(sofaRuntimeContext);
    }

    @Test
    void getMethodInvocationShouldReturnSetMethodInvocation() {
        // Arrange
        MethodInvocation methodInvocation = Mockito.mock(MethodInvocation.class);
        JvmFilterContext context = new JvmFilterContext();

        // Act
        context.setMethodInvocation(methodInvocation);
        MethodInvocation result = context.getMethodInvocation();

        // Assert
        assertThat(result).isEqualTo(methodInvocation);
    }

    @Test
    void getExceptionShouldReturnSetException() {
        // Arrange
        Throwable exception = new Exception();
        JvmFilterContext context = new JvmFilterContext();

        // Act
        context.setException(exception);
        Throwable result = context.getException();

        // Assert
        assertThat(result).isEqualTo(exception);
    }

}
