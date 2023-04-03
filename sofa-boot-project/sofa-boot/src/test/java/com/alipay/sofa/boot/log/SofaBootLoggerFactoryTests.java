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
package com.alipay.sofa.boot.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaBootLoggerFactory}.
 *
 * @author huzijie
 * @version SofaBootLoggerFactoryTests.java, v 0.1 2023年04月03日 7:44 PM huzijie Exp $
 */
public class SofaBootLoggerFactoryTests {

    @Test
    public void getLoggerByClass() {
        Class<?> clazz = null;
        assertThat(SofaBootLoggerFactory.getLogger(clazz)).isNull();
        assertThat(SofaBootLoggerFactory.getLogger(this.getClass())).isInstanceOf(Logger.class);
    }

    @Test
    public void getLoggerByName() {
        String clazz = null;
        assertThat(SofaBootLoggerFactory.getLogger(clazz)).isNull();
        assertThat(SofaBootLoggerFactory.getLogger("")).isNull();
        assertThat(SofaBootLoggerFactory.getLogger(this.getClass().getCanonicalName()))
            .isInstanceOf(Logger.class);
    }
}
