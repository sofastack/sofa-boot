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
package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version ReferenceRequireTests.java, v 0.1 2023年04月19日 5:29 PM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(ReferenceRequireTests.ReferenceConfiguration.class)
public class ReferenceRequireTests {

    @Autowired
    private SofaRuntimeManager sofaRuntimeManager;

    @Test
    public void checkReferenceRequires() {
        Collection<ComponentInfo> componentInfos =
                sofaRuntimeManager.getComponentManager().getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        assertThat(componentInfos.size()).isEqualTo(3);
        assertThat(componentInfos.stream()).allMatch(componentInfo -> componentInfo.isHealthy().isHealthy());
    }

    @Configuration
    @ImportResource("classpath:spring/reference/test-reference.xml")
    static class ReferenceConfiguration implements ClientFactoryAware, InitializingBean {

        @SofaReference(uniqueId = "A", required = false)
        private SampleService sampleService;

        private ClientFactory clientFactory;

        @Override
        public void setClientFactory(ClientFactory clientFactory) {
            this.clientFactory = clientFactory;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            ReferenceClient referenceClient = clientFactory.getClient(ReferenceClient.class);
            ReferenceParam<SampleService> referenceParam = new ReferenceParam<>();
            referenceParam.setInterfaceType(SampleService.class);
            referenceParam.setUniqueId("C");
            referenceParam.setRequired(false);
            referenceClient.reference(referenceParam);
        }
    }
}
