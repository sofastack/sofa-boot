/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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
     * @return
     */

    ComponentInfo registerAndGet(ComponentInfo componentInfo);

    /**
     * remove component in this manager
     *
     * @param componentInfo
     * @throws Exception
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
     * @param name
     * @return
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
     * Shuts down the component registry.
     * <p/>
     * This unregisters all objects registered in this registry.
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
