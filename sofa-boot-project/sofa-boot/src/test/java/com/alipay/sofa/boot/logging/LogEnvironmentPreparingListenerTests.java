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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LogEnvironmentPreparingListener}.
 *
 * @author huzijie
 * @version LogEnvironmentPreparingListenerTests.java, v 0.1 2023年02月01日 3:35 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class LogEnvironmentPreparingListenerTests {

    @Mock
    private ApplicationEnvironmentPreparedEvent event;

    @Test
    public void registerPropertiesToLoggerContext() {
        LogEnvironmentPreparingListener logEnvironmentPreparingListener = new LogEnvironmentPreparingListener();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("sofa.middleware.log.key1", "value1");
        environment.setProperty("sofa.middleware.log.key2", "value2");
        Mockito.doReturn(environment).when(event).getEnvironment();
        logEnvironmentPreparingListener.onApplicationEvent(event);

        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key1"))
            .isEqualTo("value1");
        assertThat(
            CommonLoggingConfigurations.getExternalConfigurations().get("sofa.middleware.log.key2"))
            .isEqualTo("value2");
    }
}
