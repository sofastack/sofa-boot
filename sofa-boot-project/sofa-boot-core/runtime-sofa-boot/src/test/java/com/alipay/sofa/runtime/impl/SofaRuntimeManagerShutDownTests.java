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
package com.alipay.sofa.runtime.impl;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.context.SpringContextComponent;
import com.alipay.sofa.runtime.context.SpringContextImplementation;
import com.alipay.sofa.runtime.sample.DemoComponent;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import static com.alipay.sofa.runtime.context.SpringContextComponent.SPRING_COMPONENT_TYPE;
import static com.alipay.sofa.runtime.sample.DemoComponent.DEMO_COMPONENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaRuntimeManager#shutdown()}.
 *
 * @author huzijie
 * @version SofaRuntimeManagerShutDownTests.java, v 0.1 2022年04月29日 5:19 PM huzijie Exp $
 */
public class SofaRuntimeManagerShutDownTests {

    private final ClientFactoryInternal clientFactory = new ClientFactoryImpl();

    @Test
    public void normalShutdown() {
        ComponentManager componentManager = initComponentManager(false, false);
        ComponentInfo demoComponentInfo = componentManager
            .getComponentInfosByType(DEMO_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        assertThat(componentManager.size()).isEqualTo(2);
        assertThat(demoComponentInfo.isActivated()).isTrue();
        assertThat(springComponentInfo.isActivated()).isTrue();
        assertThat(applicationContext.isActive()).isTrue();
        componentManager.shutdown();
        assertThat(componentManager.size()).isEqualTo(0);
        assertThat(demoComponentInfo.isActivated()).isFalse();
        assertThat(springComponentInfo.isActivated()).isFalse();
        assertThat(applicationContext.isActive()).isFalse();
    }

    @Test
    public void skipAllComponentShutdown() {
        ComponentManager componentManager = initComponentManager(true, false);
        ComponentInfo demoComponentInfo = componentManager
            .getComponentInfosByType(DEMO_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        assertThat(componentManager.size()).isEqualTo(2);
        assertThat(demoComponentInfo.isActivated()).isTrue();
        assertThat(springComponentInfo.isActivated()).isTrue();
        assertThat(applicationContext.isActive()).isTrue();
        componentManager.shutdown();
        assertThat(componentManager.size()).isEqualTo(2);
        assertThat(demoComponentInfo.isActivated()).isTrue();
        assertThat(springComponentInfo.isActivated()).isTrue();
        assertThat(applicationContext.isActive()).isTrue();
    }

    @Test
    public void skipCommonComponentShutdown() {
        ComponentManager componentManager = initComponentManager(false, true);
        ComponentInfo demoComponentInfo = componentManager
            .getComponentInfosByType(DEMO_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        assertThat(componentManager.size()).isEqualTo(2);
        assertThat(demoComponentInfo.isActivated()).isTrue();
        assertThat(springComponentInfo.isActivated()).isTrue();
        assertThat(applicationContext.isActive()).isTrue();
        componentManager.shutdown();
        assertThat(componentManager.size()).isEqualTo(1);
        assertThat(demoComponentInfo.isActivated()).isTrue();
        assertThat(springComponentInfo.isActivated()).isFalse();
        assertThat(applicationContext.isActive()).isFalse();
    }

    private ComponentManager initComponentManager(boolean skipAll, boolean skipComponent) {
        StandardSofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager("testApp",
            this.getClass().getClassLoader(), clientFactory);
        ComponentManager componentManager = sofaRuntimeManager.getComponentManager();
        SofaRuntimeContext sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        sofaRuntimeContext.getProperties().setSkipAllComponentShutdown(skipAll);
        sofaRuntimeContext.getProperties().setSkipCommonComponentShutdown(skipComponent);

        ComponentInfo demoComponent = new DemoComponent();
        componentManager.register(demoComponent);

        GenericApplicationContext applicationContext = new GenericApplicationContext();
        ComponentName springComponentName = ComponentNameFactory.createComponentName(
            SPRING_COMPONENT_TYPE, "testModule");
        ComponentInfo springComponentInfo = new SpringContextComponent(springComponentName,
            new SpringContextImplementation(applicationContext), sofaRuntimeContext);
        applicationContext.refresh();
        componentManager.register(springComponentInfo);

        return componentManager;
    }
}
