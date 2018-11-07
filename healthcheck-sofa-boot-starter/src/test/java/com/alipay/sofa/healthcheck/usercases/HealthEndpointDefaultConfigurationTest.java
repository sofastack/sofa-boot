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
package com.alipay.sofa.healthcheck.usercases;

import com.alipay.sofa.healthcheck.base.SofaBootTestApplication;
import com.alipay.sofa.healthcheck.service.SofaBootReadinessCheckEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HealthEndpointDefaultConfigurationTest {

    @Autowired
    public ApplicationContext ctx;

    @Autowired
    private TestRestTemplate  restTemplate;

    @Test
    public void testReadinessCheckSuccessHttpCode() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/readiness",
            String.class);
        org.junit.Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test() {
        SofaBootReadinessCheckEndpoint sofaBootReadinessCheckEndpoint = ctx
            .getBean(SofaBootReadinessCheckEndpoint.class);
        Assert.notNull(sofaBootReadinessCheckEndpoint);
    }

}
