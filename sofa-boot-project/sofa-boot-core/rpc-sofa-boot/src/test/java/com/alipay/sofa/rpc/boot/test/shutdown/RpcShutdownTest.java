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
package com.alipay.sofa.rpc.boot.test.shutdown;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.boot.test.util.TestUtils;

@SpringBootApplication
@SpringBootTest(classes = RpcShutdownTest.class)
@RunWith(SpringRunner.class)
@ImportResource("/spring/shutdown.xml")
public class RpcShutdownTest extends ActivelyDestroyTest implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Test
    public void test() {
        Assert.assertFalse(TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT));
    }

    @AfterClass
    public static void testRpcGracefulShutdown() {
        ((ConfigurableApplicationContext) applicationContext).close();

        boolean portAvailale = false;
        //in case of graceful shutdown too slow
        for (int i = 0; i < 3; i++) {
            portAvailale = TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT);
            if (portAvailale) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(portAvailale);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RpcShutdownTest.applicationContext = applicationContext;
    }
}
