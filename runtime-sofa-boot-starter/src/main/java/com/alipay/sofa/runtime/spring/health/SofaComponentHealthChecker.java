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

import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.health.Health;

/**
 * component health checker which implements ${@link com.alipay.sofa.healthcheck.core.HealthChecker}
 *
 * @author xuanbei 18/3/17
 */
public class SofaComponentHealthChecker extends AbstractComponentHealthChecker implements
                                                                              HealthChecker {
    public SofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        super(sofaRuntimeContext);
    }

    @Override
    public Health isHealthy() {
        return doHealthCheck();
    }

    @Override
    public String getComponentName() {
        return "RUNTIME-COMPONENT";
    }

    @Override
    public int getRetryCount() {
        return 0;
    }

    @Override
    public long getRetryTimeInterval() {
        return 0;
    }

    @Override
    public boolean isStrictCheck() {
        return true;
    }
}
