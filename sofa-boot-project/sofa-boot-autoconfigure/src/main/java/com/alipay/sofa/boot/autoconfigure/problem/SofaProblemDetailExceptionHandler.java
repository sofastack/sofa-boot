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
package com.alipay.sofa.boot.autoconfigure.problem;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.exception.SofaRpcRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * MVC exception handler that renders SOFA exceptions as RFC 7807 problem details.
 *
 * @author OpenAI
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SofaProblemDetailExceptionHandler {

    private final SofaProblemDetailFactory factory;

    public SofaProblemDetailExceptionHandler(SofaProblemDetailFactory factory) {
        this.factory = factory;
    }

    @ExceptionHandler(SofaRpcException.class)
    public ResponseEntity<ProblemDetail> handleSofaRpcException(SofaRpcException exception,
                                                                HttpServletRequest request,
                                                                Locale locale) {
        return buildResponse(factory.create(exception, request.getRequestURI(), locale));
    }

    @ExceptionHandler(SofaRpcRuntimeException.class)
    public ResponseEntity<ProblemDetail> handleSofaRpcRuntimeException(SofaRpcRuntimeException exception,
                                                                       HttpServletRequest request,
                                                                       Locale locale) {
        return buildResponse(factory.create(exception, request.getRequestURI(), locale));
    }

    @ExceptionHandler(SofaBootRpcRuntimeException.class)
    public ResponseEntity<ProblemDetail> handleSofaBootRpcRuntimeException(SofaBootRpcRuntimeException exception,
                                                                           HttpServletRequest request,
                                                                           Locale locale) {
        return buildResponse(factory.create(exception, request.getRequestURI(), locale));
    }

    private ResponseEntity<ProblemDetail> buildResponse(ProblemDetail problemDetail) {
        return ResponseEntity.status(problemDetail.getStatus())
            .contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problemDetail);
    }
}
