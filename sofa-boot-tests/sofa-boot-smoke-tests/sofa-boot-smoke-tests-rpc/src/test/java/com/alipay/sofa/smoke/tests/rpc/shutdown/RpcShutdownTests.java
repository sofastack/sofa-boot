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
package com.alipay.sofa.smoke.tests.rpc.shutdown;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.smoke.tests.rpc.ActivelyDestroyTests;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import com.alipay.sofa.smoke.tests.rpc.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for rpc shutdown.
 */
@SpringBootTest(classes = RpcSofaBootApplication.class)
@Import(RpcShutdownTests.RpcShutdownConfiguration.class)
public class RpcShutdownTests extends ActivelyDestroyTests implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Test
    @DirtiesContext
    public void checkPort() {
        assertThat(TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT)).isFalse();
    }

    @AfterAll
    public static void rpcGracefulShutdown() {
        ((ConfigurableApplicationContext) applicationContext).close();

        boolean portAvailable = false;
        //in case of graceful shutdown too slow
        for (int i = 0; i < 3; i++) {
            portAvailable = TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT);
            if (portAvailable) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertThat(portAvailable).isTrue();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RpcShutdownTests.applicationContext = applicationContext;
    }

    @Configuration
    @ImportResource("/spring/shutdown.xml")
    static class RpcShutdownConfiguration {

    }
}
