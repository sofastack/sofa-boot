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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.boot.actuator.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author huzijie
 * @version ReadinessEndpointTest.java, v 0.1 2022年04月28日 11:11 AM huzijie Exp $
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmptyConfiguration.class)
@RunWith(SpringRunner.class)
public class ReadinessEndpointTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test() {
        ResponseEntity<HealthResponse> response = restTemplate.getForEntity("/actuator/readiness",
            HealthResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        HealthResponse health = response.getBody();
        Assert.assertNotNull(health);
        Assert.assertNotNull(health.getDetails());

        response = restTemplate.getForEntity("/actuator/readiness?showDetail=false",
            HealthResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        health = response.getBody();
        Assert.assertNotNull(health);
        Assert.assertNull(health.getDetails());
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
