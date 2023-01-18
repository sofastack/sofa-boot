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

import org.springframework.util.StringUtils;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * Implementation of {@link HealthChecker} used to check {@link ComponentInfo} health.
 *
 * @author xuanbei 18/5/15
 * @author huzijie
 */
public class ComponentHealthChecker implements HealthChecker {

    public static final String       COMPONENT_NAME = "components";

    private final SofaRuntimeContext sofaRuntimeContext;

    public ComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public Health isHealthy() {
        boolean allPassed = true;
        Health.Builder builder = new Health.Builder();

        for (ComponentInfo componentInfo : sofaRuntimeContext.getComponentManager().getComponents()) {
            HealthResult healthy = componentInfo.isHealthy();
            String healthReport = healthy.getHealthReport();
            if (!healthy.isHealthy()) {
                allPassed = false;
                builder.withDetail(healthy.getHealthName(),
                    StringUtils.hasText(healthReport) ? healthReport : "failed");
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
        return COMPONENT_NAME;
    }

    @Override
    public int getRetryCount() {
        return 20;
    }

    @Override
    public long getRetryTimeInterval() {
        return 1000;
    }

    @Override
    public boolean isStrictCheck() {
        return true;
    }

    @Override
    public int getTimeout() {
        return 10 * 1000;
    }
}
