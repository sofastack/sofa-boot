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
package com.alipay.sofa.smoke.tests.rpc.container;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.LocalFileConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.boot.autoconfigure.rpc.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.config.ZookeeperConfigurator;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
@SpringBootTest(classes = RpcSofaBootApplication.class)
public class RegistryConfigContainerTests {

    @Autowired
    private SofaBootRpcProperties      sofaBootRpcProperties;
    @Autowired
    private RegistryConfigContainer    registryConfigContainer;

    private RegistryConfigureProcessor localFileConfigurator = new LocalFileConfigurator();

    private RegistryConfigureProcessor zkFileConfigurator    = new ZookeeperConfigurator();

    @Test
    public void testGetLocalRegistryConfig() {
        RegistryConfig registryConfigLocal = localFileConfigurator
            .buildFromAddress("local:///home/admin/local");
        assertThat(registryConfigLocal.getProtocol()).isEqualTo("local");
        assertThat(registryConfigLocal.getFile()).isEqualTo("/home/admin/local");
    }

    @Test
    public void testZooKeeperRegistryConfig() {
        RegistryConfig registryConfigZk = zkFileConfigurator
            .buildFromAddress("zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        assertThat(registryConfigZk.getProtocol()).isEqualTo("zookeeper");
        assertThat(registryConfigZk.getFile()).isEqualTo("/home/admin/zookeeper");
    }

    @Test
    public void testWrongRegistryConfig() {
        try {

            sofaBootRpcProperties.setRegistryAddress("no");
            //Test case will init by other xmls.
            registryConfigContainer.getRegistryConfigs().clear();
            registryConfigContainer.getRegistryConfig();
        } catch (Throwable e) {
            assertThat(e).isInstanceOf(SofaBootRpcRuntimeException.class);
            assertThat(e.getMessage()).isEqualTo(
                "RPC-010060028: Registry config [no] is not supported ");
        }

    }

    @Test
    public void testCustomRegistryConfig() {
        Map<String, String> registryAlias = new HashMap<String, String>();
        registryAlias.put("zk1", "zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        sofaBootRpcProperties.setRegistries(registryAlias);
        RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig("zk1");
        assertThat(registryConfig.getProtocol()).isEqualTo("zookeeper");
    }

    @Test
    public void testCustomRegistryConfig2() {
        Map<String, String> registryAlias = new HashMap<String, String>();
        registryAlias.put("zk1", "zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        sofaBootRpcProperties.setRegistries(registryAlias);
        RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig();
        assertThat(registryConfig.getProtocol()).isEqualTo("zookeeper");
    }
}
