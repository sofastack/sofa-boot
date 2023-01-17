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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;

/**
 * SOFA Runtime Context
 *
 * @author xuanbei 18/2/28
 */
public class SofaRuntimeContext {
    private final Properties properties;
    /** component manager */
    private final ComponentManager           componentManager;
    /** client factory */
    private final ClientFactory              clientFactory;

    private final SofaRuntimeManager         sofaRuntimeManager;

    private DynamicServiceProxyManager serviceProxyManager;

    public SofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager,
                              ComponentManager componentManager, ClientFactory clientFactory) {
        this.sofaRuntimeManager = sofaRuntimeManager;
        this.componentManager = componentManager;
        this.clientFactory = clientFactory;
        this.properties = new Properties();
    }

    public String getAppName() {
        return sofaRuntimeManager.getAppName();
    }

    public ClassLoader getAppClassLoader() {
        return sofaRuntimeManager.getAppClassLoader();
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public SofaRuntimeManager getSofaRuntimeManager() {
        return sofaRuntimeManager;
    }

    public DynamicServiceProxyManager getServiceProxyManager() {
        return serviceProxyManager;
    }

    public Properties getProperties() {
        return properties;
    }

    public class Properties {

        private boolean skipJvmReferenceHealthCheck   = false;
        private boolean skipExtensionHealthCheck      = false;
        private boolean disableJvmFirst               = false;
        private boolean extensionFailureInsulating    = false;
        private boolean skipAllComponentShutdown      = false;
        private boolean skipCommonComponentShutdown   = false;
        private boolean jvmFilterEnable               = false;
        private boolean serviceInterfaceTypeCheck     = false;

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
}
