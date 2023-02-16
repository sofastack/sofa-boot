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
package com.alipay.sofa.smoke.tests.rpc.misc;

import com.alipay.sofa.boot.actuator.health.ComponentHealthChecker;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.smoke.tests.rpc.boot.RpcSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2019/12/19
 */
@SpringBootTest(classes = RpcSofaBootApplication.class, properties = { "timeout=10000" })
@Import(ComponentHealthCheckerTests.ComponentHealthCheckerTestConfiguration.class)
public class ComponentHealthCheckerTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void componentHealthCheckerTest() {
        ComponentHealthChecker componentHealthChecker = applicationContext
            .getBean(ComponentHealthChecker.class);
        Health health = componentHealthChecker.isHealthy();
        Map<String, Object> details = health.getDetails();
        for (String key : details.keySet()) {
            assertThat(((String) details.get(key)).contains("passed"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ImportResource("/spring/service_reference.xml")
    static class ComponentHealthCheckerTestConfiguration {
        @Bean
        public ComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new ComponentHealthChecker(sofaRuntimeContext);
        }
    }
}
