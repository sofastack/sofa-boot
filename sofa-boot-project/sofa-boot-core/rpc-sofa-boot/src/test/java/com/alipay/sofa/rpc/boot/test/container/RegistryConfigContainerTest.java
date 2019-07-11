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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.LocalFileConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.config.ZookeeperConfigurator;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.config.RegistryConfig;

/**
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RegistryConfigContainerTest {
    @Rule
    public ExpectedException           thrown                = ExpectedException.none();

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
        Assert.assertEquals("local", registryConfigLocal.getProtocol());
        Assert.assertEquals("/home/admin/local", registryConfigLocal.getFile());
    }

    @Test
    public void testZooKeeperRegistryConfig() {
        RegistryConfig registryConfigZk = zkFileConfigurator
            .buildFromAddress("zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        Assert.assertEquals("zookeeper", registryConfigZk.getProtocol());
        Assert.assertEquals("/home/admin/zookeeper", registryConfigZk.getFile());
    }

    @Test
    public void testWrongRegistryConfig() {
        thrown.expect(SofaBootRpcRuntimeException.class);
        thrown.expectMessage("registry config [no] is not supported");
        sofaBootRpcProperties.setRegistryAddress("no");
        //Test case will init by other xmls.
        registryConfigContainer.getRegistryConfigs().clear();
        registryConfigContainer.getRegistryConfig();

    }

    @Test
    public void testCustomRegistryConfig() {
        Map<String, String> registryAlias = new HashMap<String, String>();
        registryAlias.put("zk1", "zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        sofaBootRpcProperties.setRegistries(registryAlias);
        RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig("zk1");
        Assert.assertEquals(registryConfig.getProtocol(), "zookeeper");
    }

    @Test
    public void testCustomRegistryConfig2() {
        Map<String, String> registryAlias = new HashMap<String, String>();
        registryAlias.put("zk1", "zookeeper://127.0.0.1:2181?file=/home/admin/zookeeper");
        sofaBootRpcProperties.setRegistries(registryAlias);
        RegistryConfig registryConfig = registryConfigContainer.getRegistryConfig();
        Assert.assertEquals(registryConfig.getProtocol(), "zookeeper");
    }
}