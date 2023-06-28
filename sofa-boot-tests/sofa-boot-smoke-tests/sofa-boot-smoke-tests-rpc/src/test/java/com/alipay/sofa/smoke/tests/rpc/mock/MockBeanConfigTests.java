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
package com.alipay.sofa.smoke.tests.rpc.mock;

import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ConsumerConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.binding.BoltBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.param.BoltBindingParam;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.common.MockMode;
import com.alipay.sofa.rpc.common.json.JSON;
import com.alipay.sofa.runtime.api.annotation.SofaParameter;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.invoke.HelloSyncService;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Integration tests for mock.
 *
 * @author zhaowang
 * @version : MockBeanConfigTest.java, v 0.1 2020年03月10日 9:12 下午 zhaowang Exp $
 */
@SpringBootTest(classes = RpcSofaBootApplication.class)
@TestPropertySource(properties = { "sofa.boot.rpc" + ".consumer.repeated.reference.limit=10", })
@Import(MockBeanConfigTests.MockBeanConfiguration.class)
public class MockBeanConfigTests {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", mockMode = MockMode.LOCAL, mockBean = "mockHello"))
    private HelloSyncService     helloSyncService;

    @Autowired
    private ConsumerConfigHelper consumerConfigHelper;

    @Test
    public void localMock() {
        String s = helloSyncService.saySync("mock");
        assertThat(s).isEqualTo("mock");
    }

    @Test
    public void emptyMockBean() {
        RpcBindingParam rpcBindingParam = new BoltBindingParam();

        RpcBinding rpcBinding = new BoltBinding(rpcBindingParam, null, true);
        try {
            consumerConfigHelper.getMockRef(rpcBinding, null);
            fail("");
        } catch (IllegalArgumentException e) {
            // success;
        }
    }

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", mockMode = MockMode.REMOTE, parameters = @SofaParameter(key = "mockUrl", value = "http://127.0.0.1:1235/")))
    private HelloSyncService remoteMock;

    @Test
    public void remote() {

        HttpMockServer.initSever(1235);
        HttpMockServer.addMockPath("/", JSON.toJSONString("mockJson"));
        HttpMockServer.start();

        assertThat(remoteMock.saySync(("xx"))).isEqualTo("mockJson");
        HttpMockServer.stop();
    }

    @Configuration
    @ImportResource("/spring/test_only_mock_bean.xml")
    static class MockBeanConfiguration {

    }
}
