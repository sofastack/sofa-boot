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

import com.alipay.sofa.rpc.boot.common.RpcThreadPoolMonitor;
import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.config.UserThreadPoolManager;
import com.alipay.sofa.rpc.server.UserThreadPool;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ServerConfigContainerTest extends ActivelyDestroyTest {
    private ServerConfigContainer serverConfigContainer;

    public ServerConfigContainerTest() {
        serverConfigContainer = new ServerConfigContainer();
    }

    @Test
    public void testBoltConfiguration() {
        serverConfigContainer.setBoltPortStr("9090");
        serverConfigContainer.setBoltThreadPoolCoreSizeStr("8080");
        serverConfigContainer.setBoltThreadPoolMaxSizeStr("7070");
        serverConfigContainer.setBoltAcceptsSizeStr(("6060"));
        serverConfigContainer.setVirtualHostStr("127.0.0.2");
        serverConfigContainer.setBoundHostStr("127.0.0.3");
        serverConfigContainer.setVirtualPortStr("8888");
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
        serverConfigContainer.setEnabledIpRange("192.168");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        Assert.assertEquals("192.168", serverConfig.getVirtualHost());
    }

    @Test
    public void testBoltServerDefaultPort() {
        serverConfigContainer.setBoltPortStr("");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        Assert.assertEquals(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT, serverConfig.getPort());
    }

    @Test
    public void testDubboServerConfiguration() {
        serverConfigContainer.setDubboPortStr("9696");
        serverConfigContainer.setDubboIoThreadSizeStr("8686");
        serverConfigContainer.setDubboThreadPoolMaxSizeStr("7676");
        serverConfigContainer.setDubboAcceptsSizeStr("6666");

        ServerConfig serverConfig = serverConfigContainer.createDubboServerConfig();

        Assert.assertEquals(9696, serverConfig.getPort());
        Assert.assertEquals(8686, serverConfig.getIoThreads());
        Assert.assertEquals(7676, serverConfig.getMaxThreads());
        Assert.assertEquals(6666, serverConfig.getAccepts());
    }

    @Test
    public void testRestServerConfiguration() {
        serverConfigContainer.setRestHostName("host_name");
        serverConfigContainer.setRestPortStr("123");
        serverConfigContainer.setRestIoThreadSizeStr("456");
        serverConfigContainer.setRestContextPath("/api");
        serverConfigContainer.setRestThreadPoolMaxSizeStr("789");
        serverConfigContainer.setRestMaxRequestSizeStr("1000");
        serverConfigContainer.setRestTelnetStr("true");
        serverConfigContainer.setRestDaemonStr("true");
        serverConfigContainer.setRestAllowedOrigins("a.com");
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

    @Test
    public void testCreateHttpServerConfig() {
        serverConfigContainer.setHttpPortStr("8080");
        serverConfigContainer.setHttpThreadPoolCoreSizeStr("5");
        serverConfigContainer.setHttpThreadPoolMaxSizeStr("10");
        serverConfigContainer.setHttpAcceptsSizeStr("1");
        serverConfigContainer.setHttpThreadPoolQueueSizeStr("8");

        ServerConfig serverConfig = serverConfigContainer
            .getServerConfig(SofaBootRpcConfigConstants.RPC_PROTOCOL_HTTP);

        Assert.assertEquals(8080, serverConfig.getPort());
        Assert.assertEquals(5, serverConfig.getCoreThreads());
        Assert.assertEquals(10, serverConfig.getMaxThreads());
        Assert.assertEquals(1, serverConfig.getAccepts());
        Assert.assertEquals(8, serverConfig.getQueues());
    }

    @Test
    public void testStartCustomThreadPoolMonitor() throws NoSuchMethodException,
                                                  IllegalAccessException,
                                                  InvocationTargetException, NoSuchFieldException {
        UserThreadPoolManager.registerUserThread("service1", new UserThreadPool());
        UserThreadPoolManager.registerUserThread("service2", new UserThreadPool());
        UserThreadPoolManager.registerUserThread("service3", new UserThreadPool("same-name"));
        UserThreadPoolManager.registerUserThread("service4", new UserThreadPool("same-name"));

        Method privateStartMethod = serverConfigContainer.getClass().getDeclaredMethod(
            "startCustomThreadPoolMonitor");
        privateStartMethod.setAccessible(true);
        privateStartMethod.invoke(serverConfigContainer);

        Field privateField = serverConfigContainer.getClass().getDeclaredField(
            "customThreadPoolMonitorList");
        privateField.setAccessible(true);
        Object value = privateField.get(serverConfigContainer);
        List<RpcThreadPoolMonitor> customThreadPoolMonitorList = (List<RpcThreadPoolMonitor>) value;
        Assert.assertEquals(customThreadPoolMonitorList.size(), 4);

        boolean hasHashCode = false;
        for (RpcThreadPoolMonitor monitor : customThreadPoolMonitorList) {
            if (monitor.getPoolName().contains("same-name-")) {
                hasHashCode = true;
            }
        }
        Assert.assertTrue(hasHashCode);

        Method privateStopMethod = serverConfigContainer.getClass().getDeclaredMethod(
            "stopCustomThreadPoolMonitor");
        privateStopMethod.setAccessible(true);
        privateStopMethod.invoke(serverConfigContainer);

        Assert.assertEquals(customThreadPoolMonitorList.size(), 0);

    }
}