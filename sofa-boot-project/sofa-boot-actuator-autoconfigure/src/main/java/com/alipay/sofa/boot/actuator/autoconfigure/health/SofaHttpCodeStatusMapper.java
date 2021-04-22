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
package com.alipay.sofa.boot.actuator.autoconfigure.health;

import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.SimpleHttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huzijie
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/3/22
 */
public class SofaHttpCodeStatusMapper implements HttpCodeStatusMapper {
    private SimpleHttpCodeStatusMapper statusMapper;

    public SofaHttpCodeStatusMapper(HealthEndpointProperties healthEndpointProperties) {
        Map<String, Integer> mapping = new HashMap<>(8);
        if (healthEndpointProperties.getStatus().getHttpMapping() != null) {
            mapping.putAll(healthEndpointProperties.getStatus().getHttpMapping());
        }
        mapping.put(Status.DOWN.getCode(), WebEndpointResponse.STATUS_SERVICE_UNAVAILABLE);
        mapping.put(Status.OUT_OF_SERVICE.getCode(),
            WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);
        mapping.put(Status.UNKNOWN.getCode(), WebEndpointResponse.STATUS_INTERNAL_SERVER_ERROR);

        statusMapper = new SimpleHttpCodeStatusMapper(mapping);
    }

    @Override
    public int getStatusCode(Status status) {
        return statusMapper.getStatusCode(status);
    }
}
