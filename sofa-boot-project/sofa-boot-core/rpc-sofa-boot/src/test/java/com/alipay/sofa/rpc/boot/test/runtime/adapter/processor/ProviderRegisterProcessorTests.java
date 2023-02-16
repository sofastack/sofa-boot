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
package com.alipay.sofa.rpc.boot.test.runtime.adapter.processor;

import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderRegisterProcessor;
import com.alipay.sofa.rpc.config.ProviderConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProviderRegisterProcessor}.
 *
 * @author BaoYi
 */
public class ProviderRegisterProcessorTests {

    private final ProviderRegisterProcessor providerRegisterProcessor = new ProviderRegisterProcessor();

    @Test
    public void checkConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setRegister(true);
        providerRegisterProcessor.processorProvider(providerConfig);
        assertThat(providerConfig.isRegister()).isTrue();

        System.setProperty("sofa.boot.rpc.registry.disablePub", "true");
        providerRegisterProcessor.processorProvider(providerConfig);
        assertThat(providerConfig.isRegister()).isFalse();
        System.clearProperty("sofa.boot.rpc.registry.disablePub");
    }
}
