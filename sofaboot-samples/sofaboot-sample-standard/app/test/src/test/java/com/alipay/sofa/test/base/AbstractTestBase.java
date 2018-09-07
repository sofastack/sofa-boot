package com.alipay.sofa.test.base;

import com.alipay.sofa.SOFABootWebApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * AbstractTestBase
 * <p>
 * reference file: http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
 * <p/>
 * Created by yangguanchao on 16/11/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SOFABootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractTestBase {

    public static final Logger LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected String urlHttpPrefix;

    /**
     * 8080
     */
    @LocalServerPort
    public int definedPort;

    @Before
    public void setUp() {
        urlHttpPrefix = "http://localhost:" + definedPort;
    }
}
