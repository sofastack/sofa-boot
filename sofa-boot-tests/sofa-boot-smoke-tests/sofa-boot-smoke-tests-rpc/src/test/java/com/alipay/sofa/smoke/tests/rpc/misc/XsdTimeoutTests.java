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
package com.alipay.sofa.smoke.tests.rpc.misc;

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingType;
import com.alipay.sofa.rpc.boot.runtime.converter.RpcBindingConverter;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.misc.MethodElementInterface;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.misc.WhateverInterface;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2019/12/18
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = { "timeout=10000" })
@Import(XsdTimeoutTests.XsdTimeoutConfiguration.class)
public class XsdTimeoutTests {
    @Autowired
    WhateverInterface  whatever;
    @Autowired
    SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void service() {
        assertEquals(whatever.say(), "whatever");
    }

    @Test
    public void serviceTimeout() {
        ServiceComponent component = (ServiceComponent) sofaRuntimeContext.getComponentManager()
            .getComponentInfo(
                new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE, WhateverInterface.class
                    .getName()));
        RpcBinding binding = (RpcBinding) component.getService().getBinding(
            RpcBindingType.BOLT_BINDING_TYPE);
        assertThat((long) binding.getRpcBindingParam().getTimeout()).isEqualTo(10000);
    }

    /**
     * @see RpcBindingConverter#parseMethod
     */
    @Test
    public void bindingMethod() {
        //service
        ServiceComponent component = (ServiceComponent) sofaRuntimeContext.getComponentManager()
            .getComponentInfo(
                new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
                    MethodElementInterface.class.getName()));
        RpcBinding binding = (RpcBinding) component.getService().getBinding(
            RpcBindingType.BOLT_BINDING_TYPE);
        assertThat(binding.getRpcBindingParam().getMethodInfos().size()).isEqualTo(1);
        assertThat(binding.getRpcBindingParam().getMethodInfos().get(0).getName()).isEqualTo(
            "service");
        assertThat(binding.getRpcBindingParam().getMethodInfos().get(0).getTimeout().intValue())
            .isEqualTo(10000);

        //reference
        Collection<ComponentInfo> componentInfos = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        for (ComponentInfo componentInfo : componentInfos) {
            if (componentInfo instanceof ReferenceComponent) {
                ReferenceComponent referenceComponent = (ReferenceComponent) componentInfo;
                if (referenceComponent.getReference().getInterfaceType()
                    .equals(MethodElementInterface.class)) {
                    RpcBinding refBinding = (RpcBinding) referenceComponent.getReference()
                        .getBinding(RpcBindingType.BOLT_BINDING_TYPE);
                    assertThat(refBinding.getRpcBindingParam().getMethodInfos().size())
                        .isEqualTo(1);
                    assertThat(refBinding.getRpcBindingParam().getMethodInfos().get(0).getName())
                        .isEqualTo("service");
                }
            }
        }
    }

    @Test
    public void referenceTimeout() {
        Collection<ComponentInfo> c = sofaRuntimeContext.getComponentManager()
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        for (ComponentInfo componentInfo : c) {
            if (componentInfo instanceof ReferenceComponent) {
                ReferenceComponent referenceComponent = (ReferenceComponent) componentInfo;
                if (!referenceComponent.getReference().getInterfaceType()
                    .equals(WhateverInterface.class)) {
                    continue;
                }
                Binding binding = referenceComponent.getReference().getBinding(
                    RpcBindingType.BOLT_BINDING_TYPE);
                if (binding instanceof RpcBinding) {
                    RpcBinding rpcBinding = (RpcBinding) binding;
                    assertThat((long) rpcBinding.getRpcBindingParam().getTimeout())
                        .isEqualTo(10000);
                }
            }
        }
    }

    @Configuration
    @ImportResource("/spring/service_reference.xml")
    static class XsdTimeoutConfiguration {

    }
}
