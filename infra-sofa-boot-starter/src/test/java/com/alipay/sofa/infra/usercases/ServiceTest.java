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
package com.alipay.sofa.infra.usercases;

import com.alipay.sofa.infra.base.AbstractTestBase;
import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpointMvcAdapter;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * ServiceTest
 *
 * @author yangguanchao
 * @since 2018/01/04
 */
public class ServiceTest extends AbstractTestBase {

    @Test
    public void testServiceGet() {
        assertNotNull(urlHttpPrefix);
        String sofaBootVersionUrl = urlHttpPrefix + "/"
                                    + SofaBootVersionEndpointMvcAdapter.SOFA_BOOT_VERSION_URL;
        ResponseEntity<String> result = testRestTemplate.getForEntity(sofaBootVersionUrl,
            String.class);
        assertEquals(result.getStatusCode().value(), (HttpStatus.OK.value()));
        assertNotNull(result);
    }
}
