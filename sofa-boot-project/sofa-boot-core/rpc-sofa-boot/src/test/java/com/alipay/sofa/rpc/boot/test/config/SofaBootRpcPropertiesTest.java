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

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.boot.test.bean.SampleFacade;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(properties = {
                              SofaBootRpcProperties.PREFIX + ".bolt.port=5000",
                              "com_alipay_sofa_rpc_bolt_thread_pool_max_size=600",
                              SofaBootRpcProperties.PREFIX + ".registries.zk1=zookeeper://xxxx",
                              SofaBootRpcProperties.PREFIX
                                      + ".consumer.repeated.reference.limit=10",
                              SofaBootRpcProperties.PREFIX + ".rest.allowed.origins=a.com" })
public class SofaBootRpcPropertiesTest {
    @Autowired
    private SofaBootRpcProperties   sofaBootRpcProperties;

    @SofaReference(jvmFirst = false, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private SampleFacade            sampleFacade;

    @Autowired
    private ConsumerConfigContainer consumerConfigContainer;

    private Field                   consumerConfigMap;

    @Before
    public void setUp() throws Throwable {
        consumerConfigMap = ConsumerConfigContainer.class.getDeclaredField("consumerConfigMap");
        consumerConfigMap.setAccessible(true);
    }

    @Test
    public void testCamelCaseToDot() {
        Assert.assertEquals("com.alipay.sofa", sofaBootRpcProperties.camelToDot("comAlipaySofa"));
        Assert.assertEquals("com.alipay.sofa", sofaBootRpcProperties.camelToDot("ComAlipaySofa"));
    }

    @Test
    public void testDotConfig() {
        Assert.assertEquals("5000", sofaBootRpcProperties.getBoltPort());
    }

    @Test
    public void testConsumerRepeatedReferenceLimit() {
        try {
            Map configMap = (Map) consumerConfigMap.get(consumerConfigContainer);
            for (Object consumerConfig : configMap.values()) {
                Assert.assertEquals(10, ((ConsumerConfig) consumerConfig).getRepeatedReferLimit());
            }
        } catch (IllegalAccessException ex) {

        }
    }

    @Test
    @Ignore("do not support in spring boot 2.0")
    public void testUnderscoreConfig() {
        Assert.assertEquals("600", sofaBootRpcProperties.getBoltThreadPoolMaxSize());
    }

    @Test
    public void testCustoMapConfig() {
        Map<String, String> map = sofaBootRpcProperties.getRegistries();

        Assert.assertTrue(map != null);
        Assert.assertEquals("zookeeper://xxxx", map.get("zk1"));
    }

    @Test
    public void testAllowedOriginis() {
        String result = sofaBootRpcProperties.getRestAllowedOrigins();
        Assert.assertEquals("a.com", result);
    }

}
