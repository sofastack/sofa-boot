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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangen on 17/8/7.
 */
public class HealthCheckConfigurationMapping {
    public static Map<String, String> dotMap = new HashMap<>();

    static {

        dotMap.put(HealthCheckConfigurationConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK,
            HealthCheckConfigurationConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK_UNDERLINE);

        dotMap.put(HealthCheckConfigurationConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK,
            HealthCheckConfigurationConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK_UNDERLINE);

        dotMap.put(HealthCheckConfigurationConstants.SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND,
            HealthCheckConfigurationConstants.SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND_UNDERLINE);

        dotMap.put(HealthCheckConfigurationConstants.SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK,
            HealthCheckConfigurationConstants.SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK_UNDERLINE);

        dotMap.put(HealthCheckConfigurationConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK,
            HealthCheckConfigurationConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK_UNDERLINE);

    }

}
