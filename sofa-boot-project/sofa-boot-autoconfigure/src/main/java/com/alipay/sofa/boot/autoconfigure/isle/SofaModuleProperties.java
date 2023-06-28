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

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties to configure sofa isle.
 *
 * @author xuanbei 18/5/6
 * @author huzijie
 */
@ConfigurationProperties("sofa.boot.isle")
public class SofaModuleProperties {

    /**
     * Active profile list.
     */
    private List<String> activeProfiles                = new ArrayList<>();

    /**
     * Disable sofa module name list.
     */
    private List<String> ignoreModules                 = new ArrayList<>();

    /**
     * Disable require module calculate sofa module name list.
     */
    private List<String> ignoreCalculateRequireModules = new ArrayList<>();

    /**
     * Allow duplicate name sofa module.
     */
    private boolean      allowModuleOverriding         = false;

    /**
     * Allow bean definition override in sofa module application contexts.
     */
    private boolean      allowBeanDefinitionOverriding = false;

    /**
     * Enable parallel sofa module application context refresh.
     */
    private boolean      moduleStartUpParallel         = true;

    /**
     * Allow sofa module application context publish event to parent application context.
     */
    private boolean      publishEventToParent          = false;

    /**
     * Ignore module install failure.
     */
    private boolean      ignoreModuleInstallFailure    = false;

    /**
     * Share parent post processors to sofa module application context.
     */
    private boolean      shareParentPostProcessor      = true;

    /**
     * Thead pool size factor used in parallel sofa module application context refresh.
     */
    private float        parallelRefreshPoolSizeFactor = 5.0f;

    /**
     * Timeout used in parallel sofa module application context refresh, in milliseconds.
     */
    private long         parallelRefreshTimeout        = 60;

    /**
     * Monitor period used in parallel sofa module application context refresh, in milliseconds.
     */
    private long         parallelRefreshCheckPeriod    = 30;

    public List<String> getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public List<String> getIgnoreModules() {
        return ignoreModules;
    }

    public void setIgnoreModules(List<String> ignoreModules) {
        this.ignoreModules = ignoreModules;
    }

    public List<String> getIgnoreCalculateRequireModules() {
        return ignoreCalculateRequireModules;
    }

    public void setIgnoreCalculateRequireModules(List<String> ignoreCalculateRequireModules) {
        this.ignoreCalculateRequireModules = ignoreCalculateRequireModules;
    }

    public boolean isAllowModuleOverriding() {
        return allowModuleOverriding;
    }

    public void setAllowModuleOverriding(boolean allowModuleOverriding) {
        this.allowModuleOverriding = allowModuleOverriding;
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

    public boolean isIgnoreModuleInstallFailure() {
        return ignoreModuleInstallFailure;
    }

    public void setIgnoreModuleInstallFailure(boolean ignoreModuleInstallFailure) {
        this.ignoreModuleInstallFailure = ignoreModuleInstallFailure;
    }

    public float getParallelRefreshPoolSizeFactor() {
        return parallelRefreshPoolSizeFactor;
    }

    public void setParallelRefreshPoolSizeFactor(float parallelRefreshPoolSizeFactor) {
        this.parallelRefreshPoolSizeFactor = parallelRefreshPoolSizeFactor;
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
