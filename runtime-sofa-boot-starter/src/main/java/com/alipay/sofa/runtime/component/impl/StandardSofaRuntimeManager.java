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
package com.alipay.sofa.runtime.component.impl;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.AppConfiguration;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.*;

/**
 * Default Sofa Runtime Manager
 *
 * @author xuanbei 18/3/1
 */
public class StandardSofaRuntimeManager implements SofaRuntimeManager {

    private ComponentManager      componentManager;
    private ClientFactoryInternal clientFactoryInternal;
    private SofaRuntimeContext    sofaRuntimeContext;
    private String                appName;
    private ClassLoader           appClassLoader;
    private boolean               isStartupHealthCheckPassed = false;

    public StandardSofaRuntimeManager(String appName, ClassLoader appClassLoader,
                                      AppConfiguration appConfiguration,
                                      ClientFactoryInternal clientFactoryInternal) {
        componentManager = new ComponentManagerImpl(clientFactoryInternal);
        this.appName = appName;
        this.appClassLoader = appClassLoader;
        this.sofaRuntimeContext = new SofaRuntimeContext(this, componentManager,
            clientFactoryInternal, appConfiguration);
        this.clientFactoryInternal = clientFactoryInternal;
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
    public boolean isStartupHealthCheckPassed() {
        return isStartupHealthCheckPassed;
    }

    @Override
    public void startupHealthCheckPassed() {
        isStartupHealthCheckPassed = true;
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
     * @throws Exception
     */
    public void shutdown() throws ServiceRuntimeException {
        if (componentManager != null) {
            componentManager.shutdown();
        }
    }
}
