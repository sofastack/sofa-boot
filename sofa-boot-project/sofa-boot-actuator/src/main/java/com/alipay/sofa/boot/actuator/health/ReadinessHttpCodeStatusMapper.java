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
package com.alipay.sofa.boot.actuator.health;

import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.SimpleHttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SOFABoot custom {@link HttpCodeStatusMapper} backed by map of {@link Status#getCode() status
 * code} to HTTP status code.
 *
 * @author huzijie
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/3/22
 */
public class ReadinessHttpCodeStatusMapper implements HttpCodeStatusMapper {

    private static final Map<String, Integer> DEFAULT_MAPPINGS;

    static {
        Map<String, Integer> defaultMappings = new HashMap<>(8);
        defaultMappings.put(Status.DOWN.getCode(), WebEndpointResponse.STATUS_SERVICE_UNAVAILABLE);
        defaultMappings.put(Status.OUT_OF_SERVICE.getCode(),
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
        defaultMappings.put(Status.UNKNOWN.getCode(),
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
        DEFAULT_MAPPINGS = getUniformMappings(defaultMappings);
    }

    private final SimpleHttpCodeStatusMapper  statusMapper;

    public ReadinessHttpCodeStatusMapper() {
        this(null);
    }

    public ReadinessHttpCodeStatusMapper(Map<String, Integer> mappings) {
        Map<String, Integer> mapping = new HashMap<>(8);

        // add custom status mapper
        mapping.putAll(DEFAULT_MAPPINGS);
        if (mappings != null) {
            mapping.putAll(getUniformMappings(mappings));
        }
        statusMapper = new SimpleHttpCodeStatusMapper(mapping);
    }

    @Override
    public int getStatusCode(Status status) {
        return statusMapper.getStatusCode(status);
    }

    private static Map<String, Integer> getUniformMappings(Map<String, Integer> mappings) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : mappings.entrySet()) {
            String code = getUniformCode(entry.getKey());
            if (code != null) {
                result.putIfAbsent(code, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static String getUniformCode(String code) {
        if (code == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
                builder.append(Character.toLowerCase(ch));
            }
        }
        return builder.toString();
    }
}
