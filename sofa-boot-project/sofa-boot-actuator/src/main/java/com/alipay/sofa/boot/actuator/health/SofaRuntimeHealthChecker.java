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

import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.health.RuntimeHealthChecker;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@Component
public class SofaRuntimeHealthChecker implements RuntimeHealthChecker {

    private final List<HealthIndicator>  healthIndicators;
    private final ReadinessCheckListener readinessCheckListener;

    public SofaRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext,
                                    List<HealthIndicator> healthIndicators,
                                    ReadinessCheckListener readinessCheckListener) {
        sofaRuntimeContext.getSofaRuntimeManager().registerRuntimeHealthChecker(this);
        this.healthIndicators = healthIndicators;
        this.readinessCheckListener = readinessCheckListener;
    }

    @Override
    public boolean isReadinessHealth() {
        return readinessCheckListener.aggregateReadinessHealth().getStatus().equals(Status.UP);
    }

    @Override
    public boolean isLivenessHealth() {
        for (HealthIndicator healthIndicator : healthIndicators) {
            if (healthIndicator.getClass().getName()
                .equals("com.alipay.sofa.boot.actuator.health.MultiApplicationHealthIndicator")) {
                continue;
            }
            if (healthIndicator.health().getStatus().equals(Status.DOWN)) {
                return false;
            }
        }
        return true;
    }
}
