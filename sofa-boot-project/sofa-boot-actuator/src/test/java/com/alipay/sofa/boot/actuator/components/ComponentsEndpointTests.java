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
package com.alipay.sofa.boot.actuator.components;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ComponentsEndpoint}.
 *
 * @author huzijie
 * @version ComponentsEndpointTests.java, v 0.1 2022年03月17日 4:15 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ComponentsEndpointTests {

    @Mock
    private SofaRuntimeContext sofaRuntimeContext;

    @Mock
    private ComponentManager   componentManager;

    @Mock
    private ServiceComponent   serviceComponent;

    @Mock
    private ExtensionComponent extensionComponent;

    @InjectMocks
    private ComponentsEndpoint sofaBootComponentsEndPoint;

    @Test
    public void components() {
        Mockito.doReturn(componentManager).when(sofaRuntimeContext).getComponentManager();

        List<ComponentType> componentTypeList = new ArrayList<>();
        componentTypeList.add(ServiceComponent.SERVICE_COMPONENT_TYPE);
        componentTypeList.add(ExtensionComponent.EXTENSION_COMPONENT_TYPE);
        Mockito.doReturn(componentTypeList).when(componentManager).getComponentTypes();

        List<ServiceComponent> serviceComponents = new ArrayList<>();
        serviceComponents.add(serviceComponent);
        Mockito
            .doReturn(new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE, "testSofaService"))
            .when(serviceComponent).getName();
        GenericApplicationContext contextA = new GenericApplicationContext();
        contextA.setId("moduleA");
        Mockito.doReturn(contextA).when(serviceComponent).getApplicationContext();
        Mockito.doReturn(serviceComponents).when(componentManager)
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        Property propertyA = new Property();
        propertyA.setName("serviceA");
        propertyA.setValue("valueA");
        Map<String, Property> servicePropertyMap = Map.of("A", propertyA);
        Mockito.doReturn(servicePropertyMap).when(serviceComponent).getProperties();

        List<ExtensionComponent> extensionComponents = new ArrayList<>();
        extensionComponents.add(extensionComponent);
        Mockito
            .doReturn(new ComponentName(ExtensionComponent.EXTENSION_COMPONENT_TYPE, "testSofaExt"))
            .when(extensionComponent).getName();
        GenericApplicationContext contextB = new GenericApplicationContext();
        contextB.setId("moduleB");
        Mockito.doReturn(contextB).when(extensionComponent).getApplicationContext();
        Mockito.doReturn(extensionComponents).when(componentManager)
            .getComponentInfosByType(ExtensionComponent.EXTENSION_COMPONENT_TYPE);

        ComponentsEndpoint.ComponentsDescriptor applicationComponents = sofaBootComponentsEndPoint
            .components();
        assertThat(applicationComponents).isNotNull();
        Map<String, Collection<ComponentsEndpoint.ComponentDisplayInfo>> componentTypeCollectionMap = applicationComponents
            .getComponentsInfoMap();
        assertThat(componentTypeCollectionMap).isNotNull();
        assertThat(componentTypeCollectionMap.size()).isEqualTo(2);

        Collection<ComponentsEndpoint.ComponentDisplayInfo> serviceComponentCollection = componentTypeCollectionMap
            .get(ServiceComponent.SERVICE_COMPONENT_TYPE.getName());
        assertThat(serviceComponents.size()).isEqualTo(1);
        assertThat(serviceComponentCollection).isInstanceOf(List.class);
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getName()).isEqualTo("testSofaService");
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getApplicationId()).isEqualTo("moduleA");
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getName()).isEqualTo("testSofaService");
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getProperties().get(0).getName()).isEqualTo("serviceA");
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getProperties().get(0).getValue()).isEqualTo("valueA");

        Collection<ComponentsEndpoint.ComponentDisplayInfo> extComponentCollection = componentTypeCollectionMap
            .get(ExtensionComponent.EXTENSION_COMPONENT_TYPE.getName());
        assertThat(extComponentCollection.size()).isEqualTo(1);
        assertThat(extComponentCollection).isInstanceOf(List.class);
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) extComponentCollection).get(0)
                .getName()).isEqualTo("testSofaExt");
        assertThat(
            ((List<ComponentsEndpoint.ComponentDisplayInfo>) extComponentCollection).get(0)
                .getApplicationId()).isEqualTo("moduleB");
    }

}
