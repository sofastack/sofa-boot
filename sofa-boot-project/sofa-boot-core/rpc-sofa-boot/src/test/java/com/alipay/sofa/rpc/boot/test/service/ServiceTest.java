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
package com.alipay.sofa.rpc.boot.test.service;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.test.bean.SampleService;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;

/**
 * @author qilong.zql
 * @since 6.0.4
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {

    @Autowired
    private ServerConfigContainer serverConfigContainer;
    private static ServerConfig   serverConfig;

    @Test
    @DirtiesContext
    public void testService() {
        Assert.assertNotNull(serverConfigContainer);
        serverConfig = serverConfigContainer
            .getServerConfig(SofaBootRpcConfigConstants.RPC_PROTOCOL_BOLT);
        Assert.assertNotNull(serverConfig);
        Assert.assertNotNull(serverConfig.getServer());
        Assert.assertFalse(serverConfig.getServer().hasNoEntry());
    }

    @AfterClass
    public static void afterClass() {
        Assert.assertNull(serverConfig.getServer());
    }

    @Configuration
    @EnableAutoConfiguration
    static class ServiceTestConfiguration {
        @Bean
        @SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
        public SampleService sampleService() {
            return new SampleService() {
                @Override
                public String echoStr(String name) {
                    return "sampleService";
                }
            };
        }
    }
}