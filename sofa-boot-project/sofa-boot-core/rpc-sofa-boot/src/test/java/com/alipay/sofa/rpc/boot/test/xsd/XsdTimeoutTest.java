package com.alipay.sofa.rpc.boot.test.xsd;

import com.alipay.sofa.rpc.boot.runtime.binding.BoltBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingType;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
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
@ImportResource("/spring/xsd.xml")
public class XsdTimeoutTest {
    @Autowired
    WhateverInterface whatever;
    @Autowired
    SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void testService() {
        Assert.assertEquals(whatever.say(), "whatever");
    }

    @Test
    public void testServiceTimeout() {
        ServiceComponent component = (ServiceComponent) sofaRuntimeContext.getComponentManager().getComponentInfo(
                new ComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE, WhateverInterface.class.getName()));
        RpcBinding binding = (RpcBinding) component.getService().getBinding(RpcBindingType.BOLT_BINDING_TYPE);
        Assert.assertEquals((long) binding.getRpcBindingParam().getTimeout(), 10000);
    }

    @Test
    public void testReferenceTimeout() {
        ReferenceComponent component = (ReferenceComponent) (sofaRuntimeContext.getComponentManager()
                .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE).iterator().next());
        RpcBinding binding = (RpcBinding) component.getReference().getBinding(RpcBindingType.BOLT_BINDING_TYPE);
        Assert.assertEquals((long) binding.getRpcBindingParam().getTimeout(), 10000);
    }
}
