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
package com.alipay.sofa.boot.tracer.flexible;

import com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Tests for {@link SofaTracerIntroductionInterceptor}.
*
* @author huzijie
* @version SofaTracerIntroductionInterceptorTests.java, v 0.1 2023年01月10日 8:58 PM huzijie Exp $
*/
@ExtendWith(MockitoExtension.class)
public class SofaTracerIntroductionInterceptorTests {

    @InjectMocks
    private SofaTracerIntroductionInterceptor sofaTracerIntroductionInterceptor;

    @Mock
    private MethodInvocationProcessor         sofaTracerIntroductionProcessor;

    @Mock
    private MethodInvocation                  methodInvocation;

    private final Method                      methodA = ReflectionUtils
                                                          .findMethod(A.class, "hello");

    private final Method                      methodB = ReflectionUtils
                                                          .findMethod(B.class, "hello");

    @Test
    public void invokeWithAnnotation() throws Throwable {
        Mockito.when(methodInvocation.getMethod()).thenReturn(methodA);
        Mockito.when(methodInvocation.getThis()).thenReturn(new A());
        Mockito.when(
            sofaTracerIntroductionProcessor.process(methodInvocation, getTracerAnnotation()))
            .thenReturn("Hello");
        Object result = sofaTracerIntroductionInterceptor.invoke(methodInvocation);
        assertThat(result).isEqualTo("Hello");
    }

    @Test
    public void invokeWithNoAnnotation() throws Throwable {
        Mockito.when(methodInvocation.getMethod()).thenReturn(methodB);
        Mockito.when(methodInvocation.getThis()).thenReturn(new B());
        Mockito.when(methodInvocation.proceed()).thenReturn("Hello");
        Object result = sofaTracerIntroductionInterceptor.invoke(methodInvocation);
        assertThat(result).isEqualTo("Hello");
    }

    private com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer getTracerAnnotation() {
        Method method = ReflectionUtils.findMethod(SofaTracerIntroductionInterceptorTests.A.class,
            "hello");
        return AnnotationUtils.findAnnotation(method,
            com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer.class);
    }

    static class A {

        @Tracer
        public void hello() {
        }

    }

    static class B {

        public void hello() {
        }

    }
}
