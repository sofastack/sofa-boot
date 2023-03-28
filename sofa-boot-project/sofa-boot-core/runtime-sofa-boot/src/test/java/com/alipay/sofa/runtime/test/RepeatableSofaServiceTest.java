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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServices;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

/**
 * Tests for resolve repeatable @SofaService annotation.
 *
 * @author huzijie
 * @version RepeatableSofaServiceTest.java, v 0.1 2023年03月28日 8:35 PM huzijie Exp $
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=repeatableSofaServiceTest")
@Import({ RepeatableSofaServiceTest.RepeatableClassA.class,
         RepeatableSofaServiceTest.RepeatableClassB.class,
         RepeatableSofaServiceTest.RepeatableClassD.class,
         RepeatableSofaServiceTest.RepeatableConfiguration.class })
public class RepeatableSofaServiceTest {

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void checkSofaService() {
        Collection<ComponentInfo> components = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        Assert.assertTrue(findServiceByUniqueId(components, "A"));
        Assert.assertTrue(findServiceByUniqueId(components, "B"));
        Assert.assertTrue(findServiceByUniqueId(components, "C"));
        Assert.assertTrue(findServiceByUniqueId(components, "D"));
        Assert.assertFalse(findServiceByUniqueId(components, "E"));
        Assert.assertFalse(findServiceByUniqueId(components, "F"));
        Assert.assertTrue(findServiceByUniqueId(components, "G"));
        Assert.assertTrue(findServiceByUniqueId(components, "H"));
        Assert.assertTrue(findServiceByUniqueId(components, "I"));
        Assert.assertTrue(findServiceByUniqueId(components, "J"));
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

    @Configuration
    @Import(RuntimeConfiguration.class)
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
