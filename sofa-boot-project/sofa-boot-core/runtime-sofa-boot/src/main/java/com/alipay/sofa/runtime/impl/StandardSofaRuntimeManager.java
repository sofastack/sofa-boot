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
package com.alipay.sofa.runtime.impl;

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link SofaRuntimeManager}.
 *
 * @author xuanbei 18/3/1
 */
public class StandardSofaRuntimeManager implements SofaRuntimeManager, ApplicationContextAware {

    private final String                     appName;

    private final List<RuntimeShutdownAware> runtimeShutdownAwareList = new CopyOnWriteArrayList<>();

    private ComponentManagerImpl             componentManager;

    private ClientFactoryInternal            clientFactoryInternal;

    private SofaRuntimeContext               sofaRuntimeContext;

    private ClassLoader                      appClassLoader;

    private ApplicationContext               rootApplicationContext;

    public StandardSofaRuntimeManager(String appName, ClassLoader appClassLoader,
                                      ClientFactoryInternal clientFactoryInternal) {
        this.appName = appName;
        this.appClassLoader = appClassLoader;
        this.clientFactoryInternal = clientFactoryInternal;
        this.componentManager = new ComponentManagerImpl(clientFactoryInternal, appClassLoader);
        this.sofaRuntimeContext = new SofaRuntimeContext(this);
        this.componentManager.setSofaRuntimeContext(sofaRuntimeContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    @Override
    public ClientFactoryInternal getClientFactoryInternal() {
        return clientFactoryInternal;
    }

    @Override
    public SofaRuntimeContext getSofaRuntimeContext() {
        return sofaRuntimeContext;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    /**
     * shutdown sofa runtime manager
     *
     * @throws ServiceRuntimeException exception occur
     */
    @Override
    public void shutdown() throws ServiceRuntimeException {
        try {
            for (RuntimeShutdownAware shutdownAware : runtimeShutdownAwareList) {
                shutdownAware.shutdown();
            }

            if (componentManager != null) {
                componentManager.shutdown();
            }

            clear();
        } catch (Throwable throwable) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-03100"), throwable);
        }
    }

    @Override
    public void shutDownExternally() throws ServiceRuntimeException {
        try {
            AbstractApplicationContext applicationContext = (AbstractApplicationContext) rootApplicationContext;
            // only need shutdown when root context is active
            if (applicationContext.isActive()) {
                applicationContext.close();
            }
            appClassLoader = null;
        } catch (Throwable throwable) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-03100"), throwable);
        }
    }

    @Override
    public void registerShutdownAware(RuntimeShutdownAware callback) {
        runtimeShutdownAwareList.add(callback);
    }

    @Override
    public ApplicationContext getRootApplicationContext() {
        return rootApplicationContext;
    }

    protected void clear() {
        componentManager = null;
        sofaRuntimeContext = null;
        clientFactoryInternal = null;
        runtimeShutdownAwareList.clear();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        rootApplicationContext = applicationContext;
    }
}
