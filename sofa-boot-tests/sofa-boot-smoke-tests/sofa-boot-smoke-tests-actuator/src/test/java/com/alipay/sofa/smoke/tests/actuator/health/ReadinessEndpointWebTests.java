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
package com.alipay.sofa.smoke.tests.actuator.health;

import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ReadinessEndpoint} web response.
 *
 * @author huzijie
 * @version ReadinessEndpointTest.java, v 0.1 2022年04月28日 11:11 AM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=readiness" })
public class ReadinessEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void health() {
        ResponseEntity<HealthResponse> response = restTemplate.getForEntity("/actuator/readiness",
            HealthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        HealthResponse health = response.getBody();
        assertThat(health).isNotNull();
        assertThat(health.getDetails()).isNotNull();

        response = restTemplate.getForEntity("/actuator/readiness?showDetail=false",
            HealthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        health = response.getBody();
        assertThat(health).isNotNull();
        assertThat(health.getDetails()).isNull();
    }

    private static class HealthResponse {

        private String              status;

        private Map<String, Object> details;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }
}
