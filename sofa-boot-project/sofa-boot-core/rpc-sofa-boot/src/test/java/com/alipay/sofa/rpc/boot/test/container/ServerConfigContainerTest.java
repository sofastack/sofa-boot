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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;

/**
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ServerConfigContainerTest extends ActivelyDestroyTest {
    private SofaBootRpcProperties sofaBootRpcProperties;
    private ServerConfigContainer serverConfigContainer;

    public ServerConfigContainerTest() {
        Environment environment = new MockEnvironment();
        sofaBootRpcProperties = new SofaBootRpcProperties();
        sofaBootRpcProperties.setEnvironment(environment);
        serverConfigContainer = new ServerConfigContainer(sofaBootRpcProperties);
    }

    @Test
    public void testBoltConfiguration() {
        sofaBootRpcProperties.setBoltPort("9090");
        sofaBootRpcProperties.setBoltThreadPoolCoreSize("8080");
        sofaBootRpcProperties.setBoltThreadPoolMaxSize("7070");
        sofaBootRpcProperties.setBoltAcceptsSize(("6060"));
        sofaBootRpcProperties.setVirtualHost("127.0.0.2");
        sofaBootRpcProperties.setBoundHost("127.0.0.3");
        sofaBootRpcProperties.setVirtualPort("8888");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        Assert.assertEquals(9090, serverConfig.getPort());
        Assert.assertEquals(8080, serverConfig.getCoreThreads());
        Assert.assertEquals(7070, serverConfig.getMaxThreads());
        Assert.assertEquals(6060, serverConfig.getAccepts());
        Assert.assertEquals(8888, serverConfig.getVirtualPort().intValue());
        Assert.assertEquals("127.0.0.2", serverConfig.getVirtualHost());
        Assert.assertEquals("127.0.0.3", serverConfig.getBoundHost());
    }

    @Test
    @Ignore("only can run in multi ip env")
    public void testBoltIpCustomConfiguration() {
        sofaBootRpcProperties.setEnabledIpRange("192.168");
        serverConfigContainer = new ServerConfigContainer(sofaBootRpcProperties);
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        Assert.assertEquals("192.168", serverConfig.getVirtualHost());
    }

    @Test
    public void testBoltServerDefaultPort() {
        sofaBootRpcProperties.setBoltPort("");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        Assert.assertEquals(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT, serverConfig.getPort());
    }

    @Test
    public void testDubboServerConfiguration() {
        sofaBootRpcProperties.setDubboPort("9696");
        sofaBootRpcProperties.setDubboIoThreadSize("8686");
        sofaBootRpcProperties.setDubboThreadPoolMaxSize("7676");
        sofaBootRpcProperties.setDubboAcceptsSize("6666");

        ServerConfig serverConfig = serverConfigContainer.createDubboServerConfig();

        Assert.assertEquals(9696, serverConfig.getPort());
        Assert.assertEquals(8686, serverConfig.getIoThreads());
        Assert.assertEquals(7676, serverConfig.getMaxThreads());
        Assert.assertEquals(6666, serverConfig.getAccepts());
    }

    @Test
    public void testRestServerConfiguration() {
        sofaBootRpcProperties.setRestHostname("host_name");
        sofaBootRpcProperties.setRestPort("123");
        sofaBootRpcProperties.setRestIoThreadSize("456");
        sofaBootRpcProperties.setRestContextPath("/api");
        sofaBootRpcProperties.setRestThreadPoolMaxSize("789");
        sofaBootRpcProperties.setRestMaxRequestSize("1000");
        sofaBootRpcProperties.setRestTelnet("true");
        sofaBootRpcProperties.setRestDaemon("true");
        sofaBootRpcProperties.setRestAllowedOrigins("a.com");
        ServerConfig serverConfig = serverConfigContainer.createRestServerConfig();

        Assert.assertEquals("host_name", serverConfig.getBoundHost());
        Assert.assertEquals(123, serverConfig.getPort());
        Assert.assertEquals(456, serverConfig.getIoThreads());
        Assert.assertEquals("/api/", serverConfig.getContextPath());
        Assert.assertEquals(789, serverConfig.getMaxThreads());
        Assert.assertEquals(1000, serverConfig.getPayload());
        Assert.assertTrue(serverConfig.isTelnet());
        Assert.assertTrue(serverConfig.isDaemon());
        Assert
            .assertEquals("a.com", serverConfig.getParameters().get(RpcConstants.ALLOWED_ORIGINS));

    }

    @Test
    public void testCustomServerConfig() {

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(123);
        final String protocol = "xxx";
        serverConfigContainer.registerCustomServerConfig(protocol, serverConfig);

        ServerConfig serverConfig2 = serverConfigContainer.getServerConfig(protocol);

        Assert.assertEquals(123, serverConfig2.getPort());
        Assert.assertEquals(serverConfig.getPort(), serverConfig2.getPort());

        boolean result = false;
        serverConfigContainer.unRegisterCustomServerConfig(protocol);
        try {
            serverConfigContainer.getServerConfig(protocol);

        } catch (Exception e) {
            Assert.assertTrue(e instanceof SofaBootRpcRuntimeException);
            result = true;
        }

        Assert.assertTrue(result);

    }

    @Test
    public void testCustomServerConfigTwice() {

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(123);
        final String protocol = "xxx";
        serverConfigContainer.registerCustomServerConfig(protocol, serverConfig);

        ServerConfig serverConfig2 = serverConfigContainer.getServerConfig(protocol);

        Assert.assertEquals(123, serverConfig2.getPort());
        Assert.assertEquals(serverConfig.getPort(), serverConfig2.getPort());

        boolean twiceResult = serverConfigContainer.registerCustomServerConfig(protocol,
            serverConfig);

        Assert.assertFalse(twiceResult);

    }
}