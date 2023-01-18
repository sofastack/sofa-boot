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
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import org.springframework.context.ApplicationContext;

/**
 * Interface to get infomations form sofa runtime.
 *
 * @author xuanbei 18/2/28
 */
public interface SofaRuntimeManager {

    /**
     * Get sofa runtime context.
     *
     * @return sofa runtime context
     */
    SofaRuntimeContext getSofaRuntimeContext();

    /**
     * Get app name.
     *
     * @return app name
     */
    String getAppName();

    /**
     * Get application ClassLoader.
     *
     * @return application ClassLoader
     */
    ClassLoader getAppClassLoader();

    /**
     * Get component manager.
     *
     * @return component manager
     */
    ComponentManager getComponentManager();

    /**
     * Get client factory.
     *
     * @return client factory
     */
    ClientFactoryInternal getClientFactoryInternal();

    /**
     * Shutdown sofa runtime.
     *
     * @throws ServiceRuntimeException throws when exception occur
     */
    void shutdown() throws ServiceRuntimeException;

    /**
     * Shutdown root context, only used by external component such sofa ark.
     *
     * @throws ServiceRuntimeException throws when exception occur
     */
    void shutDownExternally() throws ServiceRuntimeException;

    /**
     * Register shutdown aware which would be executed after receive uninstall event.
     *
     * @param shutdownAware shutdownAware
     */
    void registerShutdownAware(RuntimeShutdownAware shutdownAware);

    /**
     * Get application's Spring context.
     * The root Spring context is returned in multi-module application when isle is used.
     *
     * @return Spring context
     */
    ApplicationContext getRootApplicationContext();
}
