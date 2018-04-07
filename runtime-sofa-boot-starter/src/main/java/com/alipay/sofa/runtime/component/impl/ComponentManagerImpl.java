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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.log.SofaLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuanbei 18/3/9
 */
public class ComponentManagerImpl implements ComponentManager {

    /** container for all components */
    protected ConcurrentMap<ComponentName, ComponentInfo>                     registry;
    /** container for resolved components */
    protected ConcurrentMap<ComponentType, Map<ComponentName, ComponentInfo>> resolvedRegistry;
    /** client factory */
    protected ClientFactoryInternal                                           clientFactoryInternal;
    /** allow publish or not */
    private AtomicBoolean                                                     allowPublish = new AtomicBoolean(
                                                                                               false);

    public ComponentManagerImpl(ClientFactoryInternal clientFactoryInternal) {
        this.registry = new ConcurrentHashMap(16);
        this.resolvedRegistry = new ConcurrentHashMap(16);
        this.clientFactoryInternal = clientFactoryInternal;
    }

    public Collection<ComponentInfo> getComponentInfos() {
        return new ArrayList(registry.values());
    }

    public Collection<ComponentName> getPendingComponentInfos() {
        List<ComponentName> names = new ArrayList<ComponentName>();
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
        List<ComponentInfo> elems = new ArrayList<ComponentInfo>(registry.values());

        for (ComponentInfo ri : elems) {
            try {
                unregister(ri);
            } catch (Exception e) {
                SofaLogger.error(e, "failed to shutdown component manager");
            }
        }

        try {
            registry.clear();
            registry = null;
            resolvedRegistry.clear();
            resolvedRegistry = null;
            clientFactoryInternal = null;
        } catch (Exception e) {
            SofaLogger.error(e, "Failed to shutdown registry manager");
        }
    }

    @Override
    public Collection<ComponentType> getComponentTypes() {
        return resolvedRegistry.keySet();
    }

    @Override
    public void register(ComponentInfo componentInfo) {
        _register(componentInfo);
    }

    public ComponentInfo registerAndGet(ComponentInfo componentInfo) {
        return _register(componentInfo);
    }

    @Override
    public void registerComponentClient(Class<?> clientType, Object client) {
        clientFactoryInternal.registerClient(clientType, client);
    }

    private ComponentInfo _register(ComponentInfo ci) {
        ComponentName name = ci.getName();
        if (isRegistered(name)) {
            SofaLogger.error("Component was already registered: {0}", name);
            return getComponentInfo(name);
        }

        try {
            ci.register();
        } catch (Exception e) {
            SofaLogger.error(e, "Failed to register component: {0}", ci.getName());
            return null;
        }

        SofaLogger.info("Registering component: {0}", ci.getName());

        try {
            ComponentInfo old = registry.putIfAbsent(ci.getName(), ci);
            if (old != null) {
                SofaLogger.error("Component was already registered: {0}", name);
                return old;
            }
            if (ci.resolve()) {
                _typeRegistry(ci);
                ci.activate();
            }
        } catch (Exception e) {
            ci.exception(e);
            SofaLogger.error(e, "Failed to create the component {0}", ci.getName());
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
        List<ComponentInfo> componentInfos = new ArrayList<ComponentInfo>();

        for (ComponentInfo componentInfo : registry.values()) {
            if (type.equals(componentInfo.getType())) {
                componentInfos.add(componentInfo);
            }
        }

        return componentInfos;
    }

    protected void _typeRegistry(ComponentInfo componentInfo) {
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
