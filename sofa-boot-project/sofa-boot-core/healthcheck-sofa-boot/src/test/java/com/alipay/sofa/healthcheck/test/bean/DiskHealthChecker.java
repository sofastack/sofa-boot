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
package com.alipay.sofa.healthcheck.test.bean;

import org.springframework.boot.actuate.health.Health;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alipay.sofa.healthcheck.core.HealthChecker;

/**
 * @author qilong.zql
 * @since 3.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE - 9)
public class DiskHealthChecker implements HealthChecker {

    private final boolean health;

    public DiskHealthChecker() {
        this.health = true;
    }

    public DiskHealthChecker(boolean health) {
        this.health = health;
    }

    @Override
    public Health isHealthy() {
        if (health) {
            return Health.up().withDetail("disk", "disk is ok").build();
        } else {
            return Health.down().withDetail("disk", "disk is bad").build();
        }

    }

    @Override
    public String getComponentName() {
        return "diskHealthChecker";
    }

    @Override
    public int getRetryCount() {
        return 0;
    }

    @Override
    public long getRetryTimeInterval() {
        return 0;
    }

    @Override
    public boolean isStrictCheck() {
        return false;
    }
}