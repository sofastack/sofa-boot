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
package com.alipay.sofa.smoke.tests.rpc.registry;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.smoke.tests.rpc.ActivelyDestroyTests;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.registry.MultiRegistryService;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MultiRegistryService}.
 *
 * @author zhiyuan.lzy
 * @version $Id: MeshTest.java, v 0.1 2018-06-25 19:26 zhiyuan.lzy Exp $$
 */
@SpringBootTest(properties = { "sofa.boot.rpc.registries.gateway=zookeeper://127.0.0.1:2181" }, classes = RpcSofaBootApplication.class)
@Import(MultiRegistryTests.MultiRegistryConfiguration.class)
public class MultiRegistryTests extends ActivelyDestroyTests {

    @Autowired
    private MultiRegistryService multiRegistryServiceRef;

    @Test
    public void invokeWithMultiRegistry() {

        try {
            String result = multiRegistryServiceRef.saySync("sync");
            System.out.println("multi registry:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getClass()).isEqualTo(SofaRpcException.class);
        }
    }

    @Configuration
    @ImportResource("/spring/test_only_registry.xml")
    static class MultiRegistryConfiguration {

    }
}
