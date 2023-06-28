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
package com.alipay.sofa.rpc.boot.test.config;

import com.alipay.sofa.rpc.boot.config.ZookeeperConfigurator;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ZookeeperConfigurator}.
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ZookeeperConfiguratorTests {

    @Test
    public void checkConfig() {
        String address = "zookeeper://127.0.0.1:2181?aaa=111&rrr=666&file=/host/zk";

        ZookeeperConfigurator zookeeperConfigurator = new ZookeeperConfigurator();
        RegistryConfig registryConfig = zookeeperConfigurator.buildFromAddress(address);
        assertThat(registryConfig).isNotNull();
        assertThat("zookeeper").isEqualTo(registryConfig.getProtocol());
        assertThat("127.0.0.1:2181").isEqualTo(registryConfig.getAddress());
        assertThat("/host/zk").isEqualTo(registryConfig.getFile());
    }
}
