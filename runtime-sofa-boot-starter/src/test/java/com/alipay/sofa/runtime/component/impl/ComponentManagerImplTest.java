package com.alipay.sofa.runtime.component.impl;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xuanbei 18/4/3
 */
public class ComponentManagerImplTest {
    private ComponentManagerImpl componentManager;
    private ComponentType type = new ComponentType("testType");
    private ComponentName name = new ComponentName(type, "object");

    @Test
    public void testRegister(@Mocked final ClientFactoryInternal mockClientFactoryInternal,
                             @Mocked final ComponentInfo mockComponentInfo) {
        new Expectations() {
            {
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.register();
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.resolve();
                result = true;
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.activate();
            }
        };
        componentManager = new ComponentManagerImpl(mockClientFactoryInternal);
        componentManager.register(mockComponentInfo);
        Assert.assertTrue(componentManager.registry.containsKey(name));
        Assert.assertTrue(componentManager.resolvedRegistry.get(type).containsValue(
                mockComponentInfo));
    }

//    @Test
//    public void testUnregister(@Mocked final ClientFactoryInternal mockClientFactoryInternal,
//                               @Mocked final ComponentInfo mockComponentInfo) {
//        new Expectations() {
//            {
//                mockComponentInfo.getName();
//                result = name;
//                mockComponentInfo.resolve();
//                result = true;
//            }
//        };
//        componentManager = new ComponentManagerImpl(mockClientFactoryInternal);
//        componentManager.register(mockComponentInfo);
//
//        componentManager.unregister(mockComponentInfo);
//        Assert.assertFalse(componentManager.registry.containsKey(name));
//        Assert.assertTrue(componentManager.resolvedRegistry.get(type).isEmpty());
//    }
//
//    @Test
//    public void testResolvePendingResolveComponent(@Mocked final ClientFactoryInternal mockClientFactoryInternal,
//                                                   @Mocked final ComponentInfo mockComponentInfo) {
//        new Expectations() {
//            {
//                mockComponentInfo.getName();
//                result = name;
//                mockComponentInfo.resolve();
//                result = false;
//            }
//        };
//        componentManager = new ComponentManagerImpl(mockClientFactoryInternal);
//        componentManager.register(mockComponentInfo);
//        Assert.assertTrue(componentManager.registry.containsKey(name));
//        Assert.assertFalse(componentManager.resolvedRegistry.containsKey(type));
//        new Expectations() {
//            {
//                mockComponentInfo.resolve();
//                result = true;
//            }
//        };
//
//        Assert.assertTrue(componentManager.resolvedRegistry.get(type).containsValue(
//                mockComponentInfo));
//    }
//
//    @Test
//    public void testShutDown(@Mocked final ClientFactoryInternal mockClientFactoryInternal,
//                             @Mocked final ComponentInfo mockComponentInfo) {
//        new Expectations() {
//            {
//                mockComponentInfo.getName();
//                result = name;
//                mockComponentInfo.resolve();
//                result = true;
//            }
//        };
//        componentManager = new ComponentManagerImpl(mockClientFactoryInternal);
//        componentManager.register(mockComponentInfo);
//        componentManager.shutdown();
//
//        Assert.assertNull(componentManager.registry);
//        Assert.assertNull(componentManager.resolvedRegistry);
//        Assert.assertNull(componentManager.clientFactoryInternal);
//    }

}
