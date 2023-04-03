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

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LogOutPutUtils}.
 *
 * @author huzijie
 * @version LogOutPutUtilsTests.java, v 0.1 2023年04月03日 5:36 PM huzijie Exp $
 */
public class LogOutPutUtilsTests {

    @Test
    public void openOutPutForLoggersByString() {
        LogOutPutUtils.openOutPutForLoggers("abc", "efg");

        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get(
                Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH)).isEqualTo("true");
        assertThat(CommonLoggingConfigurations.shouldAttachConsoleAppender("abc")).isTrue();
        assertThat(CommonLoggingConfigurations.shouldAttachConsoleAppender("efg")).isTrue();
    }

    @Test
    public void openOutPutForLoggersByClass() {
        LogOutPutUtils.openOutPutForLoggers(LogOutPutUtilsTests.class);

        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get(
                Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH)).isEqualTo("true");
        assertThat(
            CommonLoggingConfigurations.shouldAttachConsoleAppender(LogOutPutUtilsTests.class
                .getName())).isTrue();
    }

}
