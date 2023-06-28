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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.service.api.component.ExtensionPoint;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ExtensionPointComponent}.
 *
 * @author huzijie
 * @version ExtensionPointComponentTests.java, v 0.1 2023年04月10日 3:24 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ExtensionPointComponentTests {

    @Mock
    private ExtensionPoint          extensionPoint;

    @Mock
    private ComponentManager        componentManager;

    @Mock
    private SofaRuntimeContext      sofaRuntimeContext;

    @Mock
    private Implementation          implementation;

    private ExtensionPointComponent component;

    @BeforeEach
    public void setUp() {
        component = new ExtensionPointComponent(extensionPoint, sofaRuntimeContext, implementation);
    }

    @Test
    void testActivate() throws ServiceRuntimeException {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        List<ComponentInfo> componentInfoList = new ArrayList<>();
        when(componentManager.getComponents()).thenReturn(componentInfoList);
        component.register();
        component.resolve();
        component.activate();
        assertThat(component.getState()).isEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void testDeactivate() throws ServiceRuntimeException {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        List<ComponentInfo> componentInfoList = new ArrayList<>();
        when(componentManager.getComponents()).thenReturn(componentInfoList);
        component.register();
        component.resolve();
        component.activate();
        component.deactivate();
        assertThat(component.getState()).isEqualTo(ComponentStatus.RESOLVED);
    }

    @Test
    void testGetType() {
        assertThat(component.getType()).isEqualTo(
            ExtensionPointComponent.EXTENSION_POINT_COMPONENT_TYPE);
    }

    @Test
    void getPropertiesShouldReturnEmptyMap() {
        assertThat(component.getProperties()).hasSize(0);
    }

    @Test
    void testGetExtensionPoint() {
        assertThat(component.getExtensionPoint()).isEqualTo(extensionPoint);
    }
}
