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
package com.alipay.sofa.runtime.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.runtime.SofaRuntimeProperties;

/**
 * SofaRuntimeConfigurationProperties is exported by SOFA Runtime plugin in Ark.
 * Upon installing an Ark module, Thread context classloader will be switched to Biz ClassLoader.
 *
 * @author xuanbei 18/5/9
 */
@ConfigurationProperties(SofaBootConstants.PREFIX)
public class SofaRuntimeConfigurationProperties {

    public boolean isManualReadinessCallback() {
        return SofaRuntimeProperties.isManualReadinessCallback(Thread.currentThread()
            .getContextClassLoader());
    }

    public void setManualReadinessCallback(boolean manualReadinessCallback) {
        SofaRuntimeProperties.setManualReadinessCallback(Thread.currentThread()
            .getContextClassLoader(), manualReadinessCallback);
    }

    public boolean isJvmFilterEnable() {
        return SofaRuntimeProperties.isJvmFilterEnable();
    }

    public void setJvmFilterEnable(boolean jvmFilterEnable) {
        SofaRuntimeProperties.setJvmFilterEnable(jvmFilterEnable);
    }

    public void setSkipJvmReferenceHealthCheck(boolean skipJvmReferenceHealthCheck) {
        SofaRuntimeProperties.setSkipJvmReferenceHealthCheck(Thread.currentThread()
            .getContextClassLoader(), skipJvmReferenceHealthCheck);
    }

    public void setDisableJvmFirst(boolean disableJvmFirst) {
        SofaRuntimeProperties.setDisableJvmFirst(Thread.currentThread().getContextClassLoader(),
            disableJvmFirst);
    }

    public boolean isSkipJvmReferenceHealthCheck() {
        return SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(Thread.currentThread()
            .getContextClassLoader());
    }

    public void setExtensionFailureInsulating(boolean extensionFailureInsulating) {
        SofaRuntimeProperties.setExtensionFailureInsulating(Thread.currentThread()
            .getContextClassLoader(), extensionFailureInsulating);
    }

    public boolean isExtensionFailureInsulating() {
        return SofaRuntimeProperties.isExtensionFailureInsulating(Thread.currentThread()
            .getContextClassLoader());
    }

    public boolean isDisableJvmFirst() {
        return SofaRuntimeProperties.isDisableJvmFirst(Thread.currentThread()
            .getContextClassLoader());
    }

    public void setSkipJvmSerialize(boolean skipJvmSerialize) {
        SofaRuntimeProperties.setSkipJvmSerialize(Thread.currentThread().getContextClassLoader(),
            skipJvmSerialize);
    }

    public boolean isSkipJvmSerialize() {
        return SofaRuntimeProperties.isSkipJvmSerialize(Thread.currentThread()
            .getContextClassLoader());
    }

    public void setSkipExtensionHealthCheck(boolean skipExtensionHealthCheck) {
        SofaRuntimeProperties.setSkipExtensionHealthCheck(Thread.currentThread()
            .getContextClassLoader(), skipExtensionHealthCheck);
    }

    public boolean isSkipExtensionHealthCheck() {
        return SofaRuntimeProperties.isSkipExtensionHealthCheck(Thread.currentThread()
            .getContextClassLoader());
    }

}
