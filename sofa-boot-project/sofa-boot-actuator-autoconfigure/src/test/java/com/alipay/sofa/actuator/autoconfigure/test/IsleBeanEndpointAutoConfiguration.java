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
package com.alipay.sofa.actuator.autoconfigure.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huzijie
 * @version IsleBeanEndpointAutoConfiguration.java, v 0.1 2022年03月17日 11:43 AM huzijie Exp $
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmptyConfiguration.class)
@RunWith(SpringRunner.class)
public class IsleBeanEndpointAutoConfiguration {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate
            .getForEntity("/actuator/beans", String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode contextNode = rootNode.get("contexts");
        Assert.assertNotNull(contextNode);
        Assert.assertEquals(2, contextNode.size());
        Assert.assertNotNull(contextNode.get("bootstrap"));
    }
}
