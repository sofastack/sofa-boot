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
package com.alipay.sofa.runtime.ext.client;

import com.alipay.sofa.runtime.api.client.param.ExtensionParam;
import com.alipay.sofa.runtime.api.client.param.ExtensionPointParam;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ExtensionClientImpl}.
 *
 * @author huzijie
 * @version ExtensionClientImplTests.java, v 0.1 2023年04月10日 11:46 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ExtensionClientImplTests {

    @Mock
    private ComponentManager    componentManager;

    @Mock
    private SofaRuntimeContext  sofaRuntimeContext;

    @Mock
    private Element             element;

    @InjectMocks
    private ExtensionClientImpl extensionClient;

    @Test
    void publishExtensionShouldRegisterExtensionComponent() {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        // Arrange
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setElement(element);
        extensionParam.setTargetInstanceName("testInstance");
        extensionParam.setTargetName("testTarget");

        // Act
        extensionClient.publishExtension(extensionParam);

        // Assert
        verify(componentManager).register(Mockito.any(ExtensionComponent.class));
    }

    @Test
    void publishExtensionShouldThrowExceptionWhenExtensionParamIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtension(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("extensionParam can not be null.");
    }

    @Test
    void publishExtensionShouldThrowExceptionWhenContributionElementIsNull() {
        // Arrange
        ExtensionParam extensionParam = new ExtensionParam();

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtension(extensionParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension contribution element can not be null.");
    }

    @Test
    void publishExtensionShouldThrowExceptionWhenTargetInstanceNameIsNull() {
        // Arrange
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setElement(element);

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtension(extensionParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension target instance name can not be null.");
    }

    @Test
    void publishExtensionShouldThrowExceptionWhenTargetNameIsNull() {
        // Arrange
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setElement(element);
        extensionParam.setTargetInstanceName("testInstance");

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtension(extensionParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension target name can not be null.");
    }

    @Test
    void publishExtensionPoint_shouldRegisterExtensionPointComponent() {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        // Arrange
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("testExtensionPoint");
        extensionPointParam.setContributionClass(this.getClass());
        extensionPointParam.setTargetName("testTarget");
        extensionPointParam.setTarget(new Object());

        // Act
        extensionClient.publishExtensionPoint(extensionPointParam);

        // Assert
        verify(componentManager).register(Mockito.any(ExtensionPointComponent.class));
    }

    @Test
    void publishExtensionPointShouldThrowExceptionWhenExtensionPointParamIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtensionPoint(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("extensionPointParam can not be null.");
    }

    @Test
    void publishExtensionPointShouldThrowExceptionWhenNameIsNull() {
        // Arrange
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtensionPoint(extensionPointParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension point name can not be null.");
    }

    @Test
    void publishExtensionPointShouldThrowExceptionWhenContributionClassIsNull() {
        // Arrange
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("testExtensionPoint");

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtensionPoint(extensionPointParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension point contribution can not be null.");

    }

    @Test
    void publishExtensionPointShouldThrowExceptionWhenTargetIsNull() {
        // Arrange
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("testExtensionPoint");
        extensionPointParam.setContributionClass(this.getClass());

        // Act & Assert
        assertThatThrownBy(() -> extensionClient.publishExtensionPoint(extensionPointParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extension point target can not be null.");

    }
}
