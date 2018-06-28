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

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaRuntimeProperties {

    private static ConcurrentHashMap<ClassLoader, Boolean> skipJvmReferenceHealthCheckMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ClassLoader, Boolean> disableJvmFirstMap             = new ConcurrentHashMap<>();

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
    }

}