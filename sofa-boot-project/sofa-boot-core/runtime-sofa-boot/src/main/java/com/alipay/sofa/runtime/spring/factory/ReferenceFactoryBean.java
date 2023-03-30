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

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.binding.JvmBindingParam;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import org.springframework.util.Assert;

import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.BEAN_ID;

/**
 * Implementation of {@link org.springframework.beans.factory.FactoryBean} to register reference.
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceFactoryBean extends AbstractContractFactoryBean {

    protected Object  proxy;
    /**
     * jvm first or not
     */
    protected boolean jvmFirst = true;
    /**
     * load balance
     **/
    protected String  loadBalance;

    protected boolean required = true;

    public ReferenceFactoryBean() {
    }

    public ReferenceFactoryBean(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Reference reference = buildReference();
        Assert
            .isTrue(bindings.size() <= 1,
                "Found more than one binding in <sofa:reference/>, <sofa:reference/> can only have one binding.");

        // default add jvm binding and reference jvm binding should set serialize as false
        if (bindings.size() == 0) {
            // default reference prefer to ignore serialize
            JvmBindingParam jvmBindingParam = new JvmBindingParam();
            jvmBindingParam.setSerialize(false);
            bindings.add(new JvmBinding().setJvmBindingParam(jvmBindingParam));
        }

        reference.addBinding(bindings.get(0));
        ComponentDefinitionInfo definitionInfo = new ComponentDefinitionInfo();
        definitionInfo.setInterfaceMode(apiType ? InterfaceMode.api : InterfaceMode.spring);
        definitionInfo.putInfo(BEAN_ID, beanId);
        proxy = ReferenceRegisterHelper.registerReference(reference, bindingAdapterFactory,
            sofaRuntimeContext, applicationContext, definitionInfo);
    }

    @Override
    protected void setProperties(BindingConverterContext bindingConverterContext) {
        bindingConverterContext.setLoadBalance(loadBalance);
    }

    protected Reference buildReference() {
        Reference reference = new ReferenceImpl(uniqueId, getInterfaceClass(),
            InterfaceMode.spring, jvmFirst);
        reference.setRequired(required);
        return reference;
    }

    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        try {
            Class<?> type = getInterfaceClass();
            return type != null ? type : ReferenceFactoryBean.class;
        } catch (Throwable t) {
            // Class not found
            return ReferenceFactoryBean.class;
        }
    }

    @Override
    protected boolean isInBinding() {
        return true;
    }

    public void setJvmFirst(boolean jvmFirst) {
        this.jvmFirst = jvmFirst;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean getRequired() {
        return required;
    }
}
