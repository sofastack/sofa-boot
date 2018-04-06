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
package com.alipay.sofa.runtime.spring.factory;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;

/**
 * @author xuanbei 18/3/1
 */
public class ServiceFactoryBean extends AbstractContractFactoryBean {
    protected Object  ref;
    protected Service service;

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        if (hasSofaServiceAnnotation()) {
            throw new ServiceRuntimeException(
                "Bean " + beanId + " of type " + ref.getClass()
                    + " has already annotated by @SofaService,"
                    + " can not be registered using xml. Please check it.");
        }

        Implementation implementation = new DefaultImplementation();
        implementation.setTarget(ref);
        service = buildService();

        if (bindings.size() == 0) {
            bindings.add(new JvmBinding());
        }

        for (Binding binding : bindings) {
            service.addBinding(binding);
        }

        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    private boolean hasSofaServiceAnnotation() {
        Class<?> implementationClazz = ref.getClass();
        SofaService sofaService = implementationClazz.getAnnotation(SofaService.class);
        if (sofaService == null) {
            return false;
        }

        String annotationUniqueId = sofaService.uniqueId();
        if ((uniqueId == null || uniqueId.isEmpty())
            && (annotationUniqueId == null || annotationUniqueId.isEmpty())) {
            return true;
        }
        if (annotationUniqueId.equals(uniqueId)) {
            return true;
        }
        return false;
    }

    @Override
    protected void setProperties(BindingConverterContext bindingConverterContext) {
        bindingConverterContext.setBeanId(beanId);
    }

    protected Service buildService() {
        return new ServiceImpl(uniqueId, getInterfaceClass(), InterfaceMode.spring, ref);
    }

    @Override
    public Object getObject() throws Exception {
        return ref;
    }

    @Override
    public Class<?> getObjectType() {
        return ref != null ? ref.getClass() : Service.class;
    }

    @Override
    protected boolean isInBinding() {
        return false;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
