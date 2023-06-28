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
package com.alipay.sofa.smoke.tests.rpc;

import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration Tests for {@link SofaBootRpcLoggerFactory}.
 */
@SpringBootTest(classes = RpcSofaBootApplication.class)
@TestPropertySource(properties = { "logging.level.com.alipay.sofa.rpc=DEBUG",
                                  "logging.level.com.alipay.sofa.rpc.boot=ERROR",
                                  "logging.level.com.user.app=WARN" })
public class SofaRpcLoggerLevelConfigTests {

    @Test
    public void testRpcLoggerLevel() {
        Logger rpcLogger = LoggerFactory.getLogger("sofa.boot.rpc.transport");
        assertTrue(rpcLogger.isDebugEnabled());
    }

    @Test
    public void testStarterLoggerLevel() {
        org.slf4j.Logger starterLogger = SofaBootRpcLoggerFactory
            .getLogger("sofa.boot.rpc.boot.xxx");
        assertTrue(starterLogger.isErrorEnabled());
    }

    @Test
    public void testOtherLoggerLevel() {
        Logger rpcLogger = LoggerFactory.getLogger("sofa.boot.rpc.xxx");
        assertTrue(rpcLogger.isDebugEnabled());
    }

    @Test
    public void testUserAppLoggerLevel() {
        org.slf4j.Logger userLogger = org.slf4j.LoggerFactory.getLogger("com.user.app.xxx");
        assertTrue(userLogger.isWarnEnabled());
    }

}
