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
package com.alipay.sofa.smoke.tests.actuator.reflection;

import com.alipay.sofa.boot.reflection.ReflectionCache;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@code /actuator/reflection-cache}.
 *
 * @author xiaosiyuan
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=reflection-cache" })
public class ReflectionCacheEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReflectionCache  reflectionCache;

    @Test
    public void reflectionCacheEndpoint() throws Exception {
        reflectionCache.forName(String.class.getName());
        reflectionCache.forName(String.class.getName());

        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/reflection-cache",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"enabled\":true").contains("\"classHitCount\":1")
            .contains("\"classMissCount\":1").contains("\"classCacheSize\":1");

        response = restTemplate.postForEntity("/actuator/reflection-cache", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalHitCount\":0")
            .contains("\"totalMissCount\":0").contains("\"classCacheSize\":0");
    }
}
