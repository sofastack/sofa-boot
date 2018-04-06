/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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
import com.alipay.sofa.runtime.service.impl.BindingFactoryContainer;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

import java.util.Collection;

/**
 * Reference Client Implementation，you can reference a service by this class
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceClientImpl implements ReferenceClient {
    private SofaRuntimeContext sofaRuntimeContext;

    public ReferenceClientImpl(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    private <T> Reference getReferenceFromReferenceParam(ReferenceParam<T> referenceParam) {
        BindingParam bindingParam = referenceParam.getBindingParam();

        Reference reference = new ReferenceImpl(referenceParam.getUniqueId(),
            referenceParam.getInterfaceType(), InterfaceMode.api, referenceParam.isLocalFirst(),
            referenceParam.isJvmService(), null);

        if (bindingParam == null) {
            // add JVM Binding Default
            reference.addBinding(new JvmBinding());
        } else {
            BindingConverter bindingConverter = BindingFactoryContainer.getBindingConverterFactory()
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
        T result = (T) ReferenceRegisterHelper.registerReference(
            getReferenceFromReferenceParam(referenceParam), sofaRuntimeContext);

        return result;
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
