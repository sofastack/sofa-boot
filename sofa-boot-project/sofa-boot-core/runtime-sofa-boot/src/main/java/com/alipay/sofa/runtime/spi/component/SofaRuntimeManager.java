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

import com.alipay.sofa.boot.health.RuntimeHealthChecker;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;

/**
 * @author xuanbei 18/2/28
 */
public interface SofaRuntimeManager extends RuntimeHealthChecker {
    /**
     * get sofa runtime context
     *
     * @return sofa runtime context
     */
    SofaRuntimeContext getSofaRuntimeContext();

    /**
     * get app name
     *
     * @return app name
     */
    String getAppName();

    /**
     * get application ClassLoader
     *
     * @return application ClassLoader
     */
    ClassLoader getAppClassLoader();

    /**
     * get component manager
     *
     * @return component manager
     */
    ComponentManager getComponentManager();

    /**
     * get client factory
     *
     * @return Client 工厂
     */
    ClientFactoryInternal getClientFactoryInternal();

    /**
     * shutdown manager
     *
     * @throws ServiceRuntimeException throws when exception occur
     */
    void shutdown() throws ServiceRuntimeException;

    /**
     * register shutdown aware which would be executed after receive uninstall event
     *
     * @param shutdownAware
     */
    void registerShutdownAware(RuntimeShutdownAware shutdownAware);

    /**
     * register runtime health checker
     *
     * @param runtimeHealthChecker
     */
    void registerRuntimeHealthChecker(RuntimeHealthChecker runtimeHealthChecker);
}
