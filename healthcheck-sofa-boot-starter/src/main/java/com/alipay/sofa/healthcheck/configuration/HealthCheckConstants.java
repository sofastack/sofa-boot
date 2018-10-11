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
    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK                  = "com.alipay.sofa.healthcheck.skip.all";

    /**
     * skip all {@literal com.alipay.sofa.healthcheck.core.HealthChecker} readiness check
     */
    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK            = "com.alipay.sofa.healthcheck.skip.component";

    /**
     * skip all {@literal org.springframework.boot.actuate.health.HealthIndicator} readiness check
     */
    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK            = "com.alipay.sofa.healthcheck.skip.indicator";

    /**
     * health check logging space
     */
    public static final String SOFABOOT_HEALTH_LOG_SPACE                       = "com.alipay.sofa.healthcheck";

    /**
     * readiness health check endpoint name
     */
    public static final String READINESS_CHECK_ENDPOINT_NAME                   = "sofaboot_health_readiness";

    /**
     * {@literal com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker} retry count config.
     */
    public static final String SOFABOOT_COMPONENT_CHECK_RETRY_COUNT            = "com.alipay.sofa.healthcheck.component.check.retry.count";

    /**
     * default {@literal com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker} retry count value.
     */
    public static final int    SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT    = 20;

    /**
     * {@literal com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker} retry time interval config.
     */
    public static final String SOFABOOT_COMPONENT_CHECK_RETRY_INTERVAL         = "com.alipay.sofa.healthcheck.component.check.retry.interval";

    /**
     * default {@literal com.alipay.sofa.runtime.spring.health.SofaComponentHealthChecker} retry time interval value.
     */
    public static final long   SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL = 1000;

    /**
     * {@literal com.alipay.sofa.isle.spring.health.SofaModuleHealthChecker} retry count config.
     */
    public static final String SOFABOOT_MODULE_CHECK_RETRY_COUNT               = "com.alipay.sofa.healthcheck.module.check.retry.count";

    /**
     * default {@literal com.alipay.sofa.isle.spring.health.SofaModuleHealthChecker} retry count value.
     */
    public static final int    SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT       = 0;

    /**
     * {@literal com.alipay.sofa.isle.spring.health.SofaModuleHealthChecker} retry time interval config.
     */
    public static final String SOFABOOT_MODULE_CHECK_RETRY_INTERVAL            = "com.alipay.sofa.healthcheck.module.check.retry.interval";

    /**
     * default {@literal com.alipay.sofa.isle.spring.health.SofaModuleHealthChecker} retry time interval value.
     */
    public static final long   SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL    = 1000;
}
