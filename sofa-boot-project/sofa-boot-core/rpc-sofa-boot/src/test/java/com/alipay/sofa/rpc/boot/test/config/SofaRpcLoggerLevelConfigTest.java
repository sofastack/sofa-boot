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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;

/**
 * logger must be init when use. or will init by test case .not starter framework.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "logging.level.com.alipay.sofa.rpc=DEBUG",
                              "logging.level.com.alipay.sofa.rpc.boot=ERROR",
                              "logging.level.com.user.app=WARN" })
public class SofaRpcLoggerLevelConfigTest {

    @Test
    public void testRpcLoggerLevel() {
        Logger rpcLogger = LoggerFactory.getLogger("com.alipay.sofa.rpc.transport");
        Assert.assertTrue(rpcLogger.isDebugEnabled());
    }

    @Test
    public void testStarterLoggerLevel() {
        org.slf4j.Logger starterLogger = SofaBootRpcLoggerFactory
            .getLogger("com.alipay.sofa.rpc.boot.xxx");
        Assert.assertTrue(starterLogger.isErrorEnabled());
    }

    @Test
    public void testOtherLoggerLevel() {
        Logger rpcLogger = LoggerFactory.getLogger("com.alipay.sofa.rpc.xxx");
        Assert.assertTrue(rpcLogger.isDebugEnabled());
    }

    @Test
    public void testUserAppLogerLevel() {
        org.slf4j.Logger userLogger = org.slf4j.LoggerFactory.getLogger("com.user.app.xxx");
        Assert.assertTrue(userLogger.isWarnEnabled());
    }

}
