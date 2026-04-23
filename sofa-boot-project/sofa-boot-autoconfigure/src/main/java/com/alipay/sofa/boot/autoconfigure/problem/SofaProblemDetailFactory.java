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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.exception.SofaRpcRuntimeException;
import org.springframework.context.MessageSource;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Factory for building RFC 7807 problem detail responses for SOFA exceptions.
 *
 * @author OpenAI
 */
public class SofaProblemDetailFactory {

    private static final String               TITLE_CODE_PREFIX  = "sofa.boot.problem-detail.title.";

    private static final String               DETAIL_CODE_PREFIX = "sofa.boot.problem-detail.detail.";

    private final SofaProblemDetailProperties properties;

    @Nullable
    private final MessageSource               messageSource;

    private final Environment                 environment;

    public SofaProblemDetailFactory(SofaProblemDetailProperties properties,
                                    @Nullable MessageSource messageSource, Environment environment) {
        this.properties = properties;
        this.messageSource = messageSource;
        this.environment = environment;
    }

    public ProblemDetail create(Throwable throwable, @Nullable String instancePath,
                                @Nullable Locale locale) {
        Locale targetLocale = locale != null ? locale : Locale.getDefault();
        if (throwable instanceof SofaRpcException) {
            return createFromSofaRpcException((SofaRpcException) throwable, instancePath,
                targetLocale);
        }
        if (throwable instanceof SofaRpcRuntimeException) {
            return createGeneric(throwable, HttpStatus.INTERNAL_SERVER_ERROR, "sofa-rpc/runtime",
                "sofa-rpc.runtime", "SOFA RPC runtime error", instancePath, targetLocale);
        }
        if (throwable instanceof SofaBootRpcRuntimeException) {
            return createGeneric(throwable, HttpStatus.INTERNAL_SERVER_ERROR,
                "sofa-rpc/boot-runtime", "sofa-rpc.boot-runtime", "SOFABoot RPC runtime error",
                instancePath, targetLocale);
        }
        return createGeneric(throwable, HttpStatus.INTERNAL_SERVER_ERROR, null, "sofa.default",
            "Unexpected server error", instancePath, targetLocale);
    }

    public Map<String, Object> render(ProblemDetail problemDetail) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        if (problemDetail.getType() != null) {
            body.put("type", problemDetail.getType().toString());
        }
        if (StringUtils.hasText(problemDetail.getTitle())) {
            body.put("title", problemDetail.getTitle());
        }
        body.put("status", problemDetail.getStatus());
        if (StringUtils.hasText(problemDetail.getDetail())) {
            body.put("detail", problemDetail.getDetail());
        }
        if (problemDetail.getInstance() != null) {
            body.put("instance", problemDetail.getInstance().toString());
        }
        if (!ObjectUtils.isEmpty(problemDetail.getProperties())) {
            body.putAll(problemDetail.getProperties());
        }
        return body;
    }

    private ProblemDetail createFromSofaRpcException(SofaRpcException exception,
                                                     @Nullable String instancePath, Locale locale) {
        String errorCode = resolveRpcErrorCode(exception.getErrorType());
        HttpStatus status = resolveStatus(exception.getErrorType());
        String messageKey = "sofa-rpc." + errorCode;
        String defaultTitle = defaultTitle(exception.getErrorType());
        ProblemDetail problemDetail = createProblemDetail(
            exception,
            status,
            resolveTitle(messageKey, defaultTitle, locale),
            resolveDetail(messageKey, exception.getMessage(),
                defaultDetail(exception, defaultTitle), locale), instancePath);
        problemDetail.setType(resolveType("sofa-rpc/" + errorCode));
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("errorType", exception.getErrorType());
        appendCommonProperties(problemDetail, exception);
        return problemDetail;
    }

    private ProblemDetail createGeneric(Throwable throwable, HttpStatus status,
                                        @Nullable String typeId, String messageKey,
                                        String defaultTitle, @Nullable String instancePath,
                                        Locale locale) {
        ProblemDetail problemDetail = createProblemDetail(
            throwable,
            status,
            resolveTitle(messageKey, defaultTitle, locale),
            resolveDetail(messageKey, throwable.getMessage(),
                defaultDetail(throwable, defaultTitle), locale), instancePath);
        problemDetail.setType(typeId != null ? resolveType(typeId) : properties.getDefaultType());
        problemDetail.setProperty("errorCode", messageKey.replace('.', '-'));
        appendCommonProperties(problemDetail, throwable);
        return problemDetail;
    }

    private ProblemDetail createProblemDetail(Throwable throwable, HttpStatus status, String title,
                                              String detail, @Nullable String instancePath) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        if (StringUtils.hasText(instancePath)) {
            problemDetail.setInstance(URI.create(instancePath));
        }
        if (properties.isIncludeServiceInfo()) {
            String serviceName = environment.getProperty(SofaBootConstants.APP_NAME_KEY);
            if (StringUtils.hasText(serviceName)) {
                problemDetail.setProperty("service", serviceName);
            }
        }
        if (properties.isIncludeStackTrace()) {
            problemDetail.setProperty("stackTrace", buildStackTrace(throwable));
        }
        return problemDetail;
    }

    private void appendCommonProperties(ProblemDetail problemDetail, Throwable throwable) {
        problemDetail.setProperty("exception", throwable.getClass().getName());
    }

    private URI resolveType(String problemId) {
        URI typeBaseUri = properties.getTypeBaseUri();
        if (typeBaseUri == null) {
            return properties.getDefaultType();
        }
        String baseUri = typeBaseUri.toString();
        if (!baseUri.endsWith("/")) {
            baseUri = baseUri + "/";
        }
        return URI.create(baseUri + problemId);
    }

    private String resolveTitle(String messageKey, String defaultTitle, Locale locale) {
        return resolveMessage(TITLE_CODE_PREFIX + messageKey, null, defaultTitle, locale);
    }

    private String resolveDetail(String messageKey, @Nullable String exceptionMessage,
                                 String defaultDetail, Locale locale) {
        return resolveMessage(DETAIL_CODE_PREFIX + messageKey, new Object[] { exceptionMessage },
            defaultDetail, locale);
    }

    private String resolveMessage(String code, @Nullable Object[] args, String defaultMessage,
                                  Locale locale) {
        if (messageSource == null) {
            return defaultMessage;
        }
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    private HttpStatus resolveStatus(int errorType) {
        switch (errorType) {
            case RpcErrorType.CLIENT_TIMEOUT:
                return HttpStatus.GATEWAY_TIMEOUT;
            case RpcErrorType.SERVER_BUSY:
            case RpcErrorType.SERVER_CLOSED:
            case RpcErrorType.SERVER_NOT_FOUND_INVOKER:
            case RpcErrorType.SERVER_NETWORK:
            case RpcErrorType.CLIENT_ROUTER:
            case RpcErrorType.CLIENT_NETWORK:
                return HttpStatus.SERVICE_UNAVAILABLE;
            case RpcErrorType.SERVER_SERIALIZE:
            case RpcErrorType.SERVER_DESERIALIZE:
            case RpcErrorType.CLIENT_SERIALIZE:
            case RpcErrorType.CLIENT_DESERIALIZE:
                return HttpStatus.BAD_GATEWAY;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String resolveRpcErrorCode(int errorType) {
        switch (errorType) {
            case RpcErrorType.SERVER_BUSY:
                return "server-busy";
            case RpcErrorType.SERVER_CLOSED:
                return "server-closed";
            case RpcErrorType.SERVER_NOT_FOUND_INVOKER:
                return "server-not-found-invoker";
            case RpcErrorType.SERVER_SERIALIZE:
                return "server-serialize";
            case RpcErrorType.SERVER_DESERIALIZE:
                return "server-deserialize";
            case RpcErrorType.SERVER_NETWORK:
                return "server-network";
            case RpcErrorType.SERVER_BIZ:
                return "server-biz";
            case RpcErrorType.SERVER_FILTER:
                return "server-filter";
            case RpcErrorType.SERVER_UNDECLARED_ERROR:
                return "server-undeclared-error";
            case RpcErrorType.CLIENT_TIMEOUT:
                return "client-timeout";
            case RpcErrorType.CLIENT_ROUTER:
                return "client-router";
            case RpcErrorType.CLIENT_SERIALIZE:
                return "client-serialize";
            case RpcErrorType.CLIENT_DESERIALIZE:
                return "client-deserialize";
            case RpcErrorType.CLIENT_NETWORK:
                return "client-network";
            case RpcErrorType.CLIENT_CALL_TYPE:
                return "client-call-type";
            case RpcErrorType.CLIENT_FILTER:
                return "client-filter";
            case RpcErrorType.CLIENT_UNDECLARED_ERROR:
                return "client-undeclared-error";
            default:
                return "unknown";
        }
    }

    private String defaultTitle(int errorType) {
        switch (errorType) {
            case RpcErrorType.CLIENT_TIMEOUT:
                return "RPC request timed out";
            case RpcErrorType.SERVER_BUSY:
            case RpcErrorType.SERVER_CLOSED:
            case RpcErrorType.SERVER_NOT_FOUND_INVOKER:
            case RpcErrorType.SERVER_NETWORK:
            case RpcErrorType.CLIENT_ROUTER:
            case RpcErrorType.CLIENT_NETWORK:
                return "RPC service unavailable";
            case RpcErrorType.SERVER_SERIALIZE:
            case RpcErrorType.SERVER_DESERIALIZE:
            case RpcErrorType.CLIENT_SERIALIZE:
            case RpcErrorType.CLIENT_DESERIALIZE:
                return "RPC message conversion failed";
            default:
                return "RPC invocation failed";
        }
    }

    private String defaultDetail(Throwable throwable, String defaultTitle) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(throwable);
        String message = rootCause != null ? rootCause.getMessage() : throwable.getMessage();
        return StringUtils.hasText(message) ? message : defaultTitle;
    }

    private String buildStackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
