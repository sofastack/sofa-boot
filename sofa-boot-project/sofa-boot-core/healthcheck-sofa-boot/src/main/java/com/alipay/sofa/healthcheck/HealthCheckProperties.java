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
package com.alipay.sofa.healthcheck;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/5/18
 */
@ConfigurationProperties(prefix = "com.alipay.sofa.boot")
public class HealthCheckProperties {
    private boolean                          healthCheckInsulator       = false;
    private boolean                          healthCheckParallelEnable  = false;
    private long                             healthCheckParallelTimeout = 120 * 1000;

    private List<String>                     excludedIndicators;
    private Map<String, HealthCheckerConfig> healthCheckerConfigs       = new HashMap<>();
    private Map<String, HealthCheckerConfig> healthIndicatorConfigs     = new HashMap<>();

    public boolean isHealthCheckInsulator() {
        return healthCheckInsulator;
    }

    public void setHealthCheckInsulator(boolean healthCheckInsulator) {
        this.healthCheckInsulator = healthCheckInsulator;
    }

    public List<String> getExcludedIndicators() {
        return excludedIndicators;
    }

    public void setExcludedIndicators(List<String> excludedIndicators) {
        this.excludedIndicators = excludedIndicators;
    }

    public boolean isHealthCheckParallelEnable() {
        return healthCheckParallelEnable;
    }

    public void setHealthCheckParallelEnable(boolean healthCheckParallelEnable) {
        this.healthCheckParallelEnable = healthCheckParallelEnable;
    }

    public long getHealthCheckParallelTimeout() {
        return healthCheckParallelTimeout;
    }

    public void setHealthCheckParallelTimeout(long healthCheckParallelTimeout) {
        this.healthCheckParallelTimeout = healthCheckParallelTimeout;
    }

    public Map<String, HealthCheckerConfig> getHealthCheckerConfigs() {
        return healthCheckerConfigs;
    }

    public void setHealthCheckerConfigs(Map<String, HealthCheckerConfig> healthCheckerConfigs) {
        this.healthCheckerConfigs = healthCheckerConfigs;
    }

    public Map<String, HealthCheckerConfig> getHealthIndicatorConfigs() {
        return healthIndicatorConfigs;
    }

    public void setHealthIndicatorConfigs(Map<String, HealthCheckerConfig> healthIndicatorConfigs) {
        this.healthIndicatorConfigs = healthIndicatorConfigs;
    }
}
