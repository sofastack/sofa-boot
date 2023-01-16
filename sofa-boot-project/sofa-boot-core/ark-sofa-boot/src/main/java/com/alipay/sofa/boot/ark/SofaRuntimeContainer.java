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
package com.alipay.sofa.boot.ark;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SofaFramework manager interface for ark
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class SofaRuntimeContainer implements ApplicationContextAware, DisposableBean {

    private static final Map<ClassLoader, ApplicationContext> APPLICATION_CONTEXT_MAP  = new ConcurrentHashMap<>();

    private static final Map<ClassLoader, SofaRuntimeManager> SOFA_RUNTIME_MANAGER_MAP = new ConcurrentHashMap<>();

    private static final Map<ClassLoader, Boolean> JVM_SERVICE_CACHE_MAP = new ConcurrentHashMap<>();

    private static final Map<ClassLoader, Boolean> JVM_INVOKE_SERIALIZE_MAP = new ConcurrentHashMap<>();

    private final ClassLoader  contextClassLoader;

    public SofaRuntimeContainer(SofaRuntimeManager sofaRuntimeManager) {
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        SOFA_RUNTIME_MANAGER_MAP.put(contextClassLoader, sofaRuntimeManager);
    }

    public void setJvmServiceCache(boolean jvmServiceCache) {
        JVM_SERVICE_CACHE_MAP.put(contextClassLoader, jvmServiceCache);
    }

    public void setJvmInvokeSerialize(boolean jvmInvokeSerialize) {
        JVM_INVOKE_SERIALIZE_MAP.put(contextClassLoader, jvmInvokeSerialize);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT_MAP.put(contextClassLoader, applicationContext);
    }

    public static ApplicationContext getApplicationContext(ClassLoader classLoader) {
        return APPLICATION_CONTEXT_MAP.get(classLoader);
    }

    public static SofaRuntimeManager getSofaRuntimeManager(ClassLoader classLoader) {
        return SOFA_RUNTIME_MANAGER_MAP.get(classLoader);
    }

    public static boolean isJvmServiceCache(ClassLoader classLoader) {
        return JVM_SERVICE_CACHE_MAP.getOrDefault(classLoader, false) ;
    }

    public static boolean isJvmInvokeSerialize(ClassLoader classLoader) {
        return JVM_INVOKE_SERIALIZE_MAP.getOrDefault(classLoader, true);
    }

    public static Collection<SofaRuntimeManager> sofaRuntimeManagerSet() {
        return SOFA_RUNTIME_MANAGER_MAP.values();
    }

    @Override
    public void destroy() throws Exception {
        APPLICATION_CONTEXT_MAP.remove(contextClassLoader);
    }
}
