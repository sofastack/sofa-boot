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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TracerAnnotationClassAdvisor}.
 *
 * @author huzijie
 * @version TracerAnnotationClassAdvisorTests.java, v 0.1 2023年01月10日 8:25 PM huzijie Exp $
 */
public class TracerAnnotationClassAdvisorTests {

    @Test
    public void getPointAndAdvice() {
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Nullable
            @Override
            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                return invocation.proceed();
            }
        };
        TracerAnnotationClassAdvisor advisor = new TracerAnnotationClassAdvisor(methodInterceptor);
        assertThat(advisor.getAdvice()).isEqualTo(methodInterceptor);
        assertThat(advisor.getPointcut()).isInstanceOf(TracerAnnotationClassPointcut.class);
    }
}
