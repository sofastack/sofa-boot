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

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.lang.Nullable;

/**
 * {@link EndpointWebExtension @EndpointWebExtension} for the {@link ReadinessEndpoint}.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 3.0.0
 */
@EndpointWebExtension(endpoint = ReadinessEndpoint.class)
public class ReadinessEndpointWebExtension {

    private final ReadinessEndpoint    delegate;

    private final HttpCodeStatusMapper statusMapper;

    public ReadinessEndpointWebExtension(ReadinessEndpoint delegate,
                                         HttpCodeStatusMapper statusMapper) {
        this.delegate = delegate;
        this.statusMapper = statusMapper;
    }

    @ReadOperation
    public WebEndpointResponse<Health> getHealth(@Nullable String showDetail) {
        Health result = delegate.health(showDetail);
        return new WebEndpointResponse<>(result, statusMapper.getStatusCode(result.getStatus()));
    }
}
