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
    private Object  ref;
    private Service service;

    public ServiceFactoryBean() {
    }

    public ServiceFactoryBean(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    protected void doAfterPropertiesSet() {
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
            bindingAdapterFactory, sofaRuntimeContext);
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
        return annotationUniqueId.equals(uniqueId);
    }

    @Override
    protected void setProperties(BindingConverterContext bindingConverterContext) {
        bindingConverterContext.setBeanId(beanId);
    }

    private Service buildService() {
        return new ServiceImpl(uniqueId, getInterfaceClass(), InterfaceMode.spring, ref);
    }

    @Override
    public Object getObject() throws Exception {
        return service;
    }

    @Override
    public Class<?> getObjectType() {
        return service != null ? service.getClass() : Service.class;
    }

    @Override
    protected boolean isInBinding() {
        return false;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
