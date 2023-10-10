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
package com.alipay.sofa.smoke.tests.isle;

import com.alipay.sofa.boot.actuator.isle.IsleEndpoint;
import com.alipay.sofa.smoke.tests.isle.util.AddCustomJar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link IsleEndpoint} web response.
 *
 * @author huzijie
 * @version IsleEndpointWebTests.java, v 0.1 2023年10月10日 4:20 PM huzijie Exp $
 */
@SpringBootTest(classes = IsleSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
                                                                                                                                   "management.endpoints.web.exposure.include=isle",
                                                                                                                                   "sofa.boot.isle.activeProfiles=test",
                                                                                                                                   "sofa.boot.isle.ignore-module-install-failure=true" })
@AddCustomJar({ "sample-module", "dev-module", "fail-module", })
public class IsleEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void componentsActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/isle", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"name\":\"com.alipay.sofa.sample\"")
            .contains("\"name\":\"com.alipay.sofa.fail\"")
            .contains("\"name\":\"com.alipay.sofa.dev\"");
    }
}
