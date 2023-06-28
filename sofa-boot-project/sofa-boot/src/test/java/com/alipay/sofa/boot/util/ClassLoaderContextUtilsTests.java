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
package com.alipay.sofa.boot.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ClassLoaderContextUtils}.
 *
 * @author huzijie
 * @version ClassLoaderContextUtilsTests.java, v 0.1 2023年04月03日 5:13 PM huzijie Exp $
 */
public class ClassLoaderContextUtilsTests {

    @Test
    public void testRunWithTemporaryContextClassloader() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newClassLoader = new FilteredClassLoader("null");
        assertThatThrownBy(() -> ClassLoaderContextUtils.runWithTemporaryContextClassloader(() -> {
            assertThat(Thread.currentThread().getContextClassLoader()).isEqualTo(newClassLoader);
            throw new RuntimeException("exception");
        }, newClassLoader)).isInstanceOf(RuntimeException.class);
        assertThat(Thread.currentThread().getContextClassLoader()).isEqualTo(oldClassLoader);
    }

    @Test
    public void testCallWithTemporaryContextClassloader() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newClassLoader = new FilteredClassLoader("null");
        assertThatThrownBy(() -> ClassLoaderContextUtils.callWithTemporaryContextClassloader(() -> {
            assertThat(Thread.currentThread().getContextClassLoader()).isEqualTo(newClassLoader);
            throw new RuntimeException("exception");
        }, newClassLoader)).isInstanceOf(RuntimeException.class);
        assertThat(Thread.currentThread().getContextClassLoader()).isEqualTo(oldClassLoader);
    }
}
