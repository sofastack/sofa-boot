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

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.service.api.component.Extensible;
import com.alipay.sofa.service.api.component.Extension;
import com.alipay.sofa.service.api.component.ExtensionPoint;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * SOFA Extension Component.
 *
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionComponent extends AbstractComponent {

    private static final Logger       LOGGER                   = SofaBootLoggerFactory
                                                                   .getLogger(ExtensionComponent.class);

    public static final String        LINK_SYMBOL              = "$";

    public static final ComponentType EXTENSION_COMPONENT_TYPE = new ComponentType("extension");

    private final Extension           extension;

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
        try {
            if (target instanceof Extensible) {
                ((Extensible) target).registerExtension(extension);
            } else {
                Method method = ReflectionUtils.findMethod(target.getClass(), "registerExtension",
                    Extension.class);
                if (method == null) {
                    throw new RuntimeException(ErrorCode.convert("01-01001", target.getClass()
                        .getCanonicalName()));
                }
                ReflectionUtils.invokeMethod(method, target, extension);
            }
        } catch (Throwable t) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-01000",
                extensionPointComponentInfo.getName()), t);
        }

        componentStatus = ComponentStatus.ACTIVATED;
    }

    @Override
    public HealthResult isHealthy() {
        if (sofaRuntimeContext.getProperties().isSkipExtensionHealthCheck()) {
            HealthResult healthResult = new HealthResult(componentName.getRawName());
            healthResult.setHealthy(true);
            return healthResult;
        }

        HealthResult healthResult = new HealthResult(componentName.getRawName());
        //表示 loadContributions 异常的 Extension
        if (e != null) {
            healthResult.setHealthy(false);
            healthResult.setHealthReport("Extension loadContributions error: " + e.getMessage());
            return healthResult;
        }
        //表示注册成功的 Extension
        if (isActivated()) {
            healthResult.setHealthy(true);
            return healthResult;
        }
        //表示对应的 ExtensionPoint 未注册
        if (!isResolved()) {
            healthResult.setHealthy(false);
            healthResult.setHealthReport("Can not find corresponding ExtensionPoint: "
                                         + extension.getTargetComponentName().getName());
            return healthResult;
        } else {
            // 表示 registerExtension 异常的 Extension
            healthResult.setHealthy(false);
            healthResult.setHealthReport("Extension registerExtension error");
            return healthResult;
        }
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
                if (sofaRuntimeContext.getProperties().isExtensionFailureInsulating()) {
                    this.e = e;
                }
                LOGGER.error(
                    ErrorCode.convert("01-01002", extensionPoint.getName(),
                        extension.getComponentName()), e);
            }
        }
    }
}
