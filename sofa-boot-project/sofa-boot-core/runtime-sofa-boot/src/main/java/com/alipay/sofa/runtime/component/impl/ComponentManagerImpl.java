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
package com.alipay.sofa.runtime.component.impl;

import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spring.SpringContextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author xuanbei 18/3/9
 */
@SuppressWarnings("unchecked")
public class ComponentManagerImpl implements ComponentManager {
    /** container for all components */
    protected ConcurrentMap<ComponentName, ComponentInfo>                     registry;
    /** container for resolved components */
    protected ConcurrentMap<ComponentType, Map<ComponentName, ComponentInfo>> resolvedRegistry;
    /** client factory */
    private ClientFactoryInternal                                             clientFactoryInternal;

    public ComponentManagerImpl(ClientFactoryInternal clientFactoryInternal) {
        this.registry = new ConcurrentHashMap(16);
        this.resolvedRegistry = new ConcurrentHashMap(16);
        this.clientFactoryInternal = clientFactoryInternal;
    }

    public Collection<ComponentInfo> getComponentInfos() {
        return new ArrayList(registry.values());
    }

    public Collection<ComponentName> getPendingComponentInfos() {
        List<ComponentName> names = new ArrayList<>();
        for (ComponentInfo ri : registry.values()) {
            if (ri.getState() == ComponentStatus.REGISTERED) {
                names.add(ri.getName());
            }
        }
        return names;
    }

    public ComponentInfo getComponentInfo(ComponentName name) {
        return registry.get(name);
    }

    public boolean isRegistered(ComponentName name) {
        return registry.containsKey(name);
    }

    @Override
    public Collection<ComponentInfo> getComponents() {
        return registry.values();
    }

    @Override
    public int size() {
        return registry.size();
    }

    @Override
    public void shutdown() {
        List<ComponentInfo> elems = new ArrayList<>(registry.values());
        // shutdown spring contexts first
        List<ComponentInfo> springContextComponents = elems.stream()
                .filter(componentInfo -> componentInfo instanceof SpringContextComponent).collect(Collectors.toList());

        for (ComponentInfo ri : springContextComponents) {
            try {
                unregister(ri);
            } catch (Throwable t) {
                SofaLogger.error(ErrorCode.convert("01-03001", ri.getName()), t);
            }
        }

        if (!springContextComponents.isEmpty()) {
            elems.removeAll(springContextComponents);
        }

        // shutdown remaining components
        for (ComponentInfo ri : elems) {
            try {
                unregister(ri);
            } catch (Throwable t) {
                SofaLogger.error(ErrorCode.convert("01-03001", ri.getName()), t);
            }
        }

        try {
            if (registry != null) {
                registry.clear();
            }
            if (resolvedRegistry != null) {
                resolvedRegistry.clear();
            }
            clientFactoryInternal = null;
        } catch (Throwable t) {
            SofaLogger.error(ErrorCode.convert("01-03000"), t);
        }
    }

    @Override
    public Collection<ComponentType> getComponentTypes() {
        return resolvedRegistry.keySet();
    }

    @Override
    public void register(ComponentInfo componentInfo) {
        doRegister(componentInfo);
    }

    public ComponentInfo registerAndGet(ComponentInfo componentInfo) {
        return doRegister(componentInfo);
    }

    @Override
    public void registerComponentClient(Class<?> clientType, Object client) {
        clientFactoryInternal.registerClient(clientType, client);
    }

    private ComponentInfo doRegister(ComponentInfo ci) {
        ComponentName name = ci.getName();
        if (isRegistered(name)) {
            SofaLogger.warn("Component was already registered: {}", name);
            if (ci.canBeDuplicate()) {
                return getComponentInfo(name);
            }
            throw new ServiceRuntimeException(ErrorCode.convert("01-03002", name));
        }

        try {
            ci.register();
        } catch (Throwable t) {
            SofaLogger.error(ErrorCode.convert("01-03003", ci.getName()), t);
            return null;
        }

        SofaLogger.info("Registering component: {}", ci.getName());

        try {
            ComponentInfo old = registry.putIfAbsent(ci.getName(), ci);
            if (old != null) {
                SofaLogger.warn("Component was already registered: {}", name);
                if (ci.canBeDuplicate()) {
                    return old;
                }
                throw new ServiceRuntimeException(ErrorCode.convert("01-03002", name));

            }
            if (ci.resolve()) {
                typeRegistry(ci);
                ci.activate();
            }
        } catch (Throwable t) {
            ci.exception(new Exception(t));
            SofaLogger.error(ErrorCode.convert("01-03004", ci.getName()), t);
        }

        return ci;
    }

    public void unregister(ComponentInfo componentInfo) throws ServiceRuntimeException {
        ComponentName componentName = componentInfo.getName();
        registry.remove(componentName);

        if (componentName != null) {
            ComponentType componentType = componentName.getType();

            Map<ComponentName, ComponentInfo> typesRi = resolvedRegistry.get(componentType);
            typesRi.remove(componentName);
        }

        componentInfo.unregister();
    }

    public Collection<ComponentInfo> getComponentInfosByType(ComponentType type) {
        List<ComponentInfo> componentInfos = new ArrayList<>();

        for (ComponentInfo componentInfo : registry.values()) {
            if (type.equals(componentInfo.getType())) {
                componentInfos.add(componentInfo);
            }
        }

        return componentInfos;
    }

    @Override
    public void resolvePendingResolveComponent(ComponentName componentName) {
        ComponentInfo componentInfo = registry.get(componentName);

        if (componentInfo.isResolved()) {
            return;
        }

        if (componentInfo.resolve()) {
            typeRegistry(componentInfo);
            try {
                componentInfo.activate();
            } catch (Throwable t) {
                componentInfo.exception(new Exception(t));
                SofaLogger.error(ErrorCode.convert("01-03005", componentInfo.getName()), t);
            }
        }
    }

    private void typeRegistry(ComponentInfo componentInfo) {
        ComponentName name = componentInfo.getName();
        if (name != null) {
            ComponentType type = name.getType();
            Map<ComponentName, ComponentInfo> typesRi = resolvedRegistry.get(type);

            if (typesRi == null) {
                resolvedRegistry.putIfAbsent(type, new HashMap<ComponentName, ComponentInfo>());
                typesRi = resolvedRegistry.get(type);
            }

            typesRi.put(name, componentInfo);
        }
    }
}
