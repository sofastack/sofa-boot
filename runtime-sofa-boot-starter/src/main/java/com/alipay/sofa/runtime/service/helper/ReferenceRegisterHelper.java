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
package com.alipay.sofa.runtime.service.helper;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.constants.SofaConfigurationConstants;

import java.util.Collection;

/**
 * reference register helper
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceRegisterHelper {

    public static Object registerReference(Reference reference,
                                           SofaRuntimeContext sofaRuntimeContext) {
        Binding binding = (Binding) reference.getBindings().toArray()[0];

        if (reference.jvmService() && binding.getBindingType().equals(JvmBinding.JVM_BINDING_TYPE)) {
            throw new ServiceRuntimeException(
                "jvm-service=\"true\" can not be used with JVM binding.");
        }

        if (!binding.getBindingType().equals(JvmBinding.JVM_BINDING_TYPE)
            && isLocalFirst(reference, sofaRuntimeContext)) {
            reference.addBinding(new JvmBinding());
        }

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ReferenceComponent referenceComponent = new ReferenceComponent(reference,
            new DefaultImplementation(), sofaRuntimeContext);

        if (componentManager.isRegistered(referenceComponent.getName())) {
            return componentManager.getComponentInfo(referenceComponent.getName())
                .getImplementation().getTarget();
        }

        ComponentInfo componentInfo = componentManager.registerAndGet(referenceComponent);
        return componentInfo.getImplementation().getTarget();

    }

    private static boolean isLocalFirst(Reference reference, SofaRuntimeContext sofaRuntimeContext) {
        String localFirstDisabled = sofaRuntimeContext.getAppConfiguration().getPropertyValue(
            SofaConfigurationConstants.SOFA_RUNTIME_DISABLE_LOCAL_FIRST);
        if (localFirstDisabled != null && localFirstDisabled.equalsIgnoreCase("true")) {
            return false;
        }
        return reference.isLocalFirst();
    }

    public static int generateBindingHashCode(Reference reference) {
        Collection<Binding> bindings = reference.getBindings();
        int result = 1;
        for (Binding binding : bindings) {
            result = result * 31 + binding.getBindingHashCode();
        }

        ClassLoader cl = reference.getInterfaceType().getClassLoader();

        if (cl != null) {
            result += reference.getInterfaceType().getClassLoader().hashCode();
        }

        return result;
    }
}
