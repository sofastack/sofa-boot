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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;

import java.util.Collection;

/**
 * Component Manager
 *
 * @author xuanbei 18/2/28
 */
public interface ComponentManager {
    /**
     * get all component types in this manager
     *
     * @return all component types
     */
    Collection<ComponentType> getComponentTypes();

    void register(ComponentInfo componentInfo);

    /**
     * register and get component in this manager
     *
     * @param componentInfo component that should be registered
     * @return component info
     */
    ComponentInfo registerAndGet(ComponentInfo componentInfo);

    /**
     * remove component in this manager
     *
     * @param componentInfo component that should unregister
     * @throws ServiceRuntimeException throws when exception occur
     */
    void unregister(ComponentInfo componentInfo) throws ServiceRuntimeException;

    /**
     * register component client in this manager
     *
     * @param clientType client type
     * @param client     client implementation
     */
    void registerComponentClient(Class<?> clientType, Object client);

    /**
     * get concrete component by component name
     *
     * @param name component name
     * @return concrete component
     */
    ComponentInfo getComponentInfo(ComponentName name);

    /**
     * whether the component is registered or not
     *
     * @param name name
     * @return true or false
     */
    boolean isRegistered(ComponentName name);

    /**
     * get all components in this manager
     *
     * @return all components
     */
    Collection<ComponentInfo> getComponents();

    /**
     * Gets the number of registered objects in this registry.
     *
     * @return the number fo registered objects
     */
    int size();

    /**
     * <p>
     * Shuts down the component registry.
     * This unregisters all objects registered in this registry.
     * </p>
     */
    void shutdown();

    /**
     * get components by component type
     *
     * @param type component type
     * @return components
     */
    Collection<ComponentInfo> getComponentInfosByType(ComponentType type);
}
