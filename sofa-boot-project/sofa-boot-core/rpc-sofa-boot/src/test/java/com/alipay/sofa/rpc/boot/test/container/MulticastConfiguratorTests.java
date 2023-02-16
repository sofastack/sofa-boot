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
package com.alipay.sofa.rpc.boot.test.container;

import com.alipay.sofa.rpc.boot.config.MulticastConfigurator;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MulticastConfigurator}.
 *
 * @author zhaowang
 * @version : MulticastConfiguratorTest.java, v 0.1 2020年03月09日 3:23 下午 zhaowang Exp $
 */
public class MulticastConfiguratorTests {

    @Test
    public void checkConfig() {
        MulticastConfigurator multicastConfigurator = new MulticastConfigurator();
        RegistryConfig registryConfig = multicastConfigurator
            .buildFromAddress("multicast://192.168.1.33:1234?a=b");
        assertThat(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST).isEqualTo(
            registryConfig.getProtocol());
        assertThat("192.168.1.33:1234").isEqualTo(registryConfig.getAddress());
        assertThat("b").isEqualTo(registryConfig.getParameter("a"));
    }
}
