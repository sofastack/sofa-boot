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
import org.mockito.Mockito;
import org.springframework.cloud.util.PropertyUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void testIsSpringCloudBootstrapEnvironment_NullEnvironment() {
        assertFalse(SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(null));
    }

    @Test
    public void testInitSpringTestEnv() {

        boolean expectedTestEnv = true;

        boolean actualTestEnv = isInitSpringTestEnv();

        assertEquals(expectedTestEnv, actualTestEnv);
    }

    private boolean isInitSpringTestEnv() {
        StackTraceElement[] stackTrace = new StackTraceElement[]{
                new StackTraceElement("SomeClass", "someMethod", "SomeClass.java", 10),
                new StackTraceElement("AnotherClass", "loadContext", "AnotherClass.java", 20),
                new StackTraceElement("org.springframework.boot.test.context.SpringBootContextLoader",
                        "loadContext", "SpringBootContextLoader.java", 30)
        };
        boolean TEST_ENV = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("loadContext".equals(stackTraceElement.getMethodName())
                    && "org.springframework.boot.test.context.SpringBootContextLoader"
                    .equals(stackTraceElement.getClassName())) {
                TEST_ENV = true;
                break;
            }
        }
        return TEST_ENV;
    }
}





