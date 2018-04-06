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
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.impl.BindingFactoryContainer;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;

import java.util.Collection;
import java.util.Map;

/**
 * Service Client Implementation，you can publish a service by this class
 *
 * @author xuanbei 18/3/1
 */
public class ServiceClientImpl implements ServiceClient {
    private SofaRuntimeContext sofaRuntimeContext;

    public ServiceClientImpl(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
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
            BindingConverter bindingConverter = BindingFactoryContainer.getBindingConverterFactory()
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
            sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    @Override
    public void removeService(Class<?> interfaceClass, int millisecondsToDelay) {
        removeService(interfaceClass, "", millisecondsToDelay);
    }

    @Override
    public void removeService(Class<?> interfaceClass, String uniqueId, int millisecondsToDelay) {
        if (millisecondsToDelay < 0) {
            throw new IllegalArgumentException( "Argument delay must be a positive integer or zero.");
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
