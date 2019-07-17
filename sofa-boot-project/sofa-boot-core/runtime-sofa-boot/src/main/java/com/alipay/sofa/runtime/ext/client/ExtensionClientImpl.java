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

import org.springframework.util.Assert;

import com.alipay.sofa.runtime.api.client.ExtensionClient;
import com.alipay.sofa.runtime.api.client.param.ExtensionParam;
import com.alipay.sofa.runtime.api.client.param.ExtensionPointParam;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionImpl;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionPointImpl;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.service.api.component.ExtensionPoint;

/**
 * Programming API Implement for Extension/Extension-Point
 *
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionClientImpl implements ExtensionClient {

    private SofaRuntimeContext sofaRuntimeContext;

    public ExtensionClientImpl(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public void publishExtension(ExtensionParam extensionParam) {
        Assert.notNull(extensionParam, "extensionParam can not be null.");
        Assert.notNull(extensionParam.getElement(),
            "Extension contribution element can not be null.");
        Assert.notNull(extensionParam.getTargetInstanceName(),
            "Extension target instance name can not be null.");
        Assert.notNull(extensionParam.getTargetName(), "Extension target name can not be null.");

        ExtensionImpl extension = new ExtensionImpl(null, extensionParam.getTargetName(),
            extensionParam.getElement(), sofaRuntimeContext.getAppClassLoader());
        extension.setTargetComponentName(ComponentNameFactory.createComponentName(
            ExtensionPointComponent.EXTENSION_POINT_COMPONENT_TYPE,
            extensionParam.getTargetInstanceName() + ExtensionComponent.LINK_SYMBOL
                    + extensionParam.getTargetName()));
        ComponentInfo extensionComponent = new ExtensionComponent(extension, sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(extensionComponent);
    }

    @Override
    public void publishExtensionPoint(ExtensionPointParam extensionPointParam) {
        Assert.notNull(extensionPointParam, "extensionPointParam can not be null.");
        Assert.notNull(extensionPointParam.getName(), "Extension point name can not be null.");
        Assert.notNull(extensionPointParam.getContributionClass(),
            "Extension point contribution can not be null.");
        Assert.notNull(extensionPointParam.getTarget(), "Extension point target can not be null.");

        ExtensionPoint extensionPoint = new ExtensionPointImpl(extensionPointParam.getName(),
            extensionPointParam.getContributionClass());
        Implementation implementation = new DefaultImplementation(
            extensionPointParam.getTargetName());
        implementation.setTarget(extensionPointParam.getTarget());
        ComponentInfo extensionPointComponent = new ExtensionPointComponent(extensionPoint,
            sofaRuntimeContext, implementation);
        sofaRuntimeContext.getComponentManager().register(extensionPointComponent);
    }
}
