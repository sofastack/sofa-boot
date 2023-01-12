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
package com.alipay.sofa.boot.autoconfigure.isle;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Properties
 *
 * @author xuanbei 18/5/6
 */
@ConfigurationProperties("sofa.boot.isle")
public class SofaModuleProperties {

    private List<String> activeProfiles;

    private boolean     allowBeanDefinitionOverriding               = false;

    private boolean     moduleStartUpParallel                       = true;

    private boolean     publishEventToParent                        = false;

    private boolean     allowModuleOverriding                       = false;

    private boolean     ignoreModuleInstallFailure                  = false;

    private boolean     shareParentPostProcessor                    = true;

    private float       parallelRefreshCoreCountFactor              = 5.0f;

    private long        parallelRefreshTimeout                      = 60;

    private long        parallelRefreshCheckPeriod                  = 30;

    public List<String> getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return allowBeanDefinitionOverriding;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isModuleStartUpParallel() {
        return moduleStartUpParallel;
    }

    public void setModuleStartUpParallel(boolean moduleStartUpParallel) {
        this.moduleStartUpParallel = moduleStartUpParallel;
    }

    public boolean isPublishEventToParent() {
        return publishEventToParent;
    }

    public void setPublishEventToParent(boolean publishEventToParent) {
        this.publishEventToParent = publishEventToParent;
    }

    public boolean isAllowModuleOverriding() {
        return allowModuleOverriding;
    }

    public void setAllowModuleOverriding(boolean allowModuleOverriding) {
        this.allowModuleOverriding = allowModuleOverriding;
    }

    public boolean isIgnoreModuleInstallFailure() {
        return ignoreModuleInstallFailure;
    }

    public void setIgnoreModuleInstallFailure(boolean ignoreModuleInstallFailure) {
        this.ignoreModuleInstallFailure = ignoreModuleInstallFailure;
    }

    public float getParallelRefreshCoreCountFactor() {
        return parallelRefreshCoreCountFactor;
    }

    public void setParallelRefreshCoreCountFactor(float parallelRefreshCoreCountFactor) {
        this.parallelRefreshCoreCountFactor = parallelRefreshCoreCountFactor;
    }

    public long getParallelRefreshTimeout() {
        return parallelRefreshTimeout;
    }

    public void setParallelRefreshTimeout(long parallelRefreshTimeout) {
        this.parallelRefreshTimeout = parallelRefreshTimeout;
    }

    public long getParallelRefreshCheckPeriod() {
        return parallelRefreshCheckPeriod;
    }

    public void setParallelRefreshCheckPeriod(long parallelRefreshCheckPeriod) {
        this.parallelRefreshCheckPeriod = parallelRefreshCheckPeriod;
    }

    public boolean isShareParentPostProcessor() {
        return shareParentPostProcessor;
    }

    public void setShareParentPostProcessor(boolean shareParentPostProcessor) {
        this.shareParentPostProcessor = shareParentPostProcessor;
    }
}
