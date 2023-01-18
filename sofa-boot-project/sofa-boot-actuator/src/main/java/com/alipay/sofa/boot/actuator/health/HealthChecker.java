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

import org.springframework.boot.actuate.health.Health;

/**
 * Strategy interface used to contribute {@link Health} to the results returned from the {@link ReadinessEndpoint}.
 *
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public interface HealthChecker {

    /**
     * HealthCheck information.
     * @return health result.
     */
    Health isHealthy();

    /**
     * HealthChecker name
     * @return component name
     */
    String getComponentName();

    /**
     * The number of retries after failure.
     * @return retry times.
     */
    default int getRetryCount() {
        return 0;
    }

    /**
     * The time interval for each retry after failure.
     * @return retry interval, unit milliseconds.
     */
    default long getRetryTimeInterval(){
        return 0;
    }

    /**
     * Is it strictly checked?
     * If true, the final check result of isHealthy() is used as the result of the component's check.
     * If false, the final result of the component is considered to be healthy, but the exception log is printed.
     * @return whether stric check.
     */
    default boolean isStrictCheck() {
        return true;
    }

    /**
     * The timeout in milliseconds.
     * If less than or equal to 0, the property {@literal com.alipay.sofa.healthcheck.component.timeout} is used as timeout.
     * @return health check timeout, unit milliseconds.
     */
    default int getTimeout() {
        return 0;
    }
}
