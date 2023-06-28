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
package com.alipay.sofa.smoke.tests.actuator.beans;

import com.alipay.sofa.boot.actuator.beans.IsleBeansEndpoint;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link IsleBeansEndpoint} web response.
 *
 * @author huzijie
 * @version IsleBeanEndpointWebTests.java, v 0.1 2022年03月17日 11:43 AM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=beans" })
public class IsleBeanEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void isleBeanActuator() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate
            .getForEntity("/actuator/beans", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode contextNode = rootNode.get("contexts");
        assertThat(contextNode).isNotNull();
        assertThat(contextNode.size()).isEqualTo(2);
        assertThat(contextNode.get("smoke-tests-actuator")).isNotNull();
        assertThat(contextNode.get("testModule")).isNotNull();
        assertThat(response.getBody()).contains("sample");
        assertThat(response.getBody()).contains("test");
    }
}
