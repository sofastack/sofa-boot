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
package com.alipay.sofa.smoke.tests.rpc.delayregister;

import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallback;
import com.alipay.sofa.rpc.core.exception.SofaRouteException;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.delayregister.DelayRegisterService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Base tests for rpc provider delay register.
 *
 * @author chengming
 * @version DelayRegisterTests.java, v 0.1 2024年02月26日 3:58 PM chengming
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = {
                                                                      "sofa.boot.rpc.registry.address=zookeeper://127.0.0.1:2181",
                                                                      "sofa.boot.rpc.enable-auto-publish=true",
                                                                      "sofa.boot.rpc.enable-delay-register=true" })
@Import(DelayRegisterTestsBase.DelayRegisterConfiguration.class)
public class DelayRegisterTestsBase {

    @BeforeAll
    public static void setUp() {
        System.setProperty("provider.delay", "2000");
    }

    @AfterAll
    public static void clear() {
        System.clearProperty("provider.delay");
    }

    @Autowired
    private DelayRegisterService delayRegisterService;

    protected void registerSuccess() {
        String hi = delayRegisterService.sayHello("hi");
        assertThat(hi).isEqualTo("hi");
    }

    protected void registerFail() {
        assertThatThrownBy(() -> delayRegisterService.sayHello("hi")).isInstanceOf(SofaRouteException.class).
                hasMessageContaining("Cannot get the service address of service");
    }

    @Configuration
    @ImportResource("/spring/test_only_delay_register.xml")
    static class DelayRegisterConfiguration {

    }

    public static class CustomReadinessCallBack implements ReadinessCheckCallback {

        @Value("${delayregister.healthcheck.result}")
        private boolean result;

        @Override
        public Health onHealthy(ApplicationContext applicationContext) {
            return result ? Health.up().build() : Health.down().build();
        }
    }
}
