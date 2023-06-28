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
package com.alipay.sofa.smoke.tests.rpc.readiness;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.core.exception.SofaRouteException;
import com.alipay.sofa.smoke.tests.rpc.ActivelyDestroyTests;
import com.alipay.sofa.smoke.tests.rpc.boot.bean.SampleFacade;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import com.alipay.sofa.smoke.tests.rpc.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for check health.
 */
@SpringBootTest(properties = { "sofa.boot.rpc.registry.address=zookeeper://localhost:2181" }, classes = RpcSofaBootApplication.class)
@Import({ ReadinessTests.Config.class })
public class ReadinessTests extends ActivelyDestroyTests {

    @Autowired
    private SampleFacade sampleFacade;

    @Test
    public void cannotFoundAddress() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        SofaRouteException thrown = assertThrows(SofaRouteException.class, () -> {
            sampleFacade.sayHi("World");
        });
        assertThat(thrown.getMessage()).contains("RPC-020060001");
    }

    @Test
    public void portNotOpen() {
        assertTrue(TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT));
    }

    @TestConfiguration
    @ImportResource("/spring/readiness.xml")
    static class Config {
        @Bean
        public DownHealthIndicator downHealthIndicator() {
            return new DownHealthIndicator();
        }
    }

    static class DownHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.down().build();
        }
    }
}
