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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;

/**
 * ComponentInfo Health Checker
 * {@link ComponentInfo}
 *
 * @author xuanbei 18/5/15
 */
public class ComponentHealthChecker implements HealthChecker {
    @Value("${" + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_COUNT + ":"
           + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT + "}")
    private int                retryCount;

    @Value("${" + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_INTERVAL + ":"
           + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL + "}")
    private int                retryInterval;

    @Value("${" + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_STRICT_ENABLED + ":"
           + SofaBootConstants.SOFABOOT_COMPONENT_CHECK_STRICT_DEFAULT_ENABLED + "}")
    private boolean            strictCheck;

    private SofaRuntimeContext sofaRuntimeContext;

    public ComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public Health isHealthy() {
        boolean allPassed = true;
        Health.Builder builder = new Health.Builder();
        for (ComponentInfo componentInfo : sofaRuntimeContext.getComponentManager().getComponents()) {
            HealthResult healthy = componentInfo.isHealthy();
            if (healthy.isHealthy()) {
                builder.withDetail(healthy.getHealthName(), "passed");
            } else {
                builder.withDetail(healthy.getHealthName(), healthy.getHealthReport());
                allPassed = false;
            }
        }

        if (allPassed) {
            return builder.status(Status.UP).build();
        } else {
            return builder.status(Status.DOWN).build();
        }
    }

    @Override
    public String getComponentName() {
        return "SOFABoot-Components";
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public long getRetryTimeInterval() {
        return retryInterval;
    }

    @Override
    public boolean isStrictCheck() {
        return strictCheck;
    }
}
