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
package com.alipay.sofa.smoke.tests.rpc.mesh;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.smoke.tests.rpc.ActivelyDestroyTests;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.SampleService;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.invoke.HelloSyncService;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhiyuan.lzy
 * @version $Id: MeshTest.java, v 0.1 2018-06-25 19:26 zhiyuan.lzy Exp $$
 * you should change mesh address to test
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = {
                                                                      "sofa.boot.rpc.registries.mesh=mesh://127.0.0.1:13330",
                                                                      "sofa.boot.rpc.enable.mesh=bolt" })
@Import(MeshTests.MeshConfiguration.class)
@Disabled
public class MeshTests extends ActivelyDestroyTests {

    @Autowired
    @Qualifier("helloSyncConsumerMesh")
    private HelloSyncService helloSyncConsumerMesh;

    @Autowired
    private SampleService    sampleService;

    @Test
    @Disabled("需要有 mesh 环境")
    public void invokeWithMesh() throws InterruptedException {

        try {
            String result = sampleService.echoStr("sync");
            System.out.println("mesh:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getClass()).isEqualTo(SofaRpcException.class);
        }
    }

    @Configuration
    @ImportResource("/spring/test_only_mesh.xml")
    static class MeshConfiguration {

    }
}
