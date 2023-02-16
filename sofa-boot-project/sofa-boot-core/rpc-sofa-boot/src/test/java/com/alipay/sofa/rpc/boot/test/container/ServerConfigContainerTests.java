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
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.config.UserThreadPoolManager;
import com.alipay.sofa.rpc.context.RpcInternalContext;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.context.RpcRunningState;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.rpc.server.UserThreadPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ServerConfigContainer}.
 * 
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class ServerConfigContainerTests {

    private final ServerConfigContainer serverConfigContainer;

    public ServerConfigContainerTests() {
        serverConfigContainer = new ServerConfigContainer();
    }

    @BeforeAll
    public static void adBeforeClass() {
        RpcRunningState.setUnitTestMode(true);
    }

    @AfterAll
    public static void adAfterClass() {
        RpcRuntimeContext.destroy();
        RpcInternalContext.removeContext();
        RpcInvokeContext.removeContext();
    }

    @Test
    public void boltConfiguration() {
        serverConfigContainer.setBoltPortStr("9090");
        serverConfigContainer.setBoltThreadPoolCoreSizeStr("8080");
        serverConfigContainer.setBoltThreadPoolMaxSizeStr("7070");
        serverConfigContainer.setBoltAcceptsSizeStr(("6060"));
        serverConfigContainer.setVirtualHostStr("127.0.0.2");
        serverConfigContainer.setBoundHostStr("127.0.0.3");
        serverConfigContainer.setVirtualPortStr("8888");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        assertThat(9090).isEqualTo(serverConfig.getPort());
        assertThat(8080).isEqualTo(serverConfig.getCoreThreads());
        assertThat(7070).isEqualTo(serverConfig.getMaxThreads());
        assertThat(6060).isEqualTo(serverConfig.getAccepts());
        assertThat(8888).isEqualTo(serverConfig.getVirtualPort().intValue());
        assertThat("127.0.0.2").isEqualTo(serverConfig.getVirtualHost());
        assertThat("127.0.0.3").isEqualTo(serverConfig.getBoundHost());
    }

    @Test
    @Disabled("only can run in multi ip env")
    public void boltIpCustomConfiguration() {
        serverConfigContainer.setEnabledIpRange("192.168");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        assertThat("192.168").isEqualTo(serverConfig.getVirtualHost());
    }

    @Test
    public void boltServerDefaultPort() {
        serverConfigContainer.setBoltPortStr("");
        ServerConfig serverConfig = serverConfigContainer.createBoltServerConfig();
        assertThat(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT).isEqualTo(serverConfig.getPort());
    }

    @Test
    public void dubboServerConfiguration() {
        serverConfigContainer.setDubboPortStr("9696");
        serverConfigContainer.setDubboIoThreadSizeStr("8686");
        serverConfigContainer.setDubboThreadPoolMaxSizeStr("7676");
        serverConfigContainer.setDubboAcceptsSizeStr("6666");

        ServerConfig serverConfig = serverConfigContainer.createDubboServerConfig();

        assertThat(9696).isEqualTo(serverConfig.getPort());
        assertThat(8686).isEqualTo(serverConfig.getIoThreads());
        assertThat(7676).isEqualTo(serverConfig.getMaxThreads());
        assertThat(6666).isEqualTo(serverConfig.getAccepts());
    }

    @Test
    public void restServerConfiguration() {
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

        assertThat("host_name").isEqualTo(serverConfig.getBoundHost());
        assertThat(123).isEqualTo(serverConfig.getPort());
        assertThat(456).isEqualTo(serverConfig.getIoThreads());
        assertThat("/api/").isEqualTo(serverConfig.getContextPath());
        assertThat(789).isEqualTo(serverConfig.getMaxThreads());
        assertThat(1000).isEqualTo(serverConfig.getPayload());
        assertThat(serverConfig.isTelnet()).isTrue();
        assertThat(serverConfig.isDaemon()).isTrue();
        assertThat("a.com").isEqualTo(
            serverConfig.getParameters().get(RpcConstants.ALLOWED_ORIGINS));

    }

    @Test
    public void customServerConfig() {

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(123);
        final String protocol = "xxx";
        serverConfigContainer.registerCustomServerConfig(protocol, serverConfig);

        ServerConfig serverConfig2 = serverConfigContainer.getServerConfig(protocol);

        assertThat(123).isEqualTo(serverConfig2.getPort());
        assertThat(serverConfig.getPort()).isEqualTo(serverConfig2.getPort());

        serverConfigContainer.unRegisterCustomServerConfig(protocol);
        assertThatThrownBy(() -> serverConfigContainer.getServerConfig(protocol))
                .isInstanceOf(SofaBootRpcRuntimeException.class);

    }

    @Test
    public void customServerConfigTwice() {

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(123);
        final String protocol = "xxx";
        serverConfigContainer.registerCustomServerConfig(protocol, serverConfig);

        ServerConfig serverConfig2 = serverConfigContainer.getServerConfig(protocol);

        assertThat(123).isEqualTo(serverConfig2.getPort());
        assertThat(serverConfig.getPort()).isEqualTo(serverConfig2.getPort());

        boolean twiceResult = serverConfigContainer.registerCustomServerConfig(protocol,
            serverConfig);

        assertThat(twiceResult).isFalse();

    }

    @Test
    public void createHttpServerConfig() {
        serverConfigContainer.setHttpPortStr("8080");
        serverConfigContainer.setHttpThreadPoolCoreSizeStr("5");
        serverConfigContainer.setHttpThreadPoolMaxSizeStr("10");
        serverConfigContainer.setHttpAcceptsSizeStr("1");
        serverConfigContainer.setHttpThreadPoolQueueSizeStr("8");

        ServerConfig serverConfig = serverConfigContainer
            .getServerConfig(SofaBootRpcConfigConstants.RPC_PROTOCOL_HTTP);

        assertThat(8080).isEqualTo(serverConfig.getPort());
        assertThat(5).isEqualTo(serverConfig.getCoreThreads());
        assertThat(10).isEqualTo(serverConfig.getMaxThreads());
        assertThat(1).isEqualTo(serverConfig.getAccepts());
        assertThat(8).isEqualTo(serverConfig.getQueues());
    }

    @Test
    public void startCustomThreadPoolMonitor() throws NoSuchMethodException,
                                              IllegalAccessException, InvocationTargetException,
                                              NoSuchFieldException {
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
        assertThat(customThreadPoolMonitorList.size()).isEqualTo(4);

        boolean hasHashCode = false;
        for (RpcThreadPoolMonitor monitor : customThreadPoolMonitorList) {
            if (monitor.getPoolName().contains("same-name-")) {
                hasHashCode = true;
            }
        }
        assertThat(hasHashCode).isTrue();

        Method privateStopMethod = serverConfigContainer.getClass().getDeclaredMethod(
            "stopCustomThreadPoolMonitor");
        privateStopMethod.setAccessible(true);
        privateStopMethod.invoke(serverConfigContainer);

        assertThat(customThreadPoolMonitorList.size()).isEqualTo(0);

    }
}
