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

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.lang.Nullable;

/**
 * {@link Endpoint @Endpoint} to expose application readiness health information.
 *
 * @author liangen
 * @author qilong.zql
 * @author huzijie
 * @version 2.3.0
 */
@Endpoint(id = "readiness")
public class ReadinessEndpoint {

    private final ReadinessCheckListener readinessCheckListener;

    public ReadinessEndpoint(ReadinessCheckListener readinessCheckListener) {
        this.readinessCheckListener = readinessCheckListener;
    }

    @ReadOperation
    public Health health(@Nullable String showDetail) {
        Health health = readinessCheckListener.aggregateReadinessHealth();
        if (showDetail == null || Boolean.parseBoolean(showDetail)) {
            return health;
        }
        return new Health.Builder(health.getStatus()).build();
    }
}
