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

import static com.alipay.sofa.runtime.service.component.ServiceComponent.SERVICE_COMPONENT_TYPE;

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

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 * @author xuanbei
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.application.name=ComponentManagerTest")
public class ComponentManagerTest {
    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void testRegisterServiceDuplicated() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ComponentName componentName = ComponentNameFactory.createComponentName(
            SERVICE_COMPONENT_TYPE, SampleService.class, "");
        ComponentInfo componentInfo = componentManager.getComponentInfo(componentName);
        componentManager.register(componentInfo);
    }

    @Test
    public void testAppName() {
        Assert.assertEquals("ComponentManagerTest", sofaRuntimeContext.getAppName());
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    static class ComponentManagerTestConfiguration {
        @Bean
        @SofaService
        public SampleService sampleService() {
            return new DefaultSampleService();
        }
    }
}
