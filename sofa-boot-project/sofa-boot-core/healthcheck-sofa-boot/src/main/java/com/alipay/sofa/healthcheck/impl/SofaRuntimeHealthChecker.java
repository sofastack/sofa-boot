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
package com.alipay.sofa.healthcheck.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import com.alipay.sofa.boot.health.RuntimeHealthChecker;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaRuntimeHealthChecker implements RuntimeHealthChecker {
    @Autowired
    private List<HealthIndicator>  healthIndicators;

    @Autowired
    private ReadinessCheckListener readinessCheckListener;

    public SofaRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        sofaRuntimeContext.getSofaRuntimeManager().registerRuntimeHealthChecker(this);
    }

    @Override
    public boolean isReadinessHealth() {
        return readinessCheckListener.aggregateReadinessHealth().getStatus().equals(Status.UP);
    }

    @Override
    public boolean isLivenessHealth() {
        for (HealthIndicator healthIndicator : healthIndicators) {
            if (healthIndicator.health().getStatus().equals(Status.DOWN)) {
                return false;
            }
        }
        return true;
    }
}