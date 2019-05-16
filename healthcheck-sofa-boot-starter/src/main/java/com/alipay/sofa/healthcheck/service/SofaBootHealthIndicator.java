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

import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.healthcheck.core.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * The liveness health check.
 *
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public class SofaBootHealthIndicator implements HealthIndicator {

    private static final String    CHECK_RESULT_PREFIX = "Middleware";

    @Autowired
    private HealthCheckerProcessor healthCheckerProcessor;

    @Autowired
    private ReadinessCheckListener readinessCheckListener;

    @Override
    public Health health() {
        Assert.isTrue(readinessCheckListener.isReadinessCheckFinish(),
            HealthCheckConstants.SOFABOOT_HEALTH_CHECK_NOT_READY_MSG);

        return checkHealth();
    }

    public Health checkHealth() {
        Map<String, Health> healths = new HashMap<>();
        boolean checkSuccessful = healthCheckerProcessor.livenessHealthCheck(healths);

        if (checkSuccessful) {
            return Health.up().withDetail(CHECK_RESULT_PREFIX, healths).build();
        } else {
            return Health.down().withDetail(CHECK_RESULT_PREFIX, healths).build();
        }
    }
}