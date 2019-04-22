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
package com.alipay.sofa.runtime.spring.health;

import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * Abstract Component Health Checker
 *
 * @author xuanbei 18/5/15
 */
public abstract class AbstractComponentHealthChecker {
    private SofaRuntimeContext sofaRuntimeContext;

    public AbstractComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    public Health doHealthCheck() {
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
}
