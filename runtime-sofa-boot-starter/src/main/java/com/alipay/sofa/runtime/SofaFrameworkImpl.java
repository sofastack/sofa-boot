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
    private Map<String, SofaRuntimeManager> sofaRuntimeManagers = new ConcurrentHashMap<>();
    /** application names */
    private Set<String>                     appNames            = new CopyOnWriteArraySet<>();

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
