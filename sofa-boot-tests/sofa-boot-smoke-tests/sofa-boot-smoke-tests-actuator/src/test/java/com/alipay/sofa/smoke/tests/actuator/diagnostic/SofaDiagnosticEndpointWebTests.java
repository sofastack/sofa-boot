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
package com.alipay.sofa.smoke.tests.actuator.diagnostic;

import com.alipay.sofa.boot.actuator.diagnostic.SofaDiagnosticEndpoint;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.alipay.sofa.smoke.tests.actuator.sample.beans.DefaultSampleService;
import com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaDiagnosticEndpoint} web response.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticEndpointWebTests.java, v 0.1 2026年04月02日 xiaosiyuan Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=sofa-diagnostic" })
@Import(SofaDiagnosticEndpointWebTests.Config.class)
public class SofaDiagnosticEndpointWebTests {

    private static final String SAMPLE_SERVICE_INTERFACE = SampleService.class.getName();

    private final ObjectMapper  objectMapper             = new ObjectMapper();

    private final HttpEntity<?> actuatorPostRequest      = createActuatorPostRequest();

    @Autowired
    private TestRestTemplate    restTemplate;

    @Test
    public void sofaDiagnosticActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/sofa-diagnostic",
            String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"components\":{\"total\":")
            .contains("\"threadPools\":[{\"threadPoolName\":\"diagnosticThreadPool\"")
            .contains("\"jvm\":{\"javaVersion\":\"").contains("\"memory\":{\"heap\":{\"used\":");
    }

    @Test
    public void sofaDiagnosticServicesActuator() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/actuator/sofa-diagnostic/services/all", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        JsonNode published = body.get("published");
        JsonNode referenced = body.get("referenced");

        assertThat(published).isNotNull();
        assertThat(referenced).isNotNull();
        assertThat(published).anySatisfy(node -> {
            assertThat(node.get("interfaceType").asText()).isEqualTo(SAMPLE_SERVICE_INTERFACE);
            assertThat(node.get("uniqueId").asText()).isEqualTo("http");
        });
        assertThat(referenced).anySatisfy(node -> {
            assertThat(node.get("interfaceType").asText()).isEqualTo(SAMPLE_SERVICE_INTERFACE);
            assertThat(node.get("uniqueId").asText()).isEqualTo("consumer");
            assertThat(node.get("required").asBoolean()).isTrue();
        });
    }

    @Test
    public void sofaDiagnosticServiceDetailActuator() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/actuator/sofa-diagnostic/serviceDetail/{interfaceId}", String.class,
            SAMPLE_SERVICE_INTERFACE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        JsonNode publishedDetails = body.get("publishedDetails");
        JsonNode referencedDetails = body.get("referencedDetails");

        assertThat(body.get("interfaceId").asText()).isEqualTo(SAMPLE_SERVICE_INTERFACE);
        assertThat(publishedDetails).isNotNull();
        assertThat(referencedDetails).isNotNull();
        assertThat(publishedDetails).anySatisfy(node -> {
            assertThat(node.get("uniqueId").asText()).isEqualTo("http");
            assertThat(node.get("implementationClass").asText()).isEqualTo(
                DefaultSampleService.class.getName());
        });
        assertThat(referencedDetails).anySatisfy(node -> {
            assertThat(node.get("uniqueId").asText()).isEqualTo("consumer");
            assertThat(node.get("jvmFirst").asBoolean()).isTrue();
            assertThat(node.get("required").asBoolean()).isTrue();
        });
    }

    @Test
    public void sofaDiagnosticGcCommand() throws JsonProcessingException {
        ResponseEntity<String> response = postToActuator("/actuator/sofa-diagnostic/gc");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("success").asBoolean()).isTrue();
        assertThat(body.get("message").asText()).isEqualTo("GC triggered successfully");
        assertThat(body.get("data")).hasSize(3);
        assertThat(body.get("data").has("memoryBeforeGc")).isTrue();
        assertThat(body.get("data").has("memoryAfterGc")).isTrue();
        assertThat(body.get("data").has("memoryFreed")).isTrue();
    }

    @Test
    public void sofaDiagnosticThreadDumpCommand() throws JsonProcessingException {
        ResponseEntity<String> response = postToActuator("/actuator/sofa-diagnostic/thread-dump");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("success").asBoolean()).isTrue();
        assertThat(body.get("message").asText()).isEqualTo("Thread dump generated successfully");
        assertThat(body.get("data").get("threadCount").asInt()).isPositive();
        assertThat(body.get("data").get("threads")).isNotEmpty();
    }

    @Test
    public void sofaDiagnosticHeapDumpCommand() throws Exception {
        ResponseEntity<String> response = postToActuator("/actuator/sofa-diagnostic/heap-dump");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        Path heapDumpPath = Path.of(body.get("data").get("path").asText());
        try {
            assertThat(body.get("success").asBoolean()).isTrue();
            assertThat(body.get("message").asText()).isEqualTo("Heap dump generated successfully");
            assertThat(body.get("data").get("liveOnly").asBoolean()).isTrue();
            assertThat(Files.exists(heapDumpPath)).isTrue();
            assertThat(body.get("data").get("size").asLong()).isGreaterThan(0L);
        } finally {
            Files.deleteIfExists(heapDumpPath);
        }
    }

    @Test
    public void sofaDiagnosticHeapDumpCommandWithInvalidPath() throws JsonProcessingException {
        String path = System.getProperty("java.io.tmpdir") + "/missing-dir-" + System.nanoTime()
                      + "/heap.hprof";
        String url = UriComponentsBuilder.fromPath("/actuator/sofa-diagnostic/heap-dump")
            .queryParam("path", path).queryParam("liveOnly", false).build().toUriString();

        ResponseEntity<String> response = postToActuator(url);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("success").asBoolean()).isFalse();
        assertThat(body.get("message").asText()).startsWith("Failed to generate heap dump:");
        assertThat(body.get("data").has("error")).isTrue();
    }

    private ResponseEntity<String> postToActuator(String url) {
        return restTemplate.exchange(url, HttpMethod.POST, actuatorPostRequest, String.class);
    }

    private HttpEntity<?> createActuatorPostRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("{}", headers);
    }

    @Configuration
    static class Config {

        @SofaReference(uniqueId = "consumer", binding = @SofaReferenceBinding(bindingType = "bolt", timeout = 1500, retries = 5, serializeType = "json"))
        private SampleService sampleServiceConsumer;

        @Bean
        public SofaThreadPoolExecutor sofaThreadPoolExecutor() {
            return new SofaThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(5), "diagnosticThreadPool");
        }

        @Bean
        @SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt", registry = "nacos") })
        public SampleService sampleServiceA() {
            return new DefaultSampleService();
        }

        @Bean
        @SofaService(uniqueId = "http", bindings = { @SofaServiceBinding(bindingType = "http") })
        public SampleService sampleServiceB() {
            return new DefaultSampleService();
        }
    }
}
