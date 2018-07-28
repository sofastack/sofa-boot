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
package com.alipay.sofa.healthcheck.enable;

import com.alipay.sofa.healthcheck.base.AbstractTestBase;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * SofaBootReadinessCheckEnableTest
 *
 * @author yangguanchao
 * @since 2018/07/28
 */
@ActiveProfiles("disable")
public class SofaBootReadinessCheckUnableTest extends AbstractTestBase {

    @Test
    public void testUnableReadinessCheck() throws Exception {
        String urlHttp = this.getUrlPath("/health/readiness");
        ResponseEntity<Map> response = this.testRestTemplate.getForEntity(urlHttp, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
