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
package com.alipay.sofa.smoke.tests.actuator.security;

import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security integration tests for {@code /actuator/sofa-diagnostic}.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticSecurityTests.java, v 0.1 2026年04月03日 xiaosiyuan Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
                                                                                                                                       "management.endpoints.web.exposure.include=sofa-diagnostic,health",
                                                                                                                                       "spring.security.user.name=ops",
                                                                                                                                       "spring.security.user.password=123456" })
public class SofaDiagnosticSecurityTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void unauthenticatedAccessToSofaDiagnosticReturns401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/sofa-diagnostic",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void anonymousAccessToHealthReturns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void authenticatedAccessToSofaDiagnosticReturns200() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("ops", "123456").getForEntity(
            "/actuator/sofa-diagnostic", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
