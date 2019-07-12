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
package com.alipay.sofa.rpc.boot.test.parameter;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.test.bean.filter.ParameterFilter;
import com.alipay.sofa.rpc.boot.test.bean.invoke.HelloSyncService;

/**
 * @author <a href="mailto:scienjus@gmail.com">ScienJus</a>
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParameterTest.class)
@TestPropertySource(properties = {
                                  "com.alipay.sofa.rpc.registry.address=", // override default zk path
                                  "dynamic_key=dynamic_test_key",
                                  "dynamic_value=dynamic_test_value" })
@ImportResource("/spring/test_only_parameter.xml")
public class ParameterTest {

    @Autowired
    private HelloSyncService helloSyncService;

    @Autowired
    private ParameterFilter  parameterFilter;

    @Test
    public void testParameter() {
        Assert.assertNull(parameterFilter.getConsumerParameters());
        Assert.assertNull(parameterFilter.getProviderParameters());

        helloSyncService.saySync("sync");

        for (Map<String, String> parameters : Arrays.asList(
            parameterFilter.getConsumerParameters(), parameterFilter.getProviderParameters())) {
            Assert.assertEquals(2, parameters.size());
            Assert.assertEquals("static_value", parameters.get("static_key"));
            Assert.assertEquals("dynamic_test_value", parameters.get("dynamic_test_key"));
        }
    }
}
