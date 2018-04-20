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
package com.alipay.sofa.healthcheck.bean;

import com.alipay.sofa.healthcheck.startup.SofaBootMiddlewareAfterReadinessCheckCallback;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: AfterReadinessCheckCallbackA.java, v 0.1 2018年03月11日 下午2:40 liangen Exp $
 */
public class AfterReadinessCheckCallbackA implements SofaBootMiddlewareAfterReadinessCheckCallback {

    private boolean health;

    public AfterReadinessCheckCallbackA(boolean health) {
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