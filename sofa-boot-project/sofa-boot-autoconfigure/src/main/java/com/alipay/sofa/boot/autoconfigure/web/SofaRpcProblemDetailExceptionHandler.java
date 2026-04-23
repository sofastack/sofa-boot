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

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

/**
 * ProblemDetail mapping for SOFA RPC exceptions.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SofaRpcProblemDetailExceptionHandler extends SofaProblemDetailExceptionHandler {

    public SofaRpcProblemDetailExceptionHandler(SofaProblemDetailProperties properties,
                                                Environment environment) {
        super(properties, environment);
    }

    @ExceptionHandler(SofaBootRpcRuntimeException.class)
    public org.springframework.http.ResponseEntity<Object> handleSofaBootRpcRuntimeException(SofaBootRpcRuntimeException ex,
                                                                                             WebRequest request) {
        RpcProblemDescriptor descriptor = classify(ex);
        String detail = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : descriptor.detail;
        return createSofaResponseEntity(ex, descriptor.status, detail, descriptor.type,
            descriptor.title, request);
    }

    private RpcProblemDescriptor classify(SofaBootRpcRuntimeException ex) {
        SofaRpcException rpcCause = findCause(ex);
        if (rpcCause != null && isRemoteUnavailable(rpcCause.getErrorType())) {
            return new RpcProblemDescriptor(HttpStatus.SERVICE_UNAVAILABLE, RPC_EXCEPTION_TYPE,
                "SOFA RPC Error", "RPC service call failed");
        }
        return new RpcProblemDescriptor(HttpStatus.INTERNAL_SERVER_ERROR, RPC_CONFIGURATION_TYPE,
            "SOFA RPC Configuration Error", "SOFA RPC configuration is invalid");
    }

    private SofaRpcException findCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SofaRpcException sofaRpcException) {
                return sofaRpcException;
            }
            current = current.getCause();
        }
        return null;
    }

    private boolean isRemoteUnavailable(int errorType) {
        return errorType == RpcErrorType.SERVER_BUSY || errorType == RpcErrorType.SERVER_CLOSED
               || errorType == RpcErrorType.SERVER_NOT_FOUND_INVOKER
               || errorType == RpcErrorType.SERVER_NETWORK
               || errorType == RpcErrorType.CLIENT_TIMEOUT
               || errorType == RpcErrorType.CLIENT_NETWORK;
    }

    private static final class RpcProblemDescriptor {

        private final HttpStatus status;
        private final URI        type;
        private final String     title;
        private final String     detail;

        private RpcProblemDescriptor(HttpStatus status, URI type, String title, String detail) {
            this.status = status;
            this.type = type;
            this.title = title;
            this.detail = detail;
        }
    }
}
