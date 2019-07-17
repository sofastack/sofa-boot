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
import org.springframework.context.ApplicationContext;

import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class MiddlewareHealthCheckCallback implements ReadinessCheckCallback {

    private boolean health;

    public MiddlewareHealthCheckCallback(boolean health) {
        this.health = health;
    }

    @Override
    public Health onHealthy(ApplicationContext applicationContext) {
        if (health) {
            return Health.up().withDetail("server", "server is ok").build();
        } else {
            return Health.down().withDetail("server", "server is bad").build();
        }
    }
}