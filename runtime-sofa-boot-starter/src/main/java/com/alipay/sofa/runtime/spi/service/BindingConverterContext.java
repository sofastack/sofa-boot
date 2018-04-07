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
package com.alipay.sofa.runtime.spi.service;

import org.springframework.context.ApplicationContext;

/**
 * context information for binding convert
 *
 * @author xuanbei 18/2/28
 */
public class BindingConverterContext {
    /** is inBinding  */
    private boolean            inBinding;
    /** spring context */
    private ApplicationContext applicationContext;
    /** app name */
    private String             appName;
    /** service ref beanId */
    private String             beanId;

    private ClassLoader        appClassLoader;

    private String             loadBalance;

    private String             repeatReferLimit;

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setAppClassLoader(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    public boolean isInBinding() {
        return inBinding;
    }

    public void setInBinding(boolean inBinding) {
        this.inBinding = inBinding;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public String getRepeatReferLimit() {
        return repeatReferLimit;
    }

    public void setRepeatReferLimit(String repeatReferLimit) {
        this.repeatReferLimit = repeatReferLimit;
    }
}
