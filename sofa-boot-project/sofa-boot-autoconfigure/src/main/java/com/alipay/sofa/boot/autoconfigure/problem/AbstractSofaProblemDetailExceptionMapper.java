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

import org.springframework.http.ProblemDetail;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.Locale;

/**
 * Base JAX-RS exception mapper for SOFA problem detail responses.
 *
 * @author OpenAI
 */
public abstract class AbstractSofaProblemDetailExceptionMapper<T extends Throwable>
                                                                                    implements
                                                                                    ExceptionMapper<T> {

    private final SofaProblemDetailFactory factory;

    @Context
    private UriInfo                        uriInfo;

    @Context
    private HttpHeaders                    headers;

    protected AbstractSofaProblemDetailExceptionMapper(SofaProblemDetailFactory factory) {
        this.factory = factory;
    }

    @Override
    public Response toResponse(T exception) {
        ProblemDetail problemDetail = factory.create(exception, resolveInstancePath(),
            resolveLocale());
        return Response.status(problemDetail.getStatus())
            .type(MediaType.valueOf("application/problem+json"))
            .entity(factory.render(problemDetail)).build();
    }

    private String resolveInstancePath() {
        if (uriInfo == null || uriInfo.getRequestUri() == null) {
            return null;
        }
        String path = uriInfo.getRequestUri().getPath();
        if (!StringUtils.hasText(path)) {
            return null;
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private Locale resolveLocale() {
        if (headers == null) {
            return Locale.getDefault();
        }
        List<Locale> acceptableLanguages = headers.getAcceptableLanguages();
        return acceptableLanguages.isEmpty() ? Locale.getDefault() : acceptableLanguages.get(0);
    }
}
