/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.rpc.boot.test.container;

import com.alipay.sofa.rpc.boot.config.MulticastConfigurator;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaowang
 * @version : MulticastConfiguratorTest.java, v 0.1 2020年03月09日 3:23 下午 zhaowang Exp $
 */
public class MulticastConfiguratorTest {

    @Test
    public void test(){
        MulticastConfigurator multicastConfigurator = new MulticastConfigurator();
        RegistryConfig registryConfig = multicastConfigurator.buildFromAddress("multicast://192.168.1.33:1234?a=b");
        Assert.assertEquals(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_MULTICAST,registryConfig.getProtocol());
        Assert.assertEquals("192.168.1.33:1234",registryConfig.getAddress());
        Assert.assertEquals("b",registryConfig.getParameter("a"));
    }
}