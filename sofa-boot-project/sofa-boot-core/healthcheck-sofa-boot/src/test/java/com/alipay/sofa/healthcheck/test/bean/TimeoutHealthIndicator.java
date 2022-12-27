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
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * TimeOutHealthIndicator
 *
 * @author xunfang
 * @version TimeOutHealthIndicator.java, v 0.1 2022/12/27
 */
public class TimeoutHealthIndicator implements HealthIndicator {
    private boolean health;

    public TimeoutHealthIndicator(boolean health) {
        this.health = health;
    }

    @Override
    public Health health() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (health) {
            return Health.up().withDetail("timeout", "timeoutHealthIndicator is ok").build();
        } else {
            return Health.down().withDetail("timeout", "timeoutHealthIndicator is bad").build();
        }
    }
}
