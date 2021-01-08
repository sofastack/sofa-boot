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
package com.alipay.sofa.runtime.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import com.alipay.sofa.runtime.api.ReferenceRegisterHook;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * Reference register helper.
 * Before referencing, invoke <code>ReferenceRegisterHook.before</code> implementations via JAVA SPI.
 * After referencing, invoke <code>ReferenceRegisterHook.after</code> implementations via JAVA SPI.
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceRegisterHelper {
    private final static List<ReferenceRegisterHook> referenceRegisterHooks = new ArrayList<>();

    static {
        ServiceLoader<ReferenceRegisterHook> serviceLoader = ServiceLoader.load(ReferenceRegisterHook.class);
        for (ReferenceRegisterHook referenceRegisterHook: serviceLoader) {
            referenceRegisterHooks.add(referenceRegisterHook);
        }

        // Sort in ascending order
        referenceRegisterHooks.sort((o1, o2) -> {
            return Integer.compare(o1.order(), o2.order());
        });
    }

    public static Object registerReference(Reference reference,
                                           BindingAdapterFactory bindingAdapterFactory,
                                           SofaRuntimeContext sofaRuntimeContext) {
        // Invoke reference registering before hook
        for (ReferenceRegisterHook referenceRegisterHook: referenceRegisterHooks) {
            referenceRegisterHook.before(reference, sofaRuntimeContext);
        }

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ReferenceComponent referenceComponent = new ReferenceComponent(reference,
            new DefaultImplementation(), bindingAdapterFactory, sofaRuntimeContext);

        if (componentManager.isRegistered(referenceComponent.getName())) {
            return componentManager.getComponentInfo(referenceComponent.getName())
                .getImplementation().getTarget();
        }

        ComponentInfo componentInfo = componentManager.registerAndGet(referenceComponent);
        Object rtn = componentInfo.getImplementation().getTarget();

        // Invoke reference registering after hook
        for (ReferenceRegisterHook referenceRegisterHook: referenceRegisterHooks) {
            referenceRegisterHook.after(rtn);
        }
        return rtn;
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
