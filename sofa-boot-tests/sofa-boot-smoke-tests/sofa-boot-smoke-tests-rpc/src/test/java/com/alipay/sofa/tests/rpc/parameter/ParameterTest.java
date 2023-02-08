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
package com.alipay.sofa.tests.rpc.parameter;

import com.alipay.sofa.tests.rpc.bean.filter.ParameterFilter;
import com.alipay.sofa.tests.rpc.bean.invoke.HelloSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:scienjus@gmail.com">ScienJus</a>
 */
@SpringBootApplication
@SpringBootTest(classes = ParameterTest.class)
@TestPropertySource(properties = {
                                  "sofa.boot.rpc.registry.address=", // override default zk path
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
        assertThat(parameterFilter.getConsumerParameters()).isNull();
        assertThat(parameterFilter.getProviderParameters()).isNull();

        helloSyncService.saySync("sync");

        for (Map<String, String> parameters : Arrays.asList(
            parameterFilter.getConsumerParameters(), parameterFilter.getProviderParameters())) {
            assertThat(parameters.size()).isEqualTo(2);
            assertThat(parameters.get("static_key")).isEqualTo("static_value");
            assertThat(parameters.get("dynamic_test_key")).isEqualTo("dynamic_test_value");
        }
    }
}
