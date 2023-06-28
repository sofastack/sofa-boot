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

import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.SOURCE;

/**
 * Reference register helper.
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceRegisterHelper {

    public static Object registerReference(Reference reference,
                                           BindingAdapterFactory bindingAdapterFactory,
                                           SofaRuntimeContext sofaRuntimeContext) {
        return registerReference(reference, bindingAdapterFactory, sofaRuntimeContext, null, null);

    }

    public static Object registerReference(Reference reference,
                                           BindingAdapterFactory bindingAdapterFactory,
                                           SofaRuntimeContext sofaRuntimeContext,
                                           ApplicationContext applicationContext) {
        return registerReference(reference, bindingAdapterFactory, sofaRuntimeContext,
            applicationContext, null);

    }

    public static Object registerReference(Reference reference,
                                           BindingAdapterFactory bindingAdapterFactory,
                                           SofaRuntimeContext sofaRuntimeContext,
                                           ApplicationContext applicationContext,
                                           ComponentDefinitionInfo definitionInfo) {
        Binding binding = (Binding) reference.getBindings().toArray()[0];

        if (!binding.getBindingType().equals(JvmBinding.JVM_BINDING_TYPE)
            && !sofaRuntimeContext.getProperties().isDisableJvmFirst() && reference.isJvmFirst()) {
            // as rpc invocation would be serialized, so here would Not ignore serialized
            reference.addBinding(new JvmBinding());
        }

        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ReferenceComponent referenceComponent = new ReferenceComponent(reference,
            new DefaultImplementation(), bindingAdapterFactory, sofaRuntimeContext);
        Property property = new Property();
        property.setName(SOURCE);
        property.setValue(definitionInfo);
        referenceComponent.getProperties().put(SOURCE, property);

        if (componentManager.isRegistered(referenceComponent.getName())) {
            return componentManager.getComponentInfo(referenceComponent.getName())
                .getImplementation().getTarget();
        }

        ComponentInfo componentInfo = componentManager.registerAndGet(referenceComponent);
        componentInfo.setApplicationContext(applicationContext);
        return componentInfo.getImplementation().getTarget();

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
