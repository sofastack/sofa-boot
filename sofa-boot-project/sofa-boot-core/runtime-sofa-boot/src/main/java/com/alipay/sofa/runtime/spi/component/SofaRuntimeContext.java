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
import com.alipay.sofa.runtime.filter.JvmFilterHolder;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.service.DefaultDynamicServiceProxyManager;
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * SOFA Runtime Context.
 *
 * @author xuanbei 18/2/28
 */
public class SofaRuntimeContext {

    /** component manager */
    private final ComponentManager      componentManager;

    private final SofaRuntimeManager    sofaRuntimeManager;

    /** client factory */
    private final ClientFactoryInternal clientFactory;

    private final Properties            properties;

    private final JvmFilterHolder       jvmFilterHolder;

    private DynamicServiceProxyManager  serviceProxyManager;

    public SofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager) {
        this.sofaRuntimeManager = sofaRuntimeManager;
        this.componentManager = sofaRuntimeManager.getComponentManager();
        this.clientFactory = sofaRuntimeManager.getClientFactoryInternal();
        this.properties = new Properties();
        this.jvmFilterHolder = new JvmFilterHolder();
        this.serviceProxyManager = new DefaultDynamicServiceProxyManager();
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

    public void setServiceProxyManager(DynamicServiceProxyManager serviceProxyManager) {
        this.serviceProxyManager = serviceProxyManager;
    }

    public Properties getProperties() {
        return properties;
    }

    public JvmFilterHolder getJvmFilterHolder() {
        return jvmFilterHolder;
    }

    public static class Properties {

        private boolean      skipJvmReferenceHealthCheck          = false;
        private boolean      skipExtensionHealthCheck             = false;
        private boolean      disableJvmFirst                      = false;
        private boolean      extensionFailureInsulating           = false;
        private boolean      skipAllComponentShutdown             = false;
        private boolean      skipCommonComponentShutdown          = false;
        private boolean      jvmFilterEnable                      = false;
        private boolean      serviceInterfaceTypeCheck            = false;
        private List<String> skipJvmReferenceHealthCheckList      = new ArrayList<>();
        private boolean      referenceHealthCheckMoreDetailEnable = false;
        private boolean      serviceCanBeDuplicate                = true;

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

        public List<String> getSkipJvmReferenceHealthCheckList() {
            return skipJvmReferenceHealthCheckList;
        }

        public void setSkipJvmReferenceHealthCheckList(List<String> skipJvmReferenceHealthCheckList) {
            this.skipJvmReferenceHealthCheckList = skipJvmReferenceHealthCheckList;
        }

        public boolean isReferenceHealthCheckMoreDetailEnable() {
            return referenceHealthCheckMoreDetailEnable;
        }

        public void setReferenceHealthCheckMoreDetailEnable(boolean referenceHealthCheckMoreDetailEnable) {
            this.referenceHealthCheckMoreDetailEnable = referenceHealthCheckMoreDetailEnable;
        }

        public boolean isServiceCanBeDuplicate() {
            return serviceCanBeDuplicate;
        }

        public void setServiceCanBeDuplicate(boolean serviceCanBeDuplicate) {
            this.serviceCanBeDuplicate = serviceCanBeDuplicate;
        }
    }
}
