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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * ProblemDetail mapping for SOFA runtime exceptions.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SofaRuntimeProblemDetailExceptionHandler extends SofaProblemDetailExceptionHandler {

    public SofaRuntimeProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                                    Environment environment) {
        super(properties, environment);
    }

    @ExceptionHandler(ServiceRuntimeException.class)
    public org.springframework.http.ResponseEntity<Object> handleServiceRuntimeException(ServiceRuntimeException ex,
                                                                                         WebRequest request) {
        String detail = StringUtils.hasText(ex.getMessage()) ? ex.getMessage()
            : "SOFA runtime execution failed";
        return createSofaResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, detail,
            RUNTIME_EXCEPTION_TYPE, "SOFA Runtime Error", request);
    }
}
