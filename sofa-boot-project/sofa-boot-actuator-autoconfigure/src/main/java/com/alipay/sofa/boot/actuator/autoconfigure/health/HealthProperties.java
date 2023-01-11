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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties to configure health.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/5/18
 */
@ConfigurationProperties(prefix = "sofa.boot.actuator.health")
public class HealthProperties {

    /**
     * 健康检查失败时抛出异常
     */
    private boolean                          insulator                    = false;

    /**
     * 并行健康检查
     */
    private boolean                          parallelCheck                = true;

    /**
     * 并行健康检查超时时间，单位 ms
     */
    private long                             parallelCheckTimeout         = 120 * 1000;

    /**
     * 手动触发 readinessCallBack
     */
    private boolean                          manualReadinessCallback      = false;

    /**
     * 排除 {@link org.springframework.boot.actuate.health.HealthIndicator} 组件列表
     */
    private List<String>                     excludedIndicators           = new ArrayList<>();

    /**
     * Readiness 健康检查跳过所有组件
     */
    private boolean                          skipAll                      = false;

    /**
     * Readiness 健康检查跳过所有 {@link com.alipay.sofa.boot.actuator.health.HealthChecker} 组件
     */
    private boolean                          skipHealthChecker            = false;

    /**
     * Readiness 健康检查跳过所有 {@link org.springframework.boot.actuate.health.HealthIndicator} 组件
     */
    private boolean                          skipHealthIndicator          = false;

    /**
     * 全局的 {@link com.alipay.sofa.boot.actuator.health.HealthChecker} 组件超时时间，单位ms
     */
    private int                              globalHealthCheckerTimeout   = 60 * 1000;

    /**
     * 指定 {@link com.alipay.sofa.boot.actuator.health.HealthChecker} 组件配置,
     * key 为 {@link com.alipay.sofa.boot.actuator.health.HealthChecker#getComponentName()} 的返回值
     */
    private Map<String, HealthCheckerConfig> healthCheckerConfig          = new HashMap<>();

    /**
     * 全局的 {@link org.springframework.boot.actuate.health.HealthIndicator} 组件超时时间，单位ms
     */
    private int                              globalHealthIndicatorTimeout = 60 * 1000;

    /**
     * 指定 {@link org.springframework.boot.actuate.health.HealthIndicator} 组件超时时间, key 为 beanId, value 为超时时间，单位ms
     */
    private Map<String, HealthCheckerConfig> healthIndicatorConfig        = new HashMap<>();

    public HealthProperties() {
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
