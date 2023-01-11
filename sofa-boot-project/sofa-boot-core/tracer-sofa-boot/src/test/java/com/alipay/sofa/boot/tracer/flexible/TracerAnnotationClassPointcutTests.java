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
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TracerAnnotationClassPointcut}.
 *
 * @author huzijie
 * @version TracerAnnotationClassPointcutTests.java, v 0.1 2023年01月10日 8:19 PM huzijie Exp $
 */
public class TracerAnnotationClassPointcutTests {

    @Test
    public void classFilterMatch() {
        TracerAnnotationClassPointcut tracerAnnotationClassPointcut = new TracerAnnotationClassPointcut();
        ClassFilter classFilter = tracerAnnotationClassPointcut.getClassFilter();
        assertThat(classFilter.matches(A.class)).isTrue();
        assertThat(classFilter.matches(B.class)).isFalse();
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
