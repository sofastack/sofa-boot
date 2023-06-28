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
package com.alipay.sofa.boot.lisener;

import com.alipay.sofa.boot.listener.SofaConfigSourceSupportListener;
import com.alipay.sofa.common.config.ConfigKey;
import com.alipay.sofa.common.config.SofaConfigs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaConfigSourceSupportListener}.
 *
 * @author huzijie
 * @version SofaConfigSourceListener.java, v 0.1 2020年12月22日 7:53 下午 huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaConfigSourceSupportListenerTests {

    private final ConfigKey<String>               key1                            = ConfigKey
                                                                                      .build(
                                                                                          "sofa.test.key1",
                                                                                          "defaultValue1",
                                                                                          false,
                                                                                          "test key1",
                                                                                          new String[] { "sofa_test_key1" });
    private final ConfigKey<String>               key2                            = ConfigKey
                                                                                      .build(
                                                                                          "sofa.test.key2",
                                                                                          "defaultValue2",
                                                                                          false,
                                                                                          "test key2",
                                                                                          new String[] { "sofa_test_key2" });
    private final ConfigKey<String>               key3                            = ConfigKey
                                                                                      .build(
                                                                                          "sofa.test.key3",
                                                                                          "defaultValue3",
                                                                                          false,
                                                                                          "test key3",
                                                                                          new String[] { "sofa_test_key3" });
    private final ConfigKey<String>               key4                            = ConfigKey
                                                                                      .build(
                                                                                          "sofa.test.key4",
                                                                                          "defaultValue4",
                                                                                          false,
                                                                                          "test key4",
                                                                                          new String[] { "sofa_test_key4" });

    private final SofaConfigSourceSupportListener sofaConfigSourceSupportListener = new SofaConfigSourceSupportListener();

    @Mock
    private ApplicationEnvironmentPreparedEvent   event;

    @Test
    public void registerSofaConfigs() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("sofa.test.key1", "value1");
        environment.setProperty("sofa.test.key2", "value2");
        Mockito.doReturn(environment).when(event).getEnvironment();
        sofaConfigSourceSupportListener.onApplicationEvent(event);

        assertThat("value1").isEqualTo(SofaConfigs.getOrDefault(key1));
        assertThat("value2").isEqualTo(SofaConfigs.getOrDefault(key2));
        assertThat("defaultValue3").isEqualTo(SofaConfigs.getOrDefault(key3));
        System.setProperty("sofa.test.key4", "systemValue");
        assertThat("systemValue").isEqualTo(SofaConfigs.getOrDefault(key4));
    }
}
