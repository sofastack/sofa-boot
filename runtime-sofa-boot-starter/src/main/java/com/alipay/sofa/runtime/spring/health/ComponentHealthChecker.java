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

import com.alipay.boot.sofarpc.configuration.Slite2Configuration;
import com.alipay.sofa.healthcheck.core.DefaultHealthChecker;
import com.alipay.sofa.runtime.spi.SofaFrameworkHolder;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

/**
 * @author xuanbei 18/3/17
 */
@Component
public class ComponentHealthChecker extends DefaultHealthChecker {

    @Override
    public Health isHealthy() {
        boolean allPassed = true;
        Health.Builder builder = new Health.Builder();
        for (ComponentInfo componentInfo : SofaFrameworkHolder.getSofaFramework()
            .getSofaRuntimeContext(Slite2Configuration.getAppName()).getComponentManager().getComponents()) {
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
        return "RUNTIME-COMPONENT";
    }
}
