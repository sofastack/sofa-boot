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
package com.alipay.sofa.rpc.boot.test.misc;

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingType;
import com.alipay.sofa.rpc.boot.runtime.converter.RpcBindingConverter;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2019/12/18
 */
@SpringBootApplication
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XsdTimeoutTest.class, properties = { "timeout=10000" })
@ImportResource("/spring/service_reference.xml")
public class XsdTimeoutTest {
    @Autowired
    WhateverInterface  whatever;
    @Autowired
    SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void testService() {
        Assert.assertEquals(whatever.say(), "whatever");
    }

    @Test
    public void testServiceTimeout() {
        ServiceComponent component = (ServiceComponent) sofaRuntimeContext.getComponentManager()
            .getComponentInfo(
                new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE, WhateverInterface.class
                    .getName()));
        RpcBinding binding = (RpcBinding) component.getService().getBinding(
            RpcBindingType.BOLT_BINDING_TYPE);
        Assert.assertEquals((long) binding.getRpcBindingParam().getTimeout(), 10000);
    }

    /**
     * @see RpcBindingConverter#parseMethod
     */
    @Test
    public void testBindingMethod() {
        //service
        ServiceComponent component = (ServiceComponent) sofaRuntimeContext.getComponentManager()
            .getComponentInfo(
                new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
                    MethodElementInterface.class.getName()));
        RpcBinding binding = (RpcBinding) component.getService().getBinding(
            RpcBindingType.BOLT_BINDING_TYPE);
        Assert.assertEquals(1, binding.getRpcBindingParam().getMethodInfos().size());
        Assert.assertEquals("service", binding.getRpcBindingParam().getMethodInfos().get(0)
            .getName());
        Assert.assertEquals(10000, binding.getRpcBindingParam().getMethodInfos().get(0)
            .getTimeout().intValue());

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
                    Assert.assertEquals(1, refBinding.getRpcBindingParam().getMethodInfos().size());
                    Assert.assertEquals("service", refBinding.getRpcBindingParam().getMethodInfos()
                        .get(0).getName());
                }
            }
        }
    }

    @Test
    public void testReferenceTimeout() {
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
                    Assert.assertEquals((long) rpcBinding.getRpcBindingParam().getTimeout(), 10000);
                }
            }
        }
    }
}
