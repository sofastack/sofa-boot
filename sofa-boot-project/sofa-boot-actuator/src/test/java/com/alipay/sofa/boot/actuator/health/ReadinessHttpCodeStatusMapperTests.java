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

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReadinessHttpCodeStatusMapper}.
 *
 * @author huzijie
 * @version ReadinessHttpCodeStatusMapperTests.java, v 0.1 2023年03月14日 5:58 PM huzijie Exp $
 */
public class ReadinessHttpCodeStatusMapperTests {

    @Test
    public void checkDefaultMapper() {
        ReadinessHttpCodeStatusMapper mapper = new ReadinessHttpCodeStatusMapper();
        assertThat(mapper.getStatusCode(Status.UP)).isEqualTo(WebEndpointResponse.STATUS_OK);
        assertThat(mapper.getStatusCode(Status.DOWN)).isEqualTo(
            WebEndpointResponse.STATUS_SERVICE_UNAVAILABLE);
        assertThat(mapper.getStatusCode(Status.OUT_OF_SERVICE)).isEqualTo(
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
        assertThat(mapper.getStatusCode(Status.UNKNOWN)).isEqualTo(
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void checkCustomMapper() {
        Map<String, Integer> customMapping = Map.of("up", 300, "DOWN", 301, "OUTOFSERVICE", 302);
        ReadinessHttpCodeStatusMapper mapper = new ReadinessHttpCodeStatusMapper(customMapping);
        assertThat(mapper.getStatusCode(Status.UP)).isEqualTo(300);
        assertThat(mapper.getStatusCode(Status.DOWN)).isEqualTo(301);
        assertThat(mapper.getStatusCode(Status.OUT_OF_SERVICE)).isEqualTo(302);
        assertThat(mapper.getStatusCode(Status.UNKNOWN)).isEqualTo(
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
    }
}
