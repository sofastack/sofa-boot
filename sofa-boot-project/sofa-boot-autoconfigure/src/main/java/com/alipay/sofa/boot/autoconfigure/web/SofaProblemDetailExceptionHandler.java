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
package com.alipay.sofa.boot.autoconfigure.web;

import org.springframework.core.env.Environment;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base MVC exception handler that customizes framework-generated ProblemDetail
 * responses with SOFA-specific metadata.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SofaProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    static final URI                          ABOUT_BLANK            = URI.create("about:blank");
    static final URI                          RUNTIME_EXCEPTION_TYPE = URI
                                                                         .create("https://sofastack.io/errors/runtime-exception");
    static final URI                          RPC_EXCEPTION_TYPE     = URI
                                                                         .create("https://sofastack.io/errors/rpc-exception");
    static final URI                          RPC_CONFIGURATION_TYPE = URI
                                                                         .create("https://sofastack.io/errors/rpc-configuration-exception");

    private static final Pattern              ERROR_CODE_PATTERN     = Pattern
                                                                         .compile("(SOFA-BOOT-\\d{2}-\\d{5})");

    private final SofaProblemDetailProperties properties;
    private final String                      applicationName;

    public SofaProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                             Environment environment) {
        this.properties = properties;
        this.applicationName = environment.getProperty("spring.application.name");
    }

    @Override
    protected org.springframework.http.ResponseEntity<Object> createResponseEntity(@Nullable Object body,
                                                                                   HttpHeaders headers,
                                                                                   HttpStatusCode statusCode,
                                                                                   WebRequest request) {
        if (body instanceof ProblemDetail problemDetail) {
            customize(problemDetail, request, null);
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

    protected org.springframework.http.ResponseEntity<Object> createSofaResponseEntity(Exception ex,
                                                                                       HttpStatus status,
                                                                                       String detail,
                                                                                       URI type,
                                                                                       String title,
                                                                                       WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(ex, status, detail, null, null, request);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        customize(problemDetail, request, ex);
        return super.createResponseEntity(problemDetail, new HttpHeaders(), status, request);
    }

    void customize(ProblemDetail problemDetail, WebRequest request, @Nullable Throwable throwable) {
        if (problemDetail.getType() == null || ABOUT_BLANK.equals(problemDetail.getType())) {
            problemDetail.setType(this.properties.getDefaultType());
        }

        if (problemDetail.getInstance() == null) {
            URI instance = resolveInstance(request);
            if (instance != null) {
                problemDetail.setInstance(instance);
            }
        }

        if (this.properties.isIncludeServiceInfo() && StringUtils.hasText(this.applicationName)
            && !hasProperty(problemDetail, "service")) {
            problemDetail.setProperty("service", this.applicationName);
        }

        if (throwable == null) {
            return;
        }

        String errorCode = extractErrorCode(throwable);
        if (StringUtils.hasText(errorCode) && !hasProperty(problemDetail, "errorCode")) {
            problemDetail.setProperty("errorCode", errorCode);
        }

        if (this.properties.isIncludeStackTrace() && !hasProperty(problemDetail, "stackTrace")) {
            problemDetail.setProperty("stackTrace", toStackTrace(throwable));
        }
    }

    private boolean hasProperty(ProblemDetail problemDetail, String propertyName) {
        Map<String, Object> properties = problemDetail.getProperties();
        return properties != null && properties.containsKey(propertyName);
    }

    @Nullable
    private URI resolveInstance(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            String requestUri = servletWebRequest.getRequest().getRequestURI();
            if (StringUtils.hasText(requestUri)) {
                return URI.create(requestUri);
            }
        }
        return null;
    }

    @Nullable
    private String extractErrorCode(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (StringUtils.hasText(message)) {
                Matcher matcher = ERROR_CODE_PATTERN.matcher(message);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            current = current.getCause();
        }
        return null;
    }

    private String toStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }
}
