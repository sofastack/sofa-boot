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
package com.alipay.sofa.rpc.boot.test.readiness;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.boot.test.bean.SampleFacade;
import com.alipay.sofa.rpc.boot.test.util.TestUtils;
import com.alipay.sofa.rpc.core.exception.SofaRouteException;

@SpringBootApplication
@SpringBootTest(properties = { "com.alipay.sofa.rpc.registry.address=zookeeper://localhost:2181" }, classes = ReadinessTest.class)
@RunWith(SpringRunner.class)
@ImportResource("/spring/readiness.xml")
@Import(ReadinessTest.Config.class)
public class ReadinessTest extends ActivelyDestroyTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    private SampleFacade     sampleFacade;

    @Test
    public void testCannotFoundAddress() throws InterruptedException {
        thrown.expect(SofaRouteException.class);
        thrown.expectMessage("RPC-02306");
        TimeUnit.SECONDS.sleep(1);
        Assert.assertEquals("hi World!", sampleFacade.sayHi("World"));
    }

    @Test
    public void testPortNotOpen() {
        Assert.assertTrue(TestUtils.available(SofaBootRpcConfigConstants.BOLT_PORT_DEFAULT));
    }

    @TestConfiguration
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
