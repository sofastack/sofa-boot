/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.infra.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 参考文档: http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
 *
 * mock : https://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-mocking-beans
 *
 * OutputCapture : https://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-output-capture-test-utility
 *
 * <p>
 * <p>
 * <p/>
 * Created by yangguanchao on 16/11/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = com.alipay.sofa.infra.base.SofaBootWebSpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractTestBase {

    public static final String           SOFA_REST_PORT = "8341";

    @Autowired
    public EmbeddedWebApplicationContext server;

    /**
     * 8080
     */
    @LocalServerPort
    public int                           definedPort;

    @Autowired
    public TestRestTemplate              testRestTemplate;

    public String                        urlHttpPrefix;

    public String                        sofaRestHttpPrefix;

    @Before
    public void setUp() throws Exception {
        sofaRestHttpPrefix = "http://localhost:" + SOFA_REST_PORT;
        urlHttpPrefix = "http://localhost:" + definedPort;
    }

}
