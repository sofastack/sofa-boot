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
package com.alipay.sofa.rpc.boot.test.provider;

import com.alipay.sofa.rpc.core.exception.SofaRouteException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * @author huzijie
 * @version WhiteListProviderConfigContainerTests.java, v 0.1 2023年09月27日 11:27 AM huzijie Exp $
 */
@TestPropertySource(properties = "com.alipay.sofa.rpc.providerRegisterWhiteList=com.alipay.sofa.rpc.boot.test.bean.SampleFacade:uniqueId")
public class WhiteListProviderConfigContainerTests extends ProviderConfigContainerTestBase {

    @Test
    public void checkProviderExported() {
        Assertions.assertThatThrownBy(() -> sampleFacadeA.sayHi("Sofa")).isInstanceOf(SofaRouteException.class).
        hasMessageContaining("RPC-020060001");
        Assertions.assertThat(sampleFacadeB.sayHi("Sofa")).isEqualTo("hi Sofa!");
    }
}
