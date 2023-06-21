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

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServices;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for resolve repeatable @SofaService annotation.
 *
 * @author huzijie
 * @version RepeatableSofaServiceTest.java, v 0.1 2023年03月28日 8:35 PM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import({ RepeatableSofaServiceTests.RepeatableClassA.class,
         RepeatableSofaServiceTests.RepeatableClassB.class,
         RepeatableSofaServiceTests.RepeatableClassD.class,
         RepeatableSofaServiceTests.RepeatableClassE.class,
         RepeatableSofaServiceTests.RepeatableConfiguration.class })
public class RepeatableSofaServiceTests {

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void checkSofaService() {
        Collection<ComponentInfo> components = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        assertThat((findServiceByUniqueId(components, "A"))).isTrue();
        assertThat((findServiceByUniqueId(components, "B"))).isTrue();
        assertThat((findServiceByUniqueId(components, "C"))).isTrue();
        assertThat((findServiceByUniqueId(components, "D"))).isTrue();
        assertThat((findServiceByUniqueId(components, "E"))).isFalse();
        assertThat((findServiceByUniqueId(components, "F"))).isFalse();
        assertThat((findServiceByUniqueId(components, "G"))).isTrue();
        assertThat((findServiceByUniqueId(components, "H"))).isTrue();
        assertThat((findServiceByUniqueId(components, "I"))).isTrue();
        assertThat((findServiceByUniqueId(components, "J"))).isTrue();
        assertThat((findServiceByUniqueId(components, "K"))).isTrue();
        assertThat((findServiceByUniqueId(components, "L"))).isTrue();
    }

    private boolean findServiceByUniqueId(Collection<ComponentInfo> componentInfos, String value) {
        return componentInfos.stream().map(componentInfo -> {
            ServiceComponent component = (ServiceComponent) componentInfo;
            Contract serviceContract = component.getService();
            return serviceContract.getUniqueId();
        }).anyMatch(s -> s.equals(value));
    }

    @SofaService(interfaceType = SampleService.class, uniqueId = "A")
    @SofaService(interfaceType = SampleService.class, uniqueId = "B")
    static class RepeatableClassA implements SampleService {

        @Override
        public String service() {
            return null;
        }
    }

    @SofaService(interfaceType = SampleService.class, uniqueId = "C")
    @SofaService(interfaceType = SampleService.class, uniqueId = "D")
    static class RepeatableClassB implements SampleService {

        @Override
        public String service() {
            return null;
        }
    }

    @SofaService(interfaceType = SampleService.class, uniqueId = "E")
    @SofaService(interfaceType = SampleService.class, uniqueId = "F")
    static class RepeatableClassC implements SampleService {

        @Override
        public String service() {
            return null;
        }
    }

    @SofaServices({ @SofaService(interfaceType = SampleService.class, uniqueId = "I"),
            @SofaService(interfaceType = SampleService.class, uniqueId = "J") })
    static class RepeatableClassD implements SampleService {

        @Override
        public String service() {
            return null;
        }
    }

    @SofaServices({ @SofaService(interfaceType = SampleService.class, uniqueId = "L"),
            @SofaService(interfaceType = SampleService.class, uniqueId = "K") })
    static class RepeatableClassE extends RepeatableClassC {

        @Override
        public String service() {
            return null;
        }
    }

    @Configuration
    static class RepeatableConfiguration {

        @Bean
        public RepeatableClassB repeatableClassB() {
            return new RepeatableClassB();
        }

        @Bean
        @SofaService(interfaceType = SampleService.class, uniqueId = "G")
        @SofaService(interfaceType = SampleService.class, uniqueId = "H")
        public RepeatableClassC repeatableClassC() {
            return new RepeatableClassC();
        }
    }

}
