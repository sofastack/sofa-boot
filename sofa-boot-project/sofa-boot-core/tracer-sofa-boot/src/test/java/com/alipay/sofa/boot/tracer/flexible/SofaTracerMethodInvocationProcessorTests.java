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

import com.alipay.sofa.tracer.plugin.flexible.FlexibleTracer;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Tests for {@link SofaTracerMethodInvocationProcessor}.
 *
 *
 * @author huzijie
 * @version SofaTracerMethodInvocationProcessorTests.java, v 0.1 2023年01月10日 8:29 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaTracerMethodInvocationProcessorTests {

    @Mock
    private MethodInvocation methodInvocation;

    @Test
    public void noFlexibleTracer() throws Throwable {
        Tracer tracer = new MockTracer();
        SofaTracerMethodInvocationProcessor sofaTracerMethodInvocationProcessor = new SofaTracerMethodInvocationProcessor(
            tracer);
        sofaTracerMethodInvocationProcessor.process(methodInvocation, getTracerAnnotation());
        Mockito.verify(methodInvocation, never()).getArguments();
    }

    @Test
    public void useFlexibleTracer() throws Throwable {
        Tracer tracer = new FlexibleTracer();
        Mockito.when(methodInvocation.getArguments()).thenReturn(new Object[] {});
        Mockito.when(methodInvocation.proceed()).thenReturn(new Object());
        Mockito.when(methodInvocation.getMethod()).thenReturn(
            ReflectionUtils.findMethod(A.class, "hello"));
        SofaTracerMethodInvocationProcessor sofaTracerMethodInvocationProcessor = new SofaTracerMethodInvocationProcessor(
            tracer);
        sofaTracerMethodInvocationProcessor.process(methodInvocation, getTracerAnnotation());
        Mockito.verify(methodInvocation, times(1)).getArguments();
    }

    private com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer getTracerAnnotation() {
        Method method = ReflectionUtils.findMethod(A.class, "hello");
        return AnnotationUtils.findAnnotation(method,
            com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer.class);
    }

    static class A {

        @com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer(operateName = "test")
        public void hello() {
        }

    }
}
