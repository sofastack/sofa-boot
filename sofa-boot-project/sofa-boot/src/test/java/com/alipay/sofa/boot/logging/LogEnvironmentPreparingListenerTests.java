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
package com.alipay.sofa.boot.logging;

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.thread.SofaThreadPoolConstants;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static com.alipay.sofa.boot.logging.LogEnvironmentPostProcessor.SOFA_THREAD_POOL_MONITOR_DISABLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LogEnvironmentPostProcessor}.
 *
 * @author huzijie
 * @version LogEnvironmentPreparingListenerTests.java, v 0.1 2023年02月01日 3:35 PM huzijie Exp $
 */
public class LogEnvironmentPreparingListenerTests {

    @Test
    public void registerPropertiesToLoggerContext() {
        LogEnvironmentPostProcessor logEnvironmentPostProcessor = new LogEnvironmentPostProcessor();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("sofa.middleware.log.key1", "value1");
        environment.setProperty("sofa.middleware.log.key2", "value2");

        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key1"))
            .isEqualTo(null);
        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key2"))
            .isEqualTo(null);

        logEnvironmentPostProcessor.postProcessEnvironment(environment, null);

        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key1"))
            .isEqualTo("value1");
        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key2"))
            .isEqualTo("value2");
    }

    @Test
    public void initSofaCommonThread() {
        LogEnvironmentPostProcessor logEnvironmentPostProcessor = new LogEnvironmentPostProcessor();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(SOFA_THREAD_POOL_MONITOR_DISABLE, "true");

        logEnvironmentPostProcessor.postProcessEnvironment(environment, null);

        assertThat(System.getProperty(SofaThreadPoolConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY))
            .isEqualTo("false");
        System.clearProperty(SofaThreadPoolConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY);
    }
}
