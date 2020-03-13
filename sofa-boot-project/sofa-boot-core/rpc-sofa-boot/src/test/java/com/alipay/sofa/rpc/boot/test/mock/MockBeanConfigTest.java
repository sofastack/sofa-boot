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
package com.alipay.sofa.rpc.boot.test.mock;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ConsumerConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.binding.BoltBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.param.BoltBindingParam;
import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.rpc.boot.test.bean.invoke.HelloSyncService;
import com.alipay.sofa.rpc.common.MockMode;
import com.alipay.sofa.rpc.common.json.JSON;
import com.alipay.sofa.runtime.api.annotation.SofaParameter;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhaowang
 * @version : MockBeanConfigTest.java, v 0.1 2020年03月10日 9:12 下午 zhaowang Exp $
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest
@ImportResource("/spring/test_only_mock_bean.xml")
@TestPropertySource(properties = { SofaBootRpcProperties.PREFIX
                                   + ".consumer.repeated.reference.limit=10", })
public class MockBeanConfigTest {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", mockMode = MockMode.LOCAL, mockBean = "mockHello"))
    private HelloSyncService     helloSyncService;

    @Autowired
    private ConsumerConfigHelper consumerConfigHelper;

    @Test
    public void testLocalMock() {
        String s = helloSyncService.saySync("mock");
        Assert.assertEquals("mock", s);
    }

    @Test
    public void testEmptyMockBean() {
        RpcBindingParam rpcBindingParam = new BoltBindingParam();

        RpcBinding rpcBinding = new BoltBinding(rpcBindingParam, null, true);
        try {
            consumerConfigHelper.getMockRef(rpcBinding, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success;
        }
    }

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", mockMode = MockMode.REMOTE, parameters = @SofaParameter(key = "mockUrl", value = "http://127.0.0.1:1235/")))
    private HelloSyncService remoteMock;

    @Test
    public void testRemote() {

        HttpMockServer.initSever(1235);
        HttpMockServer.addMockPath("/", JSON.toJSONString("mockJson"));
        HttpMockServer.start();

        Assert.assertEquals("mockJson", remoteMock.saySync(("xx")));

        HttpMockServer.stop();
    }

}