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
import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpoint;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.*;

/**
 * ServiceTest
 *
 * @author yangguanchao
 * @since 2018/01/04
 */
public class ServiceTest extends AbstractTestBase {

    @Test
    public void testServiceGet() throws Exception {

        assertNotNull(urlHttpPrefix);
        String sofaBootVersionUrl = urlHttpPrefix + SofaBootVersionEndpoint.SOFA_BOOT_VERSION_PREFIX;

        //TODO 注意 RestSampleFacadeResp 一定要有默认构造函数
        ResponseEntity<List> result = testRestTemplate.getForEntity(sofaBootVersionUrl, List.class);
        assertEquals(result.getStatusCode().value(), (HttpStatus.OK.value()));
        assertNotNull(result);
        System.err.println(result);
        //        RestSampleFacadeResp restSampleFacadeResp = response.getBody();
        //        assertTrue(restSampleFacadeResp.isSuccess());
        // TODO 注意:这里只是测试使用,使用的是 jackson 不支持泛型的反序列化,所以被反序列化为 map,这里是仅供测试使用,使用详情参考文档:http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
        //        Map<String, Integer> demoUserModel = (Map<String, Integer>) restSampleFacadeResp.getData();
        //        assertTrue(demoUserModel.get("userId") >= 0);

    }
}
