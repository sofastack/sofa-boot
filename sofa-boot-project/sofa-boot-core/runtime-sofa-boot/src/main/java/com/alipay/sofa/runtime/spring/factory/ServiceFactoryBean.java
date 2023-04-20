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

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.binding.JvmBindingParam;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;

import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.BEAN_ID;
import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.SOURCE;

/**
 * Implementation of {@link org.springframework.beans.factory.FactoryBean} to register service.
 *
 * @author xuanbei 18/3/1
 */
public class ServiceFactoryBean extends AbstractContractFactoryBean {

    protected Object  ref;

    protected Service service;

    public ServiceFactoryBean() {
    }

    public ServiceFactoryBean(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (!apiType && hasSofaServiceAnnotation()) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00103", beanId, ref.getClass()));
        }

        Implementation implementation = new DefaultImplementation();
        implementation.setTarget(ref);
        service = buildService();

        // default add jvm binding and service jvm binding should set serialize as true
        if (bindings.size() == 0) {
            JvmBindingParam jvmBindingParam = new JvmBindingParam().setSerialize(true);
            bindings.add(new JvmBinding().setJvmBindingParam(jvmBindingParam));
        }

        for (Binding binding : bindings) {
            service.addBinding(binding);
        }

        ComponentInfo componentInfo = new ServiceComponent(implementation, service,
            bindingAdapterFactory, sofaRuntimeContext);
        componentInfo.setApplicationContext(applicationContext);
        ComponentDefinitionInfo definitionInfo = new ComponentDefinitionInfo();
        definitionInfo.setInterfaceMode(apiType ? InterfaceMode.api : InterfaceMode.spring);
        definitionInfo.putInfo(BEAN_ID, beanId);
        Property property = new Property();
        property.setName(SOURCE);
        property.setValue(definitionInfo);
        componentInfo.getProperties().put(SOURCE, property);
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

    protected Service buildService() {
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
