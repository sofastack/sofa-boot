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
package com.alipay.sofa.boot.autoconfigure.runtime;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to configure sofa runtime.
 *
 * @author xuanbei 18/5/9
 * @author huzijie
 */
@ConfigurationProperties("sofa.boot.runtime")
public class SofaRuntimeProperties {

    /**
     * Skip jvm reference check in health checker.
     */
    private boolean skipJvmReferenceHealthCheck = false;

    /**
     * Skip extension check in health checker.
     */
    private boolean skipExtensionHealthCheck    = false;

    /**
     * Global disable jvm first.
     */
    private boolean disableJvmFirst             = false;

    /**
     * Throw exception when extension failed to load contributions.
     */
    private boolean extensionFailureInsulating  = false;

    /**
     * Skip to destroy all components when shutdown.
     */
    private boolean skipAllComponentShutdown    = false;

    /**
     * Skip to destroy all common components when shutdown.
     */
    private boolean skipCommonComponentShutdown = false;

    /**
     * Enable jvm filter feature.
     */
    private boolean jvmFilterEnable             = false;

    /**
     * Enable service interface type check.
     */
    private boolean serviceInterfaceTypeCheck   = false;

    public boolean isSkipJvmReferenceHealthCheck() {
        return skipJvmReferenceHealthCheck;
    }

    public void setSkipJvmReferenceHealthCheck(boolean skipJvmReferenceHealthCheck) {
        this.skipJvmReferenceHealthCheck = skipJvmReferenceHealthCheck;
    }

    public boolean isSkipExtensionHealthCheck() {
        return skipExtensionHealthCheck;
    }

    public void setSkipExtensionHealthCheck(boolean skipExtensionHealthCheck) {
        this.skipExtensionHealthCheck = skipExtensionHealthCheck;
    }

    public boolean isDisableJvmFirst() {
        return disableJvmFirst;
    }

    public void setDisableJvmFirst(boolean disableJvmFirst) {
        this.disableJvmFirst = disableJvmFirst;
    }

    public boolean isExtensionFailureInsulating() {
        return extensionFailureInsulating;
    }

    public void setExtensionFailureInsulating(boolean extensionFailureInsulating) {
        this.extensionFailureInsulating = extensionFailureInsulating;
    }

    public boolean isSkipAllComponentShutdown() {
        return skipAllComponentShutdown;
    }

    public void setSkipAllComponentShutdown(boolean skipAllComponentShutdown) {
        this.skipAllComponentShutdown = skipAllComponentShutdown;
    }

    public boolean isSkipCommonComponentShutdown() {
        return skipCommonComponentShutdown;
    }

    public void setSkipCommonComponentShutdown(boolean skipCommonComponentShutdown) {
        this.skipCommonComponentShutdown = skipCommonComponentShutdown;
    }

    public boolean isJvmFilterEnable() {
        return jvmFilterEnable;
    }

    public void setJvmFilterEnable(boolean jvmFilterEnable) {
        this.jvmFilterEnable = jvmFilterEnable;
    }

    public boolean isServiceInterfaceTypeCheck() {
        return serviceInterfaceTypeCheck;
    }

    public void setServiceInterfaceTypeCheck(boolean serviceInterfaceTypeCheck) {
        this.serviceInterfaceTypeCheck = serviceInterfaceTypeCheck;
    }
}
