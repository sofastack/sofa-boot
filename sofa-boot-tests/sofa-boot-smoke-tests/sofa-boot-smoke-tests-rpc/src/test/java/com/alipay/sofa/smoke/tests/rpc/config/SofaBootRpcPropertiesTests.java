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
package com.alipay.sofa.smoke.tests.rpc.config;

import com.alipay.sofa.boot.autoconfigure.rpc.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaBootRpcProperties}.
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = {
                                                                      "sofa.boot.rpc"
                                                                              + ".bolt.port=5000",
                                                                      "com_alipay_sofa_rpc_bolt_thread_pool_max_size=600",
                                                                      "sofa.boot.rpc"
                                                                              + ".registries.zk1=zookeeper://xxxx",
                                                                      "sofa.boot.rpc"
                                                                              + ".consumer.repeated.reference.limit=10",
                                                                      "sofa.boot.rpc"
                                                                              + ".rest.allowed.origins=a.com" })
public class SofaBootRpcPropertiesTests {

    @Autowired
    private SofaBootRpcProperties   sofaBootRpcProperties;

    @Autowired
    private ConsumerConfigContainer consumerConfigContainer;

    @Test
    public void camelCaseToDot() {
        assertThat(sofaBootRpcProperties.camelToDot("comAlipaySofa"));
        assertThat(sofaBootRpcProperties.camelToDot("ComAlipaySofa"));
    }

    @Test
    public void dotConfig() {
        assertThat(sofaBootRpcProperties.getBoltPort()).isEqualTo("5000");
    }

    @Test
    public void consumerRepeatedReferenceLimit() {
        Map configMap = consumerConfigContainer.getConsumerConfigMap();
        for (Object consumerConfig : configMap.values()) {
            assertThat(((ConsumerConfig) consumerConfig).getRepeatedReferLimit()).isEqualTo(10);
        }
    }

    @Test
    public void allowedOriginis() {
        assertThat(sofaBootRpcProperties.getRestAllowedOrigins()).isEqualTo("a.com");
    }
}
