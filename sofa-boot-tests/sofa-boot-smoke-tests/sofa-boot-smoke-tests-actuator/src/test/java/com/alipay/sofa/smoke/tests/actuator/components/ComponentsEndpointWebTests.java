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
package com.alipay.sofa.smoke.tests.actuator.components;

import com.alipay.sofa.boot.actuator.components.ComponentsEndpoint;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ComponentsEndpoint} web response.
 *
 * @author huzijie
 * @version ComponentsEndpointWebTests.java, v 0.1 2022年03月17日 4:46 PM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=components" })
public class ComponentsEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void componentsActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/components",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .contains(
                "\"reference\":[{\"name\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.TestService")
            .contains("\"extension\":[{\"name\":\"extension$word")
            .contains("\"name\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService")
            .contains("\"extension-point\":[{\"name\":\"extension$word")
            .contains("\"Spring\":[{\"name\":\"testModule");
    }
}
