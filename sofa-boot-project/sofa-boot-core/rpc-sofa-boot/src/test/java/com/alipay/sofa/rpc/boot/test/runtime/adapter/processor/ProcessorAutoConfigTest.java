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
package com.alipay.sofa.rpc.boot.test.runtime.adapter.processor;

import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhaowang
 * @version : ProcessorAutoConfigTest.java, v 0.1 2020年03月11日 3:49 下午 zhaowang Exp $
 */
@SpringBootTest
@SpringBootApplication
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "com.alipay.sofa.rpc.dynamic-config=apollo",
                                  "com.alipay.sofa.rpc.mock-url=abc", })
public class ProcessorAutoConfigTest {

    @Autowired
    private DynamicConfigProcessor dynamicConfigProcessor;

    @Autowired
    private ConsumerMockProcessor  consumerMockProcessor;

    @Test
    public void testProperty() {
        Assert.assertEquals("apollo", dynamicConfigProcessor.getDynamicConfig());
        Assert.assertEquals("abc", consumerMockProcessor.getMockUrl());

    }
}