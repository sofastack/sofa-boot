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
package com.alipay.sofa.runtime.service.client;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

import java.util.Collection;

/**
 * Reference Client Implementation，you can reference a service by this class
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceClientImpl implements ReferenceClient {
    private SofaRuntimeContext      sofaRuntimeContext;
    private BindingConverterFactory bindingConverterFactory;
    private BindingAdapterFactory   bindingAdapterFactory;

    public ReferenceClientImpl(SofaRuntimeContext sofaRuntimeContext,
                               BindingConverterFactory bindingConverterFactory,
                               BindingAdapterFactory bindingAdapterFactory) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingConverterFactory = bindingConverterFactory;
        this.bindingAdapterFactory = bindingAdapterFactory;
    }

    @SuppressWarnings("unchecked")
    private <T> Reference getReferenceFromReferenceParam(ReferenceParam<T> referenceParam) {
        BindingParam bindingParam = referenceParam.getBindingParam();
        Reference reference = new ReferenceImpl(referenceParam.getUniqueId(),
            referenceParam.getInterfaceType(), InterfaceMode.api, referenceParam.isJvmFirst(), null);

        if (bindingParam == null) {
            // add JVM Binding Default
            reference.addBinding(new JvmBinding());
        } else {
            BindingConverter bindingConverter = bindingConverterFactory
                .getBindingConverter(bindingParam.getBindingType());
            if (bindingConverter == null) {
                throw new ServiceRuntimeException(
                    "Can not found binding converter for binding type "
                            + bindingParam.getBindingType());
            }
            BindingConverterContext bindingConverterContext = new BindingConverterContext();
            bindingConverterContext.setInBinding(true);
            bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
            bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
            Binding binding = bindingConverter.convert(bindingParam, bindingConverterContext);
            reference.addBinding(binding);
        }

        return reference;
    }

    @SuppressWarnings("unchecked")
    public <T> T reference(ReferenceParam<T> referenceParam) {

        return (T) ReferenceRegisterHelper.registerReference(
            getReferenceFromReferenceParam(referenceParam), bindingAdapterFactory,
            sofaRuntimeContext);
    }

    @Override
    public <T> void removeReference(ReferenceParam<T> referenceParam) {
        Reference reference = getReferenceFromReferenceParam(referenceParam);
        ComponentName referenceComponentName = ComponentNameFactory.createComponentName(
            ReferenceComponent.REFERENCE_COMPONENT_TYPE,
            reference.getInterfaceType(),
            reference.getUniqueId() + "#"
                    + ReferenceRegisterHelper.generateBindingHashCode(reference));
        Collection<ComponentInfo> referenceComponents = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);

        for (ComponentInfo referenceComponent : referenceComponents) {
            if (referenceComponent.getName().equals(referenceComponentName)) {
                sofaRuntimeContext.getComponentManager().unregister(referenceComponent);
            }
        }
    }

    @Override
    public void removeReference(Class<?> interfaceClass) {
        removeReference(interfaceClass, "");
    }

    @Override
    public void removeReference(Class<?> interfaceClass, String uniqueId) {
        Collection<ComponentInfo> referenceComponents = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        for (ComponentInfo componentInfo : referenceComponents) {
            if (!(componentInfo instanceof ReferenceComponent)) {
                continue;
            }
            ReferenceComponent referenceComponent = (ReferenceComponent) componentInfo;
            if (referenceComponent.getReference().getInterfaceType() == interfaceClass
                && referenceComponent.getReference().getUniqueId().equals(uniqueId)) {
                sofaRuntimeContext.getComponentManager().unregister(referenceComponent);
            }
        }
    }
}
