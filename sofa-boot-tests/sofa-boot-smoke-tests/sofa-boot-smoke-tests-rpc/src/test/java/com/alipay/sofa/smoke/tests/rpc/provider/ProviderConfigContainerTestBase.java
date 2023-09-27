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
package com.alipay.sofa.smoke.tests.rpc.provider;

import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.SampleFacade;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.SampleFacadeImpl;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author huzijie
 * @version ProviderConfigContainerTests.java, v 0.1 2023年09月27日 11:14 AM huzijie Exp $
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = "sofa.boot.actuator.health.skipAll=true", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(ProviderConfigContainerTestBase.RpcPublishConfiguration.class)
public class ProviderConfigContainerTestBase {

    @SofaReference(jvmFirst = false, binding = @SofaReferenceBinding(bindingType = "bolt"))
    protected SampleFacade          sampleFacadeA;

    @SofaReference(jvmFirst = false, binding = @SofaReferenceBinding(bindingType = "bolt"), uniqueId = "uniqueId")
    protected SampleFacade          sampleFacadeB;

    @Autowired
    private ProviderConfigContainer providerConfigContainer;

    @AfterEach
    private void clearExported() {
        providerConfigContainer.unExportAllProviderConfig();
    }

    @Configuration
    static class RpcPublishConfiguration {

        @SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") })
        @Bean
        public SampleFacade providerA() {
            return new SampleFacadeImpl();
        }

        @SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt") }, uniqueId = "uniqueId")
        @Bean
        public SampleFacade providerB() {
            return new SampleFacadeImpl();
        }
    }

}
