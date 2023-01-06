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

/**
 * 指定 {@link com.alipay.sofa.boot.actuator.health.HealthChecker} 组件配置
 */
public class HealthCheckerConfig {

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试间隔
     */
    private Long    retryTimeInterval;

    /**
     * 是否严格校验
     */
    private Boolean strictCheck;

    /**
     * 超时时间
     */
    private Integer timeout;

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Long getRetryTimeInterval() {
        return retryTimeInterval;
    }

    public void setRetryTimeInterval(Long retryTimeInterval) {
        this.retryTimeInterval = retryTimeInterval;
    }

    public Boolean getStrictCheck() {
        return strictCheck;
    }

    public void setStrictCheck(Boolean strictCheck) {
        this.strictCheck = strictCheck;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
