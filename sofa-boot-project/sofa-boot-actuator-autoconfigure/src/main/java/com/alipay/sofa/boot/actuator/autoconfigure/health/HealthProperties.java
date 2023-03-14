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
package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.HealthCheckerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties to configure health.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 * Created on 2020/5/18
 */
@ConfigurationProperties("sofa.boot.actuator.health")
public class HealthProperties {

    /**
     * Used to custom web response status.
     */
    @NestedConfigurationProperty
    private final org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Status status                       = new org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Status();

    /**
     * Thrown exception when the health check fails.
     */
    private boolean                                                                             insulator                    = false;

    /**
     * Enable parallel health check.
     */
    private boolean                                                                             parallelCheck                = true;

    /**
     * Timeout duration of parallel health check, in milliseconds.
     */
    private long                                                                                parallelCheckTimeout         = 120 * 1000;

    /**
     * Manually trigger the readinessCallBack.
     */
    private boolean                                                                             manualReadinessCallback      = false;

    /**
     * Exclude {@link org.springframework.boot.actuate.health.HealthIndicator} component list.
     */
    private List<String>                                                                        excludedIndicators           = new ArrayList<>();

    /**
     * Readiness health check skips all components.
     */
    private boolean                                                                             skipAll                      = false;

    /**
     * Readiness health check skips all {@link com.alipay.sofa.boot.actuator.health.HealthChecker} components.
     */
    private boolean                                                                             skipHealthChecker            = false;

    /**
     * Readiness health check skips all {@link org.springframework.boot.actuate.health.HealthIndicator} components.
     */
    private boolean                                                                             skipHealthIndicator          = false;

    /**
     * Global {@link com.alipay.sofa.boot.actuator.health.HealthChecker} health check timeout，in milliseconds.
     */
    private int                                                                                 globalHealthCheckerTimeout   = 60 * 1000;

    /**
     * Customize {@link com.alipay.sofa.boot.actuator.health.HealthChecker} component property,
     * key is {@link com.alipay.sofa.boot.actuator.health.HealthChecker#getComponentName()} return value.
     */
    private Map<String, HealthCheckerConfig>                                                    healthCheckerConfig          = new HashMap<>();

    /**
     * Global {@link org.springframework.boot.actuate.health.HealthIndicator} health check timeout，in milliseconds.
     */
    private int                                                                                 globalHealthIndicatorTimeout = 60 * 1000;

    /**
     * Customize {@link org.springframework.boot.actuate.health.HealthIndicator} component property,
     * key is bean name.
     */
    private Map<String, HealthCheckerConfig>                                                    healthIndicatorConfig        = new HashMap<>();

    public org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Status getStatus() {
        return this.status;
    }

    public boolean isInsulator() {
        return insulator;
    }

    public void setInsulator(boolean insulator) {
        this.insulator = insulator;
    }

    public boolean isParallelCheck() {
        return parallelCheck;
    }

    public void setParallelCheck(boolean parallelCheck) {
        this.parallelCheck = parallelCheck;
    }

    public long getParallelCheckTimeout() {
        return parallelCheckTimeout;
    }

    public void setParallelCheckTimeout(long parallelCheckTimeout) {
        this.parallelCheckTimeout = parallelCheckTimeout;
    }

    public boolean isManualReadinessCallback() {
        return manualReadinessCallback;
    }

    public void setManualReadinessCallback(boolean manualReadinessCallback) {
        this.manualReadinessCallback = manualReadinessCallback;
    }

    public List<String> getExcludedIndicators() {
        return excludedIndicators;
    }

    public void setExcludedIndicators(List<String> excludedIndicators) {
        this.excludedIndicators = excludedIndicators;
    }

    public boolean isSkipAll() {
        return skipAll;
    }

    public void setSkipAll(boolean skipAll) {
        this.skipAll = skipAll;
    }

    public boolean isSkipHealthChecker() {
        return skipHealthChecker;
    }

    public void setSkipHealthChecker(boolean skipHealthChecker) {
        this.skipHealthChecker = skipHealthChecker;
    }

    public boolean isSkipHealthIndicator() {
        return skipHealthIndicator;
    }

    public void setSkipHealthIndicator(boolean skipHealthIndicator) {
        this.skipHealthIndicator = skipHealthIndicator;
    }

    public int getGlobalHealthCheckerTimeout() {
        return globalHealthCheckerTimeout;
    }

    public void setGlobalHealthCheckerTimeout(int globalHealthCheckerTimeout) {
        this.globalHealthCheckerTimeout = globalHealthCheckerTimeout;
    }

    public Map<String, HealthCheckerConfig> getHealthCheckerConfig() {
        return healthCheckerConfig;
    }

    public void setHealthCheckerConfig(Map<String, HealthCheckerConfig> healthCheckerConfig) {
        this.healthCheckerConfig = healthCheckerConfig;
    }

    public int getGlobalHealthIndicatorTimeout() {
        return globalHealthIndicatorTimeout;
    }

    public void setGlobalHealthIndicatorTimeout(int globalHealthIndicatorTimeout) {
        this.globalHealthIndicatorTimeout = globalHealthIndicatorTimeout;
    }

    public Map<String, HealthCheckerConfig> getHealthIndicatorConfig() {
        return healthIndicatorConfig;
    }

    public void setHealthIndicatorConfig(Map<String, HealthCheckerConfig> healthIndicatorConfig) {
        this.healthIndicatorConfig = healthIndicatorConfig;
    }
}
