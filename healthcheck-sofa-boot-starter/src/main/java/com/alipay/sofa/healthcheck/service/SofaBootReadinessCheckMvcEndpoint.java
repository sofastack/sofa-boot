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
package com.alipay.sofa.healthcheck.service;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.AbstractEndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author khotyn
 */
@ConfigurationProperties(prefix = "com.alipay.sofa.healthcheck.readiness")
public class SofaBootReadinessCheckMvcEndpoint
                                              extends
                                              AbstractEndpointMvcAdapter<SofaBootReadinessCheckEndpoint> {
    private Map<String, HttpStatus> statusMapping       = new HashMap<>();
    private static final String     READINESS_CHECK_URL = "health/readiness";

    /**
     * Create a new {@link EndpointMvcAdapter}.
     *
     * @param delegate the underlying {@link Endpoint} to adapt.
     */
    SofaBootReadinessCheckMvcEndpoint(SofaBootReadinessCheckEndpoint delegate) {
        super(delegate);
        setupDefaultStatusMapping();
        setPath(READINESS_CHECK_URL);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object invoke(Principal principal) {
        Health health = getDelegate().invoke();
        HttpStatus status = getStatus(health);
        if (status != null) {
            return new ResponseEntity<Health>(health, status);
        }
        return health;
    }

    private HttpStatus getStatus(Health health) {
        String code = health.getStatus().getCode();
        if (code != null) {
            code = code.toLowerCase().replace("_", "-");
            for (String candidate : RelaxedNames.forCamelCase(code)) {
                HttpStatus status = this.statusMapping.get(candidate);
                if (status != null) {
                    return status;
                }
            }
        }
        return null;
    }

    private void setupDefaultStatusMapping() {
        addStatusMapping(Status.DOWN, HttpStatus.SERVICE_UNAVAILABLE);
        addStatusMapping(Status.OUT_OF_SERVICE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void addStatusMapping(Status status, HttpStatus httpStatus) {
        Assert.notNull(status, "Status must not be null");
        Assert.notNull(httpStatus, "HttpStatus must not be null");
        addStatusMapping(status.getCode(), httpStatus);
    }

    private void addStatusMapping(String statusCode, HttpStatus httpStatus) {
        Assert.notNull(statusCode, "StatusCode must not be null");
        Assert.notNull(httpStatus, "HttpStatus must not be null");
        this.statusMapping.put(statusCode, httpStatus);
    }
}