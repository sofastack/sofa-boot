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

import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
    private SofaBootRpcProperties sofaBootRpcProperties;

    @Autowired
    private ApplicationContext    context;

    @Test
    public void testCamelCaseToDot() {

        assertThat(sofaBootRpcProperties.camelToDot("comAlipaySofa"));
        assertThat(sofaBootRpcProperties.camelToDot("ComAlipaySofa"));
    }

    @Test
    public void testDotConfig() {
        assertThat(sofaBootRpcProperties.getBoltPort()).isEqualTo("5000");
    }

    @Test
    public void testConsumerRepeatedReferenceLimit() {
        Map configMap = context.getBean(ConsumerConfigContainer.class).getConsumerConfigMap();
        for (Object consumerConfig : configMap.values()) {
            assertThat(((ConsumerConfig) consumerConfig).getRepeatedReferLimit()).isEqualTo(10);
        }
    }

    @Test
    @Disabled("do not support in spring boot 2.0")
    public void testUnderscoreConfig() {
        SofaBootRpcProperties sofaBootRpcProperties = context.getBean(SofaBootRpcProperties.class);
        assertThat(sofaBootRpcProperties.getBoltThreadPoolMaxSize()).isEqualTo("600");
    }

    @Test
    public void testAllowedOriginis() {
        SofaBootRpcProperties sofaBootRpcProperties = context.getBean(SofaBootRpcProperties.class);
        assertThat(sofaBootRpcProperties.getRestAllowedOrigins()).isEqualTo("a.com");
    }
}
