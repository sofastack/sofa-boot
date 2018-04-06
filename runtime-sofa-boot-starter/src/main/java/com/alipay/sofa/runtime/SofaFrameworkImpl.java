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
package com.alipay.sofa.runtime;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * SOFA Framework Implementation
 *
 * @author xuanbei 18/3/1
 */
public class SofaFrameworkImpl implements SofaFrameworkInternal {
    /** sofa runtime managers */
    private Map<String, SofaRuntimeManager> sofaRuntimeManagers = new ConcurrentHashMap<String, SofaRuntimeManager>();
    /** application names */
    private Set<String>                     appNames            = new CopyOnWriteArraySet<String>();

    public SofaFrameworkImpl() {
    }

    @Override
    public void registerSofaRuntimeManager(SofaRuntimeManager sofaRuntimeManager) {
        sofaRuntimeManagers.put(sofaRuntimeManager.getAppName(), sofaRuntimeManager);
        appNames.add(sofaRuntimeManager.getAppName());
    }

    @Override
    public SofaRuntimeContext getSofaRuntimeContext(String appName) {
        SofaRuntimeManager sofaRuntimeManager = sofaRuntimeManagers.get(appName);
        return sofaRuntimeManager == null ? null : sofaRuntimeManager.getSofaRuntimeContext();
    }

    @Override
    public SofaRuntimeManager getSofaRuntimeManager(String appName) {
        return sofaRuntimeManagers.get(appName);
    }

    @Override
    public void removeSofaRuntimeManager(String appName) {
        sofaRuntimeManagers.remove(appName);
        appNames.remove(appName);
    }

    @Override
    public Set<String> getSofaFrameworkAppNames() {
        return appNames;
    }
}
