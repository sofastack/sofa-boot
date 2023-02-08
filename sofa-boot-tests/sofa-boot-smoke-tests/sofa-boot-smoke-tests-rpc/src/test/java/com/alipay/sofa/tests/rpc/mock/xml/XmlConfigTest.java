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
package com.alipay.sofa.tests.rpc.mock.xml;

import com.alipay.sofa.rpc.common.json.JSON;
import com.alipay.sofa.tests.rpc.bean.invoke.HelloSyncService;
import com.alipay.sofa.tests.rpc.mock.HttpMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhaowang
 * @version : XmlConfigTest.java, v 0.1 2020年03月10日 11:40 下午 zhaowang Exp $
 */
@SpringBootApplication
@SpringBootTest
@ImportResource("/spring/test_only_mock.xml")
public class XmlConfigTest {

    @Autowired
    @Qualifier("xmlLocalMock")
    private HelloSyncService xmlLocalMock;

    @Test
    public void testXmlLocalMock() {
        String xml = xmlLocalMock.saySync("xml");
        assertThat(xml).isEqualTo("xml");
    }

    @Autowired
    @Qualifier("xmlRemoteMock")
    private HelloSyncService xmlRemoteMock;

    @Test
    public void testXmlRemote() {

        HttpMockServer.initSever(1236);
        HttpMockServer.addMockPath("/", JSON.toJSONString("mockJson"));
        HttpMockServer.start();
        assertThat(xmlRemoteMock.saySync("xx")).isEqualTo("mockJson");

        HttpMockServer.stop();
    }
}
