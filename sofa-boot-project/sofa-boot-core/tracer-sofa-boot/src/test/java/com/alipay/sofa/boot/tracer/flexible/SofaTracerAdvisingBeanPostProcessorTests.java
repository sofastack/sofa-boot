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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaTracerAdvisingBeanPostProcessor}.
 *
 * @author huzijie
 * @version SofaTracerAdvisingBeanPostProcessorTests.java, v 0.1 2023年01月10日 8:50 PM huzijie Exp $
 */
public class SofaTracerAdvisingBeanPostProcessorTests {

    @Test
    public void getAdvice() {
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Nullable
            @Override
            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                return invocation.proceed();
            }
        };
        SofaTracerAdvisingBeanPostProcessor sofaTracerAdvisingBeanPostProcessor = new SofaTracerAdvisingBeanPostProcessor(
            methodInterceptor);
        assertThat(sofaTracerAdvisingBeanPostProcessor.isExposeProxy()).isTrue();
        Field advisorField = ReflectionUtils.findField(SofaTracerAdvisingBeanPostProcessor.class,
            "advisor");
        ReflectionUtils.makeAccessible(advisorField);
        Advisor advisor = (Advisor) ReflectionUtils.getField(advisorField,
            sofaTracerAdvisingBeanPostProcessor);
        assertThat(advisor).isInstanceOf(TracerAnnotationClassAdvisor.class);
        assertThat(((TracerAnnotationClassAdvisor) advisor).getAdvice()).isEqualTo(
            methodInterceptor);
        Field beforeExistingAdvisors = ReflectionUtils.findField(
            SofaTracerAdvisingBeanPostProcessor.class, "beforeExistingAdvisors");
        ReflectionUtils.makeAccessible(beforeExistingAdvisors);
        assertThat(
            (boolean) ReflectionUtils.getField(beforeExistingAdvisors,
                sofaTracerAdvisingBeanPostProcessor)).isTrue();
    }
}
