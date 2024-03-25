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

import com.alipay.sofa.rpc.boot.config.KubernetesConfigurator;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KubernetesConfiguratorTest {

    @Test
    public void buildFromAddress() {
        String address = "kubernetes://kubernetes.default.svc:20881?cluster=test";

        KubernetesConfigurator kubernetesConfigurator = new KubernetesConfigurator();
        RegistryConfig registryConfig = kubernetesConfigurator.buildFromAddress(address);

        assertThat(registryConfig).isNotNull();
        assertThat("kubernetes").isEqualTo(registryConfig.getProtocol());
        assertThat("kubernetes.default.svc:20881").isEqualTo(registryConfig.getAddress());
        assertThat(registryConfig.getParameters()).isNotNull();
        assertThat("test").isEqualTo(registryConfig.getParameter("cluster"));
    }
}