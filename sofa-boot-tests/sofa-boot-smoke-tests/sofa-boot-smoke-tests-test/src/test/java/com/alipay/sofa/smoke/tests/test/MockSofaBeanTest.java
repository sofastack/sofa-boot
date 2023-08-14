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
package com.alipay.sofa.smoke.tests.test;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.smoke.tests.test.base.SofaIntegrationTestBaseCase;
import com.alipay.sofa.test.api.annotation.SofaMockBeanFor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class MockSofaBeanTest extends SofaIntegrationTestBaseCase {
    @SofaReference
    private ExampleService exampleService;

    @SofaMockBeanFor(target = ExampleService.class, field = "clientA")
    private ExternalServiceClient mockClient;

    @Test
    public void test_mock_external_service() {
        assertThat(mockClient).isNotNull();
        assertThat(TestUtils.isMock(mockClient)).isTrue();

        assertThat(exampleService).isNotNull();
        assertThat(exampleService.getDependency("A")).isEqualTo(mockClient);

        given(mockClient.invoke(any()))
                .willReturn("SUCCESS");

        String result = exampleService.execute("A", "HELLO WORLD");
        assertThat(result).isEqualTo("SUCCESS");
    }
}