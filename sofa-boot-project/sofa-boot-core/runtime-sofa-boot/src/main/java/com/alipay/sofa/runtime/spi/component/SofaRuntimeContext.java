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

/**
 * SOFA Runtime Context
 *
 * @author xuanbei 18/2/28
 */
public class SofaRuntimeContext {
    /** component manager */
    private ComponentManager   componentManager;
    /** client factory */
    private ClientFactory      clientFactory;
    private SofaRuntimeManager sofaRuntimeManager;

    public SofaRuntimeContext(SofaRuntimeManager sofaRuntimeManager,
                              ComponentManager componentManager, ClientFactory clientFactory) {
        this.sofaRuntimeManager = sofaRuntimeManager;
        this.componentManager = componentManager;
        this.clientFactory = clientFactory;
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
}
