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
package com.alipay.sofa.rpc.boot.test.registry;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.tests.rpc.ActivelyDestroyTest;
import com.alipay.sofa.tests.rpc.bean.registry.MultiRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhiyuan.lzy
 * @version $Id: MeshTest.java, v 0.1 2018-06-25 19:26 zhiyuan.lzy Exp $$
 */
@SpringBootApplication
@SpringBootTest(properties = { "com.alipay.sofa.rpc.registries.gateway=zookeeper://127.0.0.1:2181" }, classes = MultiRegistryTest.class)
@ImportResource("/spring/test_only_registry.xml")
public class MultiRegistryTest extends ActivelyDestroyTest {

    @Autowired
    private MultiRegistryService multiRegistryServiceRef;

    @Test
    public void testInvokeWithMultiRegistry() throws InterruptedException {

        try {
            String result = multiRegistryServiceRef.saySync("sync");
            System.out.println("multi registry:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getClass()).isEqualTo(SofaRpcException.class);
        }
    }

}
