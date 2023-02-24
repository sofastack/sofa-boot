package com.alipay.sofa.smoke.tests.tracer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration for tracer springmvc and rest template enhance.
 *
 * @author huzijie
 * @version TracerMvcTests.java, v 0.1 2023年02月24日 5:56 PM huzijie Exp $
 */
@SpringBootTest(classes = TracerSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TracerMvcTests {

    private static final File mvcLogFile = new File(System.getProperty("user.dir")
            + "/logs/tracelog/spring-mvc-digest.log");

    private static final File restTemplateLogFile = new File(System.getProperty("user.dir")
            + "/logs/tracelog/resttemplate-digest.log");


    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeAll
    public static void clearFile() {
        FileUtils.deleteQuietly(mvcLogFile);
        FileUtils.deleteQuietly(restTemplateLogFile);
    }

    @Test
    public void invokeMvcRequest() throws IOException {
        String result = restTemplate.getForObject("http://127.0.0.1:" + port + "/hello", String.class);
        assertThat(result).isEqualTo("hello");

        checkMvcDigestLog();
        checkRestTemplateDigestLog();
    }

    private void checkRestTemplateDigestLog() throws IOException {
        List<String> logs = FileUtils.readLines(mvcLogFile, StandardCharsets.UTF_8);
        assertThat(logs).anyMatch(log -> log.contains("/hello"));
    }

    private void checkMvcDigestLog() throws IOException {
        List<String> logs = FileUtils.readLines(restTemplateLogFile, StandardCharsets.UTF_8);
        assertThat(logs).anyMatch(log -> log.contains("/hello"));
    }
}
