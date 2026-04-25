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
package com.alipay.sofa.boot.actuator.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationMetadataTests {

    private static final String ADDITIONAL_METADATA = "META-INF/additional-spring-configuration-metadata.json";

    private static final String GENERATED_METADATA  = "META-INF/spring-configuration-metadata.json";

    @Test
    void additionalMetadataShouldDescribeActuatorGroups() {
        Map<String, Object> metadata = readMetadata(ADDITIONAL_METADATA);

        assertThat(names(metadata, "groups")).contains("sofa.boot.actuator.health",
            "sofa.boot.startup", "sofa.boot.startup.threads.virtual");
        assertThat(items(metadata, "groups")).allSatisfy((group) -> assertThat(group)
                .containsKeys("name", "description"));
    }

    @Test
    void additionalMetadataShouldDefineCompletionPropertiesAndHints() {
        Map<String, Object> metadata = readMetadata(ADDITIONAL_METADATA);

        assertThat(names(metadata, "properties")).contains(
            "sofa.boot.actuator.health.global-health-checker-timeout",
            "sofa.boot.actuator.health.global-health-indicator-timeout",
            "sofa.boot.actuator.health.parallel-check-timeout",
            "sofa.boot.actuator.health.status.http-mapping",
            "sofa.boot.startup.threads.virtual.enabled");
        assertThat(names(metadata, "hints")).contains(
            "sofa.boot.actuator.health.status.http-mapping.keys",
            "sofa.boot.actuator.health.status.order");
    }

    @Test
    void generatedMetadataShouldContainMergedAdditionalItems() {
        Map<String, Object> metadata = readMetadata(GENERATED_METADATA);

        assertThat(names(metadata, "properties")).contains(
            "sofa.boot.actuator.health.global-health-checker-timeout",
            "sofa.boot.actuator.health.global-health-indicator-timeout",
            "sofa.boot.startup.threads.virtual.enabled");
        assertThat(names(metadata, "hints")).contains(
            "sofa.boot.actuator.health.status.http-mapping.keys",
            "sofa.boot.actuator.health.status.order");
    }

    private Map<String, Object> readMetadata(String location) {
        try {
            ClassPathResource resource = new ClassPathResource(location);
            assertThat(resource.exists()).as(location).isTrue();
            String content = StreamUtils.copyToString(resource.getInputStream(),
                StandardCharsets.UTF_8);
            return JsonParserFactory.getJsonParser().parseMap(content);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read " + location, ex);
        }
    }

    private List<String> names(Map<String, Object> metadata, String itemName) {
        return items(metadata, itemName).stream().map((item) -> (String) item.get("name")).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> items(Map<String, Object> metadata, String itemName) {
        return (List<Map<String, Object>>) metadata.get(itemName);
    }
}
