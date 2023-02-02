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
package com.alipay.sofa.smoke.tests.runtime.impl;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.component.ComponentLifeCycle;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

import static com.alipay.sofa.runtime.service.component.ServiceComponent.SERVICE_COMPONENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ComponentManager}.
 *
 * @author xuanbei
 * @author qilong.zql
 * @author huzijie
 * @since 3.2.0
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(ComponentManagerTests.ComponentManagerTestConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComponentManagerTests {

    @Autowired
    private SofaRuntimeContext     sofaRuntimeContext;

    @Autowired
    private DemoComponentLifeCycle demoComponent;

    @Test
    @Order(1)
    public void appName() {
        assertThat("smoke-tests-runtime").isEqualTo(sofaRuntimeContext.getAppName());
    }

    @Test
    @Order(2)
    public void registerServiceDuplicated() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ComponentName componentName = ComponentNameFactory.createComponentName(
            SERVICE_COMPONENT_TYPE, SampleService.class, "");
        ComponentInfo componentInfo = componentManager.getComponentInfo(componentName);
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.setId("testModuleName");
        componentInfo.setApplicationContext(applicationContext);
        componentManager.register(componentInfo);
        assertThat("testModuleName").isEqualTo(componentInfo.getApplicationContext().getId());
    }

    // must run last
    @Test
    @Order(3)
    public void componentLifeCycle() {
        assertThat("activated").isEqualTo(demoComponent.service());
        sofaRuntimeContext.getSofaRuntimeManager().shutdown();
        assertThat("deactivated").isEqualTo(demoComponent.service());
    }

    @TestConfiguration
    static class ComponentManagerTestConfiguration {

        @Bean
        @SofaService
        public SampleService demoService() {
            return new SampleServiceImpl();
        }

        @Bean
        @SofaService
        public DemoComponentLifeCycle demoComponentLifeCycle() {
            return new DemoComponentLifeCycle("init");
        }
    }

    static class DemoComponentLifeCycle extends SampleServiceImpl implements ComponentLifeCycle {

        public DemoComponentLifeCycle(String message) {
            super(message);
        }

        @Override
        public void activate() throws ServiceRuntimeException {
            message = "activated";
        }

        @Override
        public void deactivate() throws ServiceRuntimeException {
            message = "deactivated";
        }
    }
}
