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
package com.alipay.sofa.isle.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.alipay.sofa.isle.spring.config.SofaModuleProperties.PREFIX;

/**
 * Properties
 *
 * @author xuanbei 18/5/6
 */
@ConfigurationProperties(prefix = PREFIX)
public class SofaModuleProperties {
    static final String PREFIX                        = "com.alipay.sofa.boot";

    private String      activeProfiles;
    private long        beanLoadCost                  = 100;
    private boolean     allowBeanDefinitionOverriding = false;
    private boolean     moduleStartUpParallel         = true;
    private boolean     publishEventToParent          = false;

    public String getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(String activeProfiles) {
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

    public long getBeanLoadCost() {
        return beanLoadCost;
    }

    public void setBeanLoadCost(long beanLoadCost) {
        this.beanLoadCost = beanLoadCost;
    }

    public boolean isPublishEventToParent() {
        return publishEventToParent;
    }

    public void setPublishEventToParent(boolean publishEventToParent) {
        this.publishEventToParent = publishEventToParent;
    }
}
