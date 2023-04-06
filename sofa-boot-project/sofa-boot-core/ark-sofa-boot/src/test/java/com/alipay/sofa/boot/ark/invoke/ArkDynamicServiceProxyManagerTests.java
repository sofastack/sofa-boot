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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.boot.ark.sample.SampleService;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.spi.binding.Contract;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ArkDynamicServiceProxyManager}.
 *
 * @author huzijie
 * @version ArkDynamicServiceProxyManagerTests.java, v 0.1 2023年04月06日 2:25 PM huzijie Exp $
 */
public class ArkDynamicServiceProxyManagerTests {

    private final ArkDynamicServiceProxyManager arkDynamicServiceProxyManager = new ArkDynamicServiceProxyManager();

    @Test
    public void noBizEnvironment() {
        Contract contract = new ReferenceImpl("test", SampleService.class, InterfaceMode.api, true);

        assertThat(
            arkDynamicServiceProxyManager.getDynamicServiceProxy(contract,
                ClassLoader.getSystemClassLoader())).isNull();
        assertThat(
            arkDynamicServiceProxyManager.getDynamicServiceComponent(contract,
                ClassLoader.getSystemClassLoader())).isNull();
    }
}
