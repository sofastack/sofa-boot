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
package com.alipay.sofa.boot.actuator.autoconfigure.rpc;

import com.alipay.sofa.boot.actuator.rpc.RpcAfterHealthCheckCallback;
import com.alipay.sofa.boot.actuator.rpc.SofaRpcEndpoint;
import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RpcActuatorAutoConfiguration}.
 *
 * @author huzijie
 * @version RpcActuatorAutoConfigurationTests.java, v 0.1 2023年02月22日 10:34 AM huzijie Exp $
 */
public class RpcActuatorAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(
                                                                 AutoConfigurations
                                                                     .of(RpcActuatorAutoConfiguration.class))
                                                             .withPropertyValues(
                                                                 "management.endpoints.web.exposure.include=readiness,rpc");

    @Test
    void runShouldHaveRpcActuatorBeans() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .run((context) -> assertThat(context)
                        .hasSingleBean(RpcAfterHealthCheckCallback.class)
                        .hasSingleBean(SofaRpcEndpoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveReadinessBeans() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .withPropertyValues("management.endpoints.web.exposure.include=info")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class)
                        .doesNotHaveBean(SofaRpcEndpoint.class));
    }

    @Test
    void runWhenRpcClassNotExist() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .withClassLoader(new FilteredClassLoader(RpcStartApplicationListener.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class)
                        .doesNotHaveBean(SofaRpcEndpoint.class));
    }

    @Test
    void runWhenRpcBeanNotExist() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class)
                        .doesNotHaveBean(SofaRpcEndpoint.class));
    }
}
