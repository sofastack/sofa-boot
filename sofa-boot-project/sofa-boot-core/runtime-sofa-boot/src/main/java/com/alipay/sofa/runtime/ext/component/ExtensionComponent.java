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

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.service.api.component.Extensible;
import com.alipay.sofa.service.api.component.Extension;
import com.alipay.sofa.service.api.component.ExtensionPoint;

/**
 * SOFA Extension Component
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionComponent extends AbstractComponent {
    public static final String        LINK_SYMBOL              = "$";
    public static final ComponentType EXTENSION_COMPONENT_TYPE = new ComponentType("extension");

    private Extension                 extension;

    public ExtensionComponent(Extension extension, SofaRuntimeContext sofaRuntimeContext) {
        this.extension = extension;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.componentName = ComponentNameFactory.createComponentName(
            EXTENSION_COMPONENT_TYPE,
            extension.getTargetComponentName().getName() + LINK_SYMBOL
                    + ObjectUtils.getIdentityHexString(extension));
    }

    @Override
    public ComponentType getType() {
        return EXTENSION_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public boolean resolve() {
        if (componentStatus != ComponentStatus.REGISTERED) {
            return false;
        }

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ComponentName extensionPointComponentName = extension.getTargetComponentName();

        ComponentInfo extensionPointComponentInfo = componentManager
            .getComponentInfo(extensionPointComponentName);

        if (extensionPointComponentInfo != null && extensionPointComponentInfo.isActivated()) {
            componentStatus = ComponentStatus.RESOLVED;
            return true;
        }

        return false;
    }

    @Override
    public void activate() throws ServiceRuntimeException {
        if (componentStatus != ComponentStatus.RESOLVED) {
            return;
        }

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ComponentName extensionPointComponentName = extension.getTargetComponentName();
        ComponentInfo extensionPointComponentInfo = componentManager
            .getComponentInfo(extensionPointComponentName);

        if (extensionPointComponentInfo == null || !extensionPointComponentInfo.isActivated()) {
            return;
        }

        loadContributions(
            ((ExtensionPointComponent) extensionPointComponentInfo).getExtensionPoint(), extension);

        Object target = extensionPointComponentInfo.getImplementation().getTarget();
        if (target instanceof Extensible) {
            try {
                ((Extensible) target).registerExtension(extension);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        } else {
            Method method = ReflectionUtils.findMethod(target.getClass(), "registerExtension",
                Extension.class);
            ReflectionUtils.invokeMethod(method, target, extension);
        }

        componentStatus = ComponentStatus.ACTIVATED;
    }

    @Override
    public HealthResult isHealthy() {
        HealthResult healthResult = new HealthResult(componentName.getRawName());
        healthResult.setHealthy(true);
        return healthResult;
    }

    public Extension getExtension() {
        return extension;
    }

    private void loadContributions(ExtensionPoint extensionPoint, Extension extension) {
        if (extensionPoint != null && extensionPoint.hasContribution()) {
            try {
                Object[] contribs = ((ExtensionPointInternal) extensionPoint)
                    .loadContributions((ExtensionInternal) extension);
                ((ExtensionInternal) extension).setContributions(contribs);
            } catch (Exception e) {
                SofaLogger.error(e, "Failed to create contribution objects");
            }
        }
    }
}
