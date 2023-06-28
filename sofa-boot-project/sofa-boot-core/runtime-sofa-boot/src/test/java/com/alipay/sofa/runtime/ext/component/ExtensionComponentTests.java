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
package com.alipay.sofa.runtime.ext.component;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.service.api.component.Extensible;
import com.alipay.sofa.service.api.component.Extension;
import com.alipay.sofa.service.api.component.ExtensionPoint;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ExtensionComponent}.
 *
 * @author huzijie
 * @version ExtensionComponentTests.java, v 0.1 2023年04月10日 2:20 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ExtensionComponentTests {

    private final ComponentName     extensionComponentName = new ComponentName(new ComponentType(
                                                               "ext"), "ext");

    @Mock
    private SofaRuntimeContext      sofaRuntimeContext;

    @Mock
    private ComponentManager        componentManager;

    @Mock
    private ExtensionInternal       extension;

    @Mock
    private ExtensionPointComponent componentInfo;

    @Mock
    private Implementation          implementation;

    private ExtensionComponent      extensionComponent;

    @BeforeEach
    public void setUp() {
        when(extension.getTargetComponentName()).thenReturn(extensionComponentName);
        extensionComponent = new ExtensionComponent(extension, sofaRuntimeContext);
    }

    @Test
    void getTypeShouldReturnExtensionComponentType() {
        assertThat(extensionComponent.getType()).isEqualTo(
            ExtensionComponent.EXTENSION_COMPONENT_TYPE);
    }

    @Test
    void getPropertiesShouldReturnEmptyMap() {
        assertThat(extensionComponent.getProperties()).hasSize(0);
    }

    @Test
    void resolveExtensionComponent() {
        boolean result = extensionComponent.resolve();
        assertThat(result).isFalse();

        extensionComponent.register();

        ComponentInfo componentInfo = mock(ComponentInfo.class);
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(componentManager.getComponentInfo(extensionComponentName)).thenReturn(componentInfo);
        when(componentInfo.isActivated()).thenReturn(false);

        result = extensionComponent.resolve();
        assertThat(result).isFalse();

        when(componentInfo.isActivated()).thenReturn(true);
        result = extensionComponent.resolve();
        assertThat(result).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.RESOLVED);
    }

    @Test
    void unActiveExtensionComponentWhenNotRegister() {
        extensionComponent.activate();
        assertThat(extensionComponent.getState()).isNotEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void activeExtensionComponentWithExtensibleInterface() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        extensionComponent.activate();
        assertThat(mockExtensibleClass.isRegister()).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void activeExtensionComponentLoadContributionsSuccess() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        ExtensionPointInternal extensionPoint = mock(ExtensionPointInternal.class);
        when(componentInfo.getExtensionPoint()).thenReturn(extensionPoint);
        when(extensionPoint.hasContribution()).thenReturn(true);

        extensionComponent.activate();
        assertThat(mockExtensibleClass.isRegister()).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void activeExtensionComponentLoadContributionsException() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        ExtensionPoint extensionPoint = mock(ExtensionPoint.class);
        when(componentInfo.getExtensionPoint()).thenReturn(extensionPoint);
        when(extensionPoint.hasContribution()).thenReturn(true);
        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isExtensionFailureInsulating()).thenReturn(false);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);

        extensionComponent.activate();
        assertThat(mockExtensibleClass.isRegister()).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
        assertThat(extensionComponent.isHealthy().isHealthy()).isTrue();
    }

    @Test
    void activeExtensionComponentLoadContributionsThrowException() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        ExtensionPoint extensionPoint = mock(ExtensionPoint.class);
        when(componentInfo.getExtensionPoint()).thenReturn(extensionPoint);
        when(extensionPoint.hasContribution()).thenReturn(true);
        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isExtensionFailureInsulating()).thenReturn(true);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);

        extensionComponent.activate();
        assertThat(mockExtensibleClass.isRegister()).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
        assertThat(extensionComponent.isHealthy().isHealthy()).isFalse();
    }

    @Test
    void activeExtensionComponentWithNormalExtensibleClass() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleNormalClass mockExtensibleClass = new MockExtensibleNormalClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        extensionComponent.activate();
        assertThat(mockExtensibleClass.isRegister()).isTrue();
        assertThat(extensionComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void activeExtensionComponentWithNormalClassNoExtensibleMethod() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        NoExtensibleNormalClass mockExtensibleClass = new NoExtensibleNormalClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        assertThatThrownBy(() -> extensionComponent.activate()).isInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("SOFA-BOOT-01-01001: Failed to find method 'registerExtension' in class [com.alipay.sofa.runtime.ext.component.ExtensionComponentTests.NoExtensibleNormalClass]");
        assertThat(extensionComponent.getState()).isNotEqualTo(ComponentStatus.ACTIVATED);

    }

    @Test
    void activeExtensionComponentWithExtensionException() {
        registerAndResolve();

        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        mockExtensibleClass.setThrowException(true);
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);

        assertThatThrownBy(() -> extensionComponent.activate()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("01-01000")
                .hasRootCauseMessage("extension fail");
        assertThat(extensionComponent.getState()).isNotEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void isHealthyWhenSkipCheck() {
        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(true);

        HealthResult healthResult = extensionComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
    }

    @Test
    void isHealthyWhenException() {
        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);
        extensionComponent.exception(new RuntimeException("fail"));

        HealthResult healthResult = extensionComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).contains(
            "Extension loadContributions error: fail");
    }

    @Test
    void isHealthyWhenActive() {
        registerAndResolve();
        when(componentInfo.getImplementation()).thenReturn(implementation);
        MockExtensibleClass mockExtensibleClass = new MockExtensibleClass();
        when(implementation.getTarget()).thenReturn(mockExtensibleClass);
        extensionComponent.activate();

        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);

        HealthResult healthResult = extensionComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
    }

    @Test
    void isHealthyWhenUnResolve() {
        extensionComponent.activate();

        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);

        HealthResult healthResult = extensionComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).contains(
            "Can not find corresponding ExtensionPoint");
    }

    @Test
    void isHealthyWhenRegisterExtensionException() {
        registerAndResolve();

        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipExtensionHealthCheck()).thenReturn(false);

        HealthResult healthResult = extensionComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).contains("Extension registerExtension error");
    }

    private void registerAndResolve() {
        extensionComponent.register();
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(componentManager.getComponentInfo(extensionComponentName)).thenReturn(componentInfo);
        when(componentInfo.isActivated()).thenReturn(true);
        extensionComponent.resolve();
    }

    static class MockExtensibleClass implements Extensible {

        public boolean throwException = false;

        public boolean register       = false;

        @Override
        public void registerExtension(Extension extension) throws Exception {
            if (throwException) {
                throw new RuntimeException("extension fail");
            }
            register = true;
        }

        @Override
        public void unregisterExtension(Extension extension) throws Exception {

        }

        public boolean isRegister() {
            return register;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }
    }

    public static class MockExtensibleNormalClass {

        public boolean register = false;

        public void registerExtension(Extension extension) {
            register = true;
        }

        public boolean isRegister() {
            return register;
        }
    }

    public static class NoExtensibleNormalClass {

    }
}
