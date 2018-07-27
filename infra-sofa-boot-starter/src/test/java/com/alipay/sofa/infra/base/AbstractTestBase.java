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
package com.alipay.sofa.infra.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 参考文档: http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
 * <p>
 * mock : https://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-mocking-beans
 * <p>
 * OutputCapture : https://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-output-capture-test-utility
 * <p>
 * <p>
 * <p>
 * <p/>
 * @author guanchaoyang
 * @since 2.3.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaBootWebSpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "management.endpoints.web.exposure.include=*" })
public abstract class AbstractTestBase {

    /**
     * 8080
     */
    @LocalServerPort
    private int               definedPort;

    @Autowired
    public TestRestTemplate   testRestTemplate;

    @Autowired
    public ApplicationContext ctx;

    protected String          urlHttpPrefix;

    @Before
    public void setUp() {
        urlHttpPrefix = "http://localhost:" + definedPort;
    }

}
