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

/**
 * @author qilong.zql
 * @since 3.0.0
 */

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.tracer.boot.TestUtil;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.boot.base.SpringBootWebApplication;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcLogEnum;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
                                                                                                                                    "spring.main.web-application-type=reactive",
                                                                                                                                    "com.alipay.sofa.tracer.springmvc.jsonOutput=true" })
@TestPropertySource(properties = "spring.application.name=webflux-test")
public class WebFluxTracerTest extends AbstractTestBase {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int                definedPort;

    @Test
    public void testWebFluxTracer() throws Exception {
        String urlHttpPrefix = "http://localhost:" + definedPort;
        ResponseEntity<String> response = testRestTemplate
            .getForEntity(urlHttpPrefix, String.class);

        TestUtil.waitForAsyncLog();

        //wait for async output
        List<String> contents = FileUtils
            .readLines(customFileLog(SpringMvcLogEnum.SPRING_MVC_DIGEST.getDefaultLogName()));
        assertTrue(contents.size() == 1);
    }

}