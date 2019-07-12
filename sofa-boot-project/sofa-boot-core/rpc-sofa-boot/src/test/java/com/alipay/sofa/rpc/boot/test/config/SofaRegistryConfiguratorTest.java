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
package com.alipay.sofa.rpc.boot.test.config;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.sofa.rpc.boot.config.SofaRegistryConfigurator;
import com.alipay.sofa.rpc.config.RegistryConfig;

/**
 * @author zhiyuan.lzy
 * @version $Id: SofaRegistryConfiguratorTest.java, v 0.1 2010-03-16 17:36 zhiyuan.lzy Exp $$
 */
public class SofaRegistryConfiguratorTest {

    @Test
    public void buildFromAddress() {
        String address = "sofa://127.0.0.1:9603?cluster=test";

        SofaRegistryConfigurator sofaRegistryConfigurator = new SofaRegistryConfigurator();
        RegistryConfig registryConfig = sofaRegistryConfigurator.buildFromAddress(address);

        Assert.assertNotNull(registryConfig);
        Assert.assertEquals("sofa", registryConfig.getProtocol());
        Assert.assertEquals("127.0.0.1:9603", registryConfig.getAddress());
        Assert.assertNotNull(registryConfig.getParameters());
        Assert.assertEquals("test", registryConfig.getParameter("cluster"));
    }
}