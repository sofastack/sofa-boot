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

import java.util.concurrent.ConcurrentHashMap;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaRuntimeProperties {

    private static final ConcurrentHashMap<ClassLoader, Boolean> skipJvmReferenceHealthCheckMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassLoader, Boolean> skipExtensionHealthCheckMap    = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassLoader, Boolean> disableJvmFirstMap             = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassLoader, Boolean> skipJvmSerializeMap            = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassLoader, Boolean> extensionFailureInsulatingMap  = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<ClassLoader, Boolean> manualReadinessCallbackMap     = new ConcurrentHashMap<>();
    private static boolean                                       jvmFilterEnable                = false;

    public static boolean isManualReadinessCallback(ClassLoader classLoader) {
        return manualReadinessCallbackMap.get(classLoader) != null
               && manualReadinessCallbackMap.get(classLoader);
    }

    public static void setManualReadinessCallback(ClassLoader classLoader,
                                                  boolean manualReadinessCallback) {
        manualReadinessCallbackMap.putIfAbsent(classLoader, manualReadinessCallback);
    }

    public static boolean isJvmFilterEnable() {
        return jvmFilterEnable;
    }

    public static void setJvmFilterEnable(boolean jvmFilterEnable) {
        SofaRuntimeProperties.jvmFilterEnable = jvmFilterEnable;
    }

    public static boolean isSkipJvmReferenceHealthCheck(SofaRuntimeContext sofaRuntimeContext) {
        return isSkipJvmReferenceHealthCheck(sofaRuntimeContext.getAppClassLoader());
    }

    public static boolean isSkipJvmReferenceHealthCheck(ClassLoader classLoader) {
        return skipJvmReferenceHealthCheckMap.get(classLoader) != null
               && skipJvmReferenceHealthCheckMap.get(classLoader);
    }

    public static void setSkipJvmReferenceHealthCheck(ClassLoader classLoader,
                                                      boolean skipJvmReferenceHealthCheck) {
        skipJvmReferenceHealthCheckMap.putIfAbsent(classLoader, skipJvmReferenceHealthCheck);
    }

    public static boolean isExtensionFailureInsulating(ClassLoader classLoader) {
        return extensionFailureInsulatingMap.get(classLoader) != null
               && extensionFailureInsulatingMap.get(classLoader);
    }

    public static void setExtensionFailureInsulating(ClassLoader classLoader,
                                                     boolean extensionFailureInsulating) {
        extensionFailureInsulatingMap.putIfAbsent(classLoader, extensionFailureInsulating);
    }

    public static boolean isSkipExtensionHealthCheck(SofaRuntimeContext sofaRuntimeContext) {
        return isSkipExtensionHealthCheck(sofaRuntimeContext.getAppClassLoader());
    }

    public static boolean isSkipExtensionHealthCheck(ClassLoader classLoader) {
        return skipExtensionHealthCheckMap.get(classLoader) != null
               && skipExtensionHealthCheckMap.get(classLoader);
    }

    public static void setSkipExtensionHealthCheck(ClassLoader classLoader,
                                                   boolean skipExtensionHealthCheck) {
        skipExtensionHealthCheckMap.putIfAbsent(classLoader, skipExtensionHealthCheck);
    }

    public static boolean isDisableJvmFirst(SofaRuntimeContext sofaRuntimeContext) {
        return isDisableJvmFirst(sofaRuntimeContext.getAppClassLoader());
    }

    public static boolean isDisableJvmFirst(ClassLoader classLoader) {
        return disableJvmFirstMap.get(classLoader) != null && disableJvmFirstMap.get(classLoader);
    }

    public static void setDisableJvmFirst(ClassLoader classLoader, boolean disableJvmFirst) {
        disableJvmFirstMap.putIfAbsent(classLoader, disableJvmFirst);
    }

    public static void unRegisterProperties(ClassLoader classLoader) {
        skipJvmReferenceHealthCheckMap.remove(classLoader);
        disableJvmFirstMap.remove(classLoader);
        skipJvmSerializeMap.remove(classLoader);
    }

    public static Boolean isSkipJvmSerialize(ClassLoader classLoader) {
        return skipJvmSerializeMap.get(classLoader) != null && skipJvmSerializeMap.get(classLoader);
    }

    public static void setSkipJvmSerialize(ClassLoader classLoader, boolean skipJvmSerialize) {
        skipJvmSerializeMap.putIfAbsent(classLoader, skipJvmSerialize);
    }
}
