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
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version SofaBootComponentsEndpointTest.java, v 0.1 2022年03月17日 4:15 PM huzijie Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class ComponentsEndpointTest {

    @Mock
    private SofaRuntimeContext sofaRuntimeContext;

    @Mock
    private ComponentManager   componentManager;

    @Mock
    private ServiceComponent   serviceComponent;

    @Mock
    private ExtensionComponent extensionComponent;

    @InjectMocks
    private ComponentsEndPoint sofaBootComponentsEndPoint;

    @Test
    public void testComponents() {
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

        ComponentsEndPoint.ApplicationComponents applicationComponents = sofaBootComponentsEndPoint
            .components();
        Assert.assertNotNull(applicationComponents);
        Map<String, Collection<ComponentsEndPoint.ComponentDisplayInfo>> componentTypeCollectionMap = applicationComponents
            .getComponentsInfoMap();
        Assert.assertNotNull(componentTypeCollectionMap);
        Assert.assertEquals(2, componentTypeCollectionMap.size());

        Collection<ComponentsEndPoint.ComponentDisplayInfo> serviceComponentCollection = componentTypeCollectionMap
            .get(ServiceComponent.SERVICE_COMPONENT_TYPE.getName());
        Assert.assertEquals(1, serviceComponents.size());
        Assert.assertTrue(serviceComponentCollection instanceof List);
        Assert.assertEquals("testSofaService",
            ((List<ComponentsEndPoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getName());
        Assert.assertEquals("moduleA",
            ((List<ComponentsEndPoint.ComponentDisplayInfo>) serviceComponentCollection).get(0)
                .getApplicationId());

        Collection<ComponentsEndPoint.ComponentDisplayInfo> extComponentCollection = componentTypeCollectionMap
            .get(ExtensionComponent.EXTENSION_COMPONENT_TYPE.getName());
        Assert.assertEquals(1, extComponentCollection.size());
        Assert.assertTrue(extComponentCollection instanceof List);
        Assert.assertEquals("testSofaExt",
            ((List<ComponentsEndPoint.ComponentDisplayInfo>) extComponentCollection).get(0)
                .getName());
        Assert.assertEquals("moduleB",
            ((List<ComponentsEndPoint.ComponentDisplayInfo>) extComponentCollection).get(0)
                .getApplicationId());
    }

}
