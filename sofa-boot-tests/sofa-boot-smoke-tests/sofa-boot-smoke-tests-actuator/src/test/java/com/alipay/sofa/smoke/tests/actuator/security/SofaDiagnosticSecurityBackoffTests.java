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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@code SofaDiagnosticSecurityAutoConfiguration} backs off when the
 * application supplies its own {@link SecurityFilterChain}.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticSecurityBackoffTests.java, v 0.1 2026年04月03日 xiaosiyuan Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=sofa-diagnostic,health" })
@Import(SofaDiagnosticSecurityBackoffTests.UserDefinedSecurityConfig.class)
public class SofaDiagnosticSecurityBackoffTests {

    @Autowired
    private TestRestTemplate   restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * User-defined security config that permits all requests, simulating an application
     * with its own security policy — the library must not inject an additional filter chain.
     */
    @TestConfiguration
    static class UserDefinedSecurityConfig {

        @Bean
        public SecurityFilterChain userDefinedFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    public void sofaDiagnosticSecurityBeanIsNotRegisteredWhenUserHasOwnChain() {
        assertThat(applicationContext.containsBean("sofaDiagnosticSecurityFilterChain")).isFalse();
    }

    @Test
    public void sofaDiagnosticFollowsUserSecurityPolicyNotLibraryPolicy() {
        // User's chain permits all; the library's filter chain must NOT have been registered.
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/sofa-diagnostic",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
