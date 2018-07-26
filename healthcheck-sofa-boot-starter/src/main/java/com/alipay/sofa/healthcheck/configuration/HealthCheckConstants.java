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
package com.alipay.sofa.healthcheck.configuration;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthCheckConstants {

    /**
     * skip all readiness check
     */
    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK       = "com.alipay.sofa.healthcheck.skip.all";

    /**
     * skip all {@link com.alipay.sofa.healthcheck.core.HealthChecker} readiness check
     */
    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK = "com.alipay.sofa.healthcheck.skip.component";

    /**
     * skip all {@link org.springframework.boot.actuate.health.HealthIndicator} readiness check
     */
    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK = "com.alipay.sofa.healthcheck.skip.indicator";

    /**
     * health check logging space
     */
    public static final String SOFABOOT_HEALTH_LOG_SPACE            = "com.alipay.sofa.healthcheck";

    /**
     * readiness health check endpoint name
     */
    public static final String READINESS_CHECK_ENDPOINT_NAME        = "sofaboot_health_readiness";
}
