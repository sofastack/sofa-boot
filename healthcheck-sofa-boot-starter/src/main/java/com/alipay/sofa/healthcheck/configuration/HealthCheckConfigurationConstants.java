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
 * Created by liangen on 17/8/6.
 */
public class HealthCheckConfigurationConstants {

    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK                   = "com.alipay.sofa.healthcheck.skip.all";
    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK_UNDERLINE         = "com_alipay_sofa_healthcheck_skip_all";

    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK             = "com.alipay.sofa.healthcheck.skip.component";
    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK_UNDERLINE   = "com_alipay_sofa_healthcheck_skip_component";

    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK             = "com.alipay.sofa.healthcheck.skip.indicator";
    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK_UNDERLINE   = "com_alipay_sofa_healthcheck_skip_indicator";

    public static final String SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND            = "com.alipay.sofa.healthcheck.component.check.round";
    public static final String SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND_UNDERLINE  = "com_alipay_sofa_healthcheck_component_check_round";

    public static final String SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK           = "com.alipay.sofa.healthcheck.strict.component.check";
    public static final String SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK_UNDERLINE = "com_alipay_sofa_healthcheck_strict_component_check";

    // 组件健康检查次数配置项
    public static final String SOFABOOT_COMPONENT_CHECK_RETRY_COUNT             = "com.alipay.sofa.healthcheck.component.check.retry.count";
    // 组件健康检查默认次数
    public static final int    SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT     = 20;
    // 组件健康检查间隔配置项
    public static final String SOFABOOT_COMPONENT_CHECK_RETRY_INTERVAL          = "com.alipay.sofa.healthcheck.component.check.retry.interval";
    // 组件健康检查默认间隔
    public static final long   SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL  = 1000;

    // 模块健康检查次数配置项
    public static final String SOFABOOT_MODULE_CHECK_RETRY_COUNT                = "com.alipay.sofa.healthcheck.module.check.retry.count";
    // 模块健康检查默认次数
    public static final int    SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT        = 0;
    // 模块健康检查间隔配置项
    public static final String SOFABOOT_MODULE_CHECK_RETRY_INTERVAL             = "com.alipay.sofa.healthcheck.module.check.retry.interval";
    // 模块健康检查默认间隔
    public static final long   SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL     = 0;

    //日志
    public static final String SOFABOOT_HEALTH_LOG_SPACE                        = "com.alipay.sofa.healthcheck";

    public static final String SPRING_APPLICATION_NAME                          = "spring.application.name";

    public static final int    DEFAULT_COMPONENT_HEALTH_CHECK_ROUND             = 20;
}
