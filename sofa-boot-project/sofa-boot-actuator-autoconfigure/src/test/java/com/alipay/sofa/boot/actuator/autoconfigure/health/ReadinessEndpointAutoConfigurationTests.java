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
package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpointWebExtension;
import com.alipay.sofa.boot.actuator.health.ReadinessHttpCodeStatusMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReadinessEndpointAutoConfiguration}.
 *
 * @author huzijie
 * @version ReadinessEndpointAutoConfigurationTests.java, v 0.1 2022年12月30日 2:08 PM huzijie Exp $
 */
public class ReadinessEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(ReadinessEndpointAutoConfiguration.class,
                                                                     ReadinessAutoConfiguration.class,
                                                                     HealthEndpointAutoConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=readiness")
                .run((context) -> {
                    assertThat(context).hasSingleBean(ReadinessEndpoint.class);
                    assertThat(context).hasSingleBean(ReadinessEndpointWebExtension.class);
                    assertThat(context).getBean(HttpCodeStatusMapper.class).isInstanceOf(ReadinessHttpCodeStatusMapper.class);
                });
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.readiness.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(ReadinessEndpoint.class);
                    assertThat(context).doesNotHaveBean(ReadinessEndpointWebExtension.class);
                    assertThat(context).doesNotHaveBean(ReadinessHttpCodeStatusMapper.class);
                });
    }

    @Test
    void customHttpCodeStatusMapper() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=readiness")
                .withPropertyValues("sofa.boot.actuator.health.status.http-mapping.down=400")
                .run((context) -> {
                    ReadinessHttpCodeStatusMapper sofaHttpCodeStatusMapper = (ReadinessHttpCodeStatusMapper) context.getBean(HttpCodeStatusMapper.class);
                    assertThat(sofaHttpCodeStatusMapper.getStatusCode(Status.DOWN)).isEqualTo(400);
                });
    }
}
