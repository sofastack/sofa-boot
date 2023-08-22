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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.ark.spi.replay.ReplayContext;
import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Component to found service component in other Ark biz.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class DynamicJvmServiceProxyFinder {

    private static final DynamicJvmServiceProxyFinder         dynamicJvmServiceProxyFinder = new DynamicJvmServiceProxyFinder();

    private static final Map<String, JvmServiceTargetHabitat> jvmServiceTargetHabitats     = new ConcurrentHashMap<>();

    private DynamicJvmServiceProxyFinder() {
    }

    @ArkInject
    private BizManagerService bizManagerService;

    private boolean           hasFinishStartup = false;

    public ServiceProxy findServiceProxy(ClassLoader clientClassloader, Contract contract) {
        ServiceComponent serviceComponent = findServiceComponent(clientClassloader, contract);
        if (serviceComponent == null) {
            return null;
        }

        SofaRuntimeManager sofaRuntimeManager = serviceComponent.getContext()
            .getSofaRuntimeManager();
        Biz biz = getBiz(sofaRuntimeManager);

        if (biz == null) {
            return null;
        }

        return createDynamicJvmServiceInvoker(clientClassloader, contract, serviceComponent,
            sofaRuntimeManager, biz);
    }

    public ServiceComponent findServiceComponent(ClassLoader clientClassloader, Contract contract) {
        ServiceComponent serviceComponent;
        if (hasFinishStartup && SofaRuntimeContainer.isJvmServiceCache(clientClassloader)) {
            String uniqueName = getUniqueName(contract);
            serviceComponent = cacheSearching(uniqueName);
            if (serviceComponent != null) {
                return serviceComponent;
            }
        }

        String interfaceType = contract.getInterfaceTypeCanonicalName();
        String uniqueId = contract.getUniqueId();
        for (SofaRuntimeManager sofaRuntimeManager : SofaRuntimeContainer.sofaRuntimeManagerSet()) {
            if (sofaRuntimeManager.getAppClassLoader().equals(clientClassloader)) {
                continue;
            }

            String version = ReplayContext.get();

            if (ReplayContext.PLACEHOLDER.equals(version)) {
                version = null;
            }

            Biz biz = getBiz(sofaRuntimeManager);
            // if null , check next
            if (biz == null) {
                continue;
            }

            // do not match state, check next
            // https://github.com/sofastack/sofa-boot/issues/532
            if (hasFinishStartup && biz.getBizState() != BizState.DEACTIVATED
                && biz.getBizState() != BizState.ACTIVATED) {
                continue;
            }

            // if specified version , but version do not match ,check next
            if (version != null && !version.equals(biz.getBizVersion())) {
                continue;
            }

            // if not specified version, but state do not match, check next
            // https://github.com/sofastack/sofa-boot/issues/532
            if (hasFinishStartup && version == null && biz.getBizState() != BizState.ACTIVATED) {
                continue;
            }

            // match biz
            serviceComponent = findServiceComponent(uniqueId, interfaceType,
                sofaRuntimeManager.getComponentManager());
            if (serviceComponent != null) {
                return serviceComponent;
            }
        }
        return null;
    }

    public void afterBizStartup(Biz biz) {
        // Currently, there is no way to get SOFA Runtime Manager from biz
        // The overhead is acceptable as this only happens after biz's successful installation
        SofaRuntimeManager sofaRuntimeManager = SofaRuntimeContainer.getSofaRuntimeManager(biz.getBizClassLoader());
        if (sofaRuntimeManager != null && SofaRuntimeContainer.isJvmServiceCache(biz.getBizClassLoader())) {
            for (ComponentInfo componentInfo: sofaRuntimeManager.getComponentManager().getComponents()) {
                if (componentInfo instanceof ServiceComponent serviceComponent) {
                    String uniqueName = getUniqueName(serviceComponent.getService());
                    addCache(uniqueName, biz, serviceComponent);
                }
            }
        }
    }

    public void afterBizUninstall(Biz biz) {
        SofaRuntimeManager sofaRuntimeManager = SofaRuntimeContainer.getSofaRuntimeManager(biz.getBizClassLoader());
        if (sofaRuntimeManager != null && SofaRuntimeContainer.isJvmServiceCache(biz.getBizClassLoader())) {
            for (ComponentInfo componentInfo : sofaRuntimeManager.getComponentManager()
                    .getComponents()) {
                if (componentInfo instanceof ServiceComponent serviceComponent) {
                    String uniqueName = getUniqueName(serviceComponent.getService());
                    removeCache(uniqueName, biz);
                }
            }
        }
    }

    DynamicJvmServiceInvoker createDynamicJvmServiceInvoker(ClassLoader clientClassloader,
                                                            Contract contract,
                                                            ServiceComponent serviceComponent,
                                                            SofaRuntimeManager sofaRuntimeManager,
                                                            Biz biz) {
        JvmBinding referenceJvmBinding = (JvmBinding) contract
            .getBinding(JvmBinding.JVM_BINDING_TYPE);
        JvmBinding serviceJvmBinding = (JvmBinding) serviceComponent.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        boolean serialize;
        if (serviceJvmBinding != null && referenceJvmBinding != null) {
            serialize = referenceJvmBinding.getJvmBindingParam().isSerialize()
                        || serviceJvmBinding.getJvmBindingParam().isSerialize();
        } else {
            // Service provider don't intend to publish JVM service, serialize is considered to be true in this case
            serialize = true;
        }

        serialize &= SofaRuntimeContainer.isJvmInvokeSerialize(clientClassloader);

        return new DynamicJvmServiceInvoker(clientClassloader,
            sofaRuntimeManager.getAppClassLoader(), serviceComponent.getService().getTarget(),
            contract, biz.getIdentity(), serialize);
    }

    private void addCache(String uniqueName, Biz biz, ServiceComponent serviceComponent) {
        jvmServiceTargetHabitats.computeIfAbsent(uniqueName, e -> new JvmServiceTargetHabitat(biz.getBizName()));
        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats.get(uniqueName);
        jvmServiceTargetHabitat.addServiceComponent(biz.getBizVersion(), serviceComponent);
    }

    private void removeCache(String uniqueName, Biz biz) {
        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats.get(uniqueName);
        if (jvmServiceTargetHabitat != null) {
            jvmServiceTargetHabitat.removeServiceComponent(biz.getBizVersion());
        }
    }

    private ServiceComponent cacheSearching(String uniqueName) {
        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats.get(uniqueName);
        if (jvmServiceTargetHabitat == null) {
            return null;
        }

        String version = ReplayContext.get();
        version = ReplayContext.PLACEHOLDER.equals(version) ? null : version;
        if (StringUtils.hasText(version)) {
            return jvmServiceTargetHabitat.getServiceComponent(version);
        }
        return jvmServiceTargetHabitat.getDefaultServiceComponent();
    }

    private String getUniqueName(Contract contract) {
        String uniqueName = contract.getInterfaceType().getName();
        if (StringUtils.hasText(contract.getUniqueId())) {
            uniqueName += ":" + contract.getUniqueId();
        }
        return uniqueName;
    }

    /**
     * Find corresponding {@link ServiceComponent} in specified {@link ComponentManager}
     *
     * @param uniqueId
     * @param interfaceType
     * @param componentManager
     * @return
     */
    private ServiceComponent findServiceComponent(String uniqueId, String interfaceType,
                                                  ComponentManager componentManager) {
        Collection<ComponentInfo> components = componentManager
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        for (ComponentInfo c : components) {
            ServiceComponent component = (ServiceComponent) c;
            Contract serviceContract = component.getService();
            if (serviceContract.getInterfaceTypeCanonicalName().equals(interfaceType)
                && uniqueId.equals(serviceContract.getUniqueId())) {
                return component;
            }
        }
        return null;
    }

    /**
     * Get Biz {@link Biz} according to SofaRuntimeManager {@link SofaRuntimeManager}
     *
     * @param sofaRuntimeManager
     * @return
     */
    public static Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
        if (getInstance().bizManagerService == null) {
            return null;
        }

        for (Biz biz : getInstance().bizManagerService.getBizInOrder()) {
            if (biz.getBizClassLoader().equals(sofaRuntimeManager.getAppClassLoader())) {
                return biz;
            }
        }
        return null;
    }

    public boolean isHasFinishStartup() {
        return hasFinishStartup;
    }

    public void setHasFinishStartup(boolean hasFinishStartup) {
        this.hasFinishStartup = hasFinishStartup;
    }

    void setBizManagerService(BizManagerService bizManagerService) {
        this.bizManagerService = bizManagerService;
    }

    public BizManagerService getBizManagerService() {
        return bizManagerService;
    }

    public static DynamicJvmServiceProxyFinder getInstance() {
        return dynamicJvmServiceProxyFinder;
    }

}
