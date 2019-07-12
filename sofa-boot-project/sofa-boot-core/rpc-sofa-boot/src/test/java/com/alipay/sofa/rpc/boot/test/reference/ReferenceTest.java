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
package com.alipay.sofa.rpc.boot.test.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.boot.test.bean.invoke.HelloSyncService;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.core.exception.SofaRouteException;
import com.google.common.collect.Lists;

/**
 *
 * @author zhuoyu.sjw
 * @version $Id: SofaBootRpCReferenceTest.java, v 0.1 2018-06-25 19:26 zhuoyu.sjw Exp $$
 */
@SpringBootApplication
@SpringBootTest(classes = ReferenceTest.class)
@RunWith(SpringRunner.class)
@ImportResource("/spring/test_only_reference.xml")
public class ReferenceTest extends ActivelyDestroyTest {

    @Autowired
    private HelloSyncService      helloSyncService;

    @Autowired
    private ServerConfigContainer serverConfigContainer;

    @Test
    public void testNoServerStarted() {
        List<String> protocols = Lists.newArrayList(SofaBootRpcConfigConstants.RPC_PROTOCOL_BOLT,
            SofaBootRpcConfigConstants.RPC_PROTOCOL_REST,
            SofaBootRpcConfigConstants.RPC_PROTOCOL_H2C,
            SofaBootRpcConfigConstants.RPC_PROTOCOL_DUBBO);

        for (String protocol : protocols) {
            ServerConfig serverConfig = serverConfigContainer.getServerConfig(protocol);
            assertNull(serverConfig.getServer());
        }
    }

    @Test
    public void testInvokeWithoutProvider() throws InterruptedException {

        try {
            helloSyncService.saySync("sync");
        } catch (Exception e) {
            assertEquals(SofaRouteException.class, e.getClass());
        }
    }

}
