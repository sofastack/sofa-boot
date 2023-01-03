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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
    private boolean      insulator               = false;

    /**
     * 并行健康检查
     */
    private boolean      parallelCheck           = true;

    /**
     * 并行健康检查超时时间，单位 ms
     */
    private long         parallelCheckTimeout    = 120 * 1000;

    /**
     * 手动触发 readinessCallBack
     */
    private boolean      manualReadinessCallback = false;

    /**
     * 排除 HealthIndicator 组件列表
     */
    private List<String> excludedIndicators;

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
}
