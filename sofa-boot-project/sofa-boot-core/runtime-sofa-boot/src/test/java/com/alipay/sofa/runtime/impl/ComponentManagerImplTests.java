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

import com.alipay.sofa.boot.util.LogOutPutUtils;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.context.SpringContextComponent;
import com.alipay.sofa.runtime.context.SpringContextImplementation;
import com.alipay.sofa.runtime.sample.DemoComponent;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.support.GenericApplicationContext;

import static com.alipay.sofa.runtime.context.SpringContextComponent.SPRING_COMPONENT_TYPE;
import static com.alipay.sofa.runtime.sample.DemoComponent.DEMO_COMPONENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ComponentManagerImpl}.
 *
 * @author huzijie
 * @version ComponentManagerImplTests.java, v 0.1 2023年04月10日 3:37 PM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class ComponentManagerImplTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(ComponentManagerImpl.class);
    }

    @Mock
    private ClientFactoryInternal       clientFactoryInternal;

    @Mock
    private DemoComponent               componentInfoA;

    @Mock
    private DemoComponent               componentInfoB;

    private ComponentManagerImpl        componentManager;

    private final ClientFactoryInternal clientFactory = new ClientFactoryImpl();

    @BeforeEach
    public void setUp() {
        componentManager = new ComponentManagerImpl(clientFactoryInternal, this.getClass()
            .getClassLoader());
    }

    @Test
    public void registerAndGetComponents() {
        componentInfoA = new DemoComponent("A");
        componentInfoB = new DemoComponent("B");
        componentManager.register(componentInfoA);

        assertThat(componentManager.registerAndGet(componentInfoB)).isEqualTo(componentInfoB);
        assertThat(componentManager.getComponentInfos()).contains(componentInfoA, componentInfoB);
        assertThat(componentManager.getComponents()).contains(componentInfoA, componentInfoB);
        assertThat(componentManager.getComponentTypes()).containsExactly(componentInfoA.getType());
        assertThat(componentManager.getComponentInfosByType(componentInfoA.getType()))
            .containsExactly(componentInfoA, componentInfoB);
        assertThat(componentManager.getComponentInfo(componentInfoA.getName())).isEqualTo(
            componentInfoA);
        assertThat(componentManager.getComponentInfosByApplicationContext(null)).containsExactly(
            componentInfoA, componentInfoB);
    }

    @Test
    public void unRegister() {
        componentInfoA = new DemoComponent("A");
        componentManager.register(componentInfoA);
        assertThat(componentManager.getComponents()).contains(componentInfoA);
        assertThat(componentManager.getComponentInfosByType(componentInfoA.getType()))
            .containsExactly(componentInfoA);

        componentManager.unregister(componentInfoA);
        assertThat(componentManager.getComponents()).doesNotContain(componentInfoA);
        assertThat(componentManager.getComponentInfosByType(componentInfoA.getType()))
            .doesNotContain(componentInfoA);
    }

    @Test
    public void resolvePendingResolveComponent() {
        componentInfoA = new DemoComponent("A");
        componentManager.register(componentInfoA);
        componentInfoA.unresolve();

        componentInfoA.setActivateException(true);
        componentManager.resolvePendingResolveComponent(componentInfoA.getName());

        componentInfoA.setActivateException(false);
        componentInfoA.activate();
        assertThat(componentInfoA.isHealthy().isHealthy()).isFalse();
        assertThat(componentInfoA.isHealthy().getHealthReport()).contains("activate error");
    }

    @Test
    public void registerDuplicate() {
        componentInfoA = new DemoComponent("A");
        assertThat(componentManager.registerAndGet(componentInfoA)).isEqualTo(componentInfoA);

        componentInfoB = new DemoComponent("A");
        assertThat(componentManager.registerAndGet(componentInfoB)).isEqualTo(componentInfoA);

        componentInfoB.setCanBeDuplicate(false);
        assertThatThrownBy(() -> componentManager.register(componentInfoB)).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-03002");
    }

    @Test
    public void registerException(CapturedOutput capturedOutput) {
        componentInfoA = new DemoComponent("A");
        componentInfoA.setRegisterException(true);

        assertThat(componentManager.registerAndGet(componentInfoA)).isNull();
        assertThat(capturedOutput.getOut()).contains("01-03003");
        assertThat(capturedOutput.getOut()).contains(componentInfoA.getName().toString());
    }

    @Test
    public void resolveException(CapturedOutput capturedOutput) {
        componentInfoA = new DemoComponent("A");
        componentInfoA.setResolveException(true);

        assertThat(componentManager.registerAndGet(componentInfoA)).isEqualTo(componentInfoA);
        assertThat(componentInfoA.isHealthy().isHealthy()).isFalse();
        assertThat(capturedOutput.getOut()).contains("01-03004");
        assertThat(capturedOutput.getOut()).contains(componentInfoA.getName().toString());
    }

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
