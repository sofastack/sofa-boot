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

import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class MultiApplicationHealthChecker implements HealthIndicator {

    @Override
    public Health health() {
        boolean allPassed = true;
        Health.Builder builder = new Health.Builder();
        for (SofaRuntimeManager sofaRuntimeManager : SofaFramework.getRuntimeSet()) {
            if (!sofaRuntimeManager.isHealthCheckPassed()) {
                allPassed = false;
                builder.withDetail(
                    String.format("Biz: %s health check",
                        DynamicJvmServiceProxyFinder.getBiz(sofaRuntimeManager).getIdentity()),
                    "failed");
            } else {
                builder.withDetail(
                    String.format("Biz: %s health check",
                        DynamicJvmServiceProxyFinder.getBiz(sofaRuntimeManager).getIdentity()),
                    "passed");
            }
        }
        if (allPassed) {
            return builder.up().build();
        } else {
            return builder.down().build();
        }
    }
}