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
package com.alipay.sofa.smoke.tests.actuator.diagnostic;

import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the read-only default access of sofa diagnostic endpoint.
 *
 * @author xiaosiyuan
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=sofa-diagnostic" })
@Import(SofaDiagnosticEndpointWebTests.Config.class)
public class SofaDiagnosticEndpointReadOnlyWebTests {

    private final HttpEntity<?> actuatorPostRequest = createActuatorPostRequest();

    @Autowired
    private TestRestTemplate    restTemplate;

    @Test
    public void sofaDiagnosticReadOperationsRemainAvailable() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/sofa-diagnostic",
            String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void sofaDiagnosticWriteOperationsAreBlockedByDefault() {
        ResponseEntity<String> response = restTemplate.exchange("/actuator/sofa-diagnostic/gc",
            HttpMethod.POST, actuatorPostRequest, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    private HttpEntity<?> createActuatorPostRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("{}", headers);
    }
}
