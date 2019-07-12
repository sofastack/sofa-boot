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

import java.util.Map;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.service.api.component.ExtensionPoint;

/**
 * SOFA ExtensionPoint Component
 *
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionPointComponent extends AbstractComponent {
    private static final String       LINK_SYMBOL                    = "$";
    public static final ComponentType EXTENSION_POINT_COMPONENT_TYPE = new ComponentType(
                                                                         "extension-point");
    // Extension
    private ExtensionPoint            extensionPoint;

    public ExtensionPointComponent(ExtensionPoint extensionPoint,
                                   SofaRuntimeContext sofaRuntimeContext,
                                   Implementation implementation) {
        this.extensionPoint = extensionPoint;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.implementation = implementation;
        this.componentName = ComponentNameFactory.createComponentName(
            EXTENSION_POINT_COMPONENT_TYPE, implementation.getName() + LINK_SYMBOL
                                            + this.extensionPoint.getName());
    }

    @Override
    public ComponentType getType() {
        return EXTENSION_POINT_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public void activate() throws ServiceRuntimeException {
        super.activate();

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();

        for (ComponentInfo componentInfo : componentManager.getComponents()) {
            if (componentInfo.getType().equals(ExtensionComponent.EXTENSION_COMPONENT_TYPE)
                && !componentInfo.isResolved()) {
                ExtensionComponent extensionComponent = (ExtensionComponent) componentInfo;
                if (extensionComponent.getExtension().getTargetComponentName()
                    .equals(componentName)) {
                    componentManager.resolvePendingResolveComponent(componentInfo.getName());
                }
            }
        }
    }

    @Override
    public void deactivate() throws ServiceRuntimeException {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();

        for (ComponentInfo componentInfo : componentManager.getComponents()) {
            if (componentInfo.getType().equals(ExtensionComponent.EXTENSION_COMPONENT_TYPE)) {
                ExtensionComponent extensionComponent = (ExtensionComponent) componentInfo;
                if (extensionComponent.getExtension().getTargetComponentName()
                    .equals(componentName)) {
                    componentManager.unregister(componentInfo);
                }
            }
        }
        super.deactivate();
    }

    public ExtensionPoint getExtensionPoint() {
        return extensionPoint;
    }
}
