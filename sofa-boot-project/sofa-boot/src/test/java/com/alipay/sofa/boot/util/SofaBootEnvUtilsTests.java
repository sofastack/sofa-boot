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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaBootEnvUtils}.
 *
 * @author huzijie
 * @version SofaBootEnvUtilTests.java, v 0.1 2023年02月01日 3:34 PM huzijie Exp $
 */
public class SofaBootEnvUtilsTests {

    @Test
    public void localEnv() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement first = elements[elements.length - 1];
        if (first.toString().contains("com.intellij.rt.junit.JUnitStarter")) {
            // If run from IDEA, LOCAL_ENV is true
            assertThat(SofaBootEnvUtils.isLocalEnv()).isTrue();
        } else {
            assertThat(SofaBootEnvUtils.isLocalEnv()).isFalse();
        }
    }

    @Test
    public void testEnv() {
        assertThat(SofaBootEnvUtils.isSpringTestEnv()).isFalse();
    }

    @Test
    public void arkEnv() {
        assertThat(SofaBootEnvUtils.isArkEnv()).isFalse();
    }
}
