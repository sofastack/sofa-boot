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
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import java.util.Collection;
import java.util.Map;

/**
 * Service Client Implementation，you can publish a service by this class
 *
 * @author xuanbei 18/3/1
 */
public class ServiceClientImpl implements ServiceClient {
    private SofaRuntimeContext      sofaRuntimeContext;
    private BindingConverterFactory bindingConverterFactory;
    private BindingAdapterFactory   bindingAdapterFactory;

    public ServiceClientImpl(SofaRuntimeContext sofaRuntimeContext,
                             BindingConverterFactory bindingConverterFactory,
                             BindingAdapterFactory bindingAdapterFactory) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingConverterFactory = bindingConverterFactory;
        this.bindingAdapterFactory = bindingAdapterFactory;
    }

    @SuppressWarnings("unchecked")
    public void service(ServiceParam serviceParam) {
        Implementation implementation = new DefaultImplementation();
        implementation.setTarget(serviceParam.getInstance());

        if (serviceParam.getInterfaceType() == null) {
            throw new ServiceRuntimeException(
                "Interface type is null. Interface type is required while publish a service.");
        }
        Service service = new ServiceImpl(serviceParam.getUniqueId(),
            serviceParam.getInterfaceType(), InterfaceMode.api, serviceParam.getInstance(), null);

        for (BindingParam bindingParam : serviceParam.getBindingParams()) {
            BindingConverter bindingConverter = bindingConverterFactory
                .getBindingConverter(bindingParam.getBindingType());

            if (bindingConverter == null) {
                throw new ServiceRuntimeException(
                    "Can not found binding converter for binding type "
                            + bindingParam.getBindingType());
            }
            BindingConverterContext bindingConverterContext = new BindingConverterContext();
            bindingConverterContext.setInBinding(false);
            bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
            bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
            Binding binding = bindingConverter.convert(bindingParam, bindingConverterContext);
            service.addBinding(binding);
        }

        boolean hasJvmBinding = false;
        for (Binding binding : service.getBindings()) {
            if (binding.getBindingType().equals(JvmBinding.JVM_BINDING_TYPE)) {
                hasJvmBinding = true;
                break;
            }
        }

        if (!hasJvmBinding) {
            service.addBinding(new JvmBinding());
        }

        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            bindingAdapterFactory, sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    @Override
    public void removeService(Class<?> interfaceClass, int millisecondsToDelay) {
        removeService(interfaceClass, "", millisecondsToDelay);
    }

    @Override
    public void removeService(Class<?> interfaceClass, String uniqueId, int millisecondsToDelay) {
        if (millisecondsToDelay < 0) {
            throw new IllegalArgumentException("Argument delay must be a positive integer or zero.");
        }

        Collection<ComponentInfo> serviceComponents = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);

        for (ComponentInfo componentInfo : serviceComponents) {
            if (!(componentInfo instanceof ServiceComponent)) {
                continue;
            }

            ServiceComponent serviceComponent = (ServiceComponent) componentInfo;

            if (serviceComponent.getService().getInterfaceType() == interfaceClass
                && serviceComponent.getService().getUniqueId().equals(uniqueId)) {
                Map<String, Property> properties = serviceComponent.getProperties();
                Property property = new Property();
                property.setValue(millisecondsToDelay);
                properties.put(ServiceComponent.UNREGISTER_DELAY_MILLISECONDS, property);

                sofaRuntimeContext.getComponentManager().unregister(serviceComponent);
            }
        }
    }
}
