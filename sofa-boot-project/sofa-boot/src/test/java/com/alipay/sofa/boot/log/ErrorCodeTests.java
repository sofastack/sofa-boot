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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ErrorCode}.
 *
 * @author huzijie
 * @version ErrorCodeTests.java, v 0.1 2023年02月01日 11:41 AM huzijie Exp $
 */
public class ErrorCodeTests {

    @Test
    public void convertErrorCode() {
        assertThat(ErrorCode.convert("00-00000")).isEqualTo("SOFA-BOOT-00-00000: All is well");
        assertThat(ErrorCode.convert("01-00002", "a", "b")).isEqualTo(
            "SOFA-BOOT-01-00002: PreOut Binding [a] for [b] occur exception");
    }
}
