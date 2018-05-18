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

/**
 * @author xuanbei 18/2/28
 */
public interface SofaRuntimeManager {
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
     * is health check passed or not
     *
     * @return true or false
     */
    boolean isStartupHealthCheckPassed();

    /**
     * set health check passed
     */
    void startupHealthCheckPassed();

    /**
     * shutdown manager
     *
     * @throws ServiceRuntimeException throws when exception occur
     */
    void shutdown() throws ServiceRuntimeException;
}
