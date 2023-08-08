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
package com.alipay.sofa.smoke.tests.integration.test;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.smoke.tests.integration.test.base.SofaIntegrationTestBaseCase;
import com.alipay.sofa.testing.api.annotation.SofaSpyBeanFor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * @author pengym
 * @version SpySpringBeanTest.java, v 0.1 2023年08月08日 20:19 pengym
 */
public class SpySpringBeanTest extends SofaIntegrationTestBaseCase {
    @SofaReference
    private ExampleGenericService service;

    @SofaSpyBeanFor(target = ExampleGenericService.class, field = "clientA")
    private GenericExternalServiceClient<Integer> mock1;
    @SofaSpyBeanFor(target = ExampleGenericService.class, field = "clientB")
    private GenericExternalServiceClient<String>  mock2;

    @Test
    public void test_mock_injection() {
        assertThat(service).isNotNull();

        assertThat(mock1).isNotNull();
        assertThat(TestUtils.isSpy(mock1)).isTrue();
        assertThat(service.getDependency("A")).isEqualTo(mock1);

        assertThat(mock2).isNotNull();
        assertThat(TestUtils.isSpy(mock2)).isTrue();
        assertThat(service.getDependency("B")).isEqualTo(mock2);
    }

    @Test
    public void test_spy_spring_beans() {
        assertThatThrownBy(() -> mock1.invoke(1, 2, 3))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> mock2.invoke("1", "2", "3"))
                .isInstanceOf(UnsupportedOperationException.class);

        String msg1 = "MOCK A";
        String msg2 = "MOCK B";

        doReturn(msg1).when(mock1).invoke(any());
        doReturn(msg2).when(mock2).invoke(any());

        assertThat(service.execute("A"))
                .isEqualTo(msg1);
        assertThat(service.execute("B"))
                .isEqualTo(msg2);
    }
}