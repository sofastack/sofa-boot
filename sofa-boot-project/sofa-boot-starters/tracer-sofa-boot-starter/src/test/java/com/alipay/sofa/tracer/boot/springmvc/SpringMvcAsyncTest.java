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
package com.alipay.sofa.tracer.boot.springmvc;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.alipay.sofa.tracer.boot.TestUtil;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.boot.base.controller.SampleRestController;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcLogEnum;

/**
 * SpringMvcFilterTest
 *
 * @author yangguanchao
 * @since 2018/08/31
 */
@ActiveProfiles("async")
public class SpringMvcAsyncTest extends AbstractTestBase {

    @Test
    public void testAsyncServlet() throws Exception {
        String restUrl = urlHttpPrefix + "/asyncServlet";

        ResponseEntity<String> response = testRestTemplate.getForEntity(restUrl, String.class);
        String greetingResponse = response.getBody();
        assertTrue(greetingResponse.equals(SampleRestController.ASYNC_RESP));
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        // http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing

        TestUtil.waitForAsyncLog();

        //wait for async output
        List<String> contents = FileUtils
            .readLines(customFileLog(SpringMvcLogEnum.SPRING_MVC_DIGEST.getDefaultLogName()));
        assertTrue(contents.size() == 1);
    }
}
