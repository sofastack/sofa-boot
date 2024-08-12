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
package com.alipay.sofa.boot.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * @author huazhongming
 * @date 2024/8/7 16
 * @since 4.4.0
 */
public class ModuleUtil {
    private static final MethodHandle implAddOpensToAllUnnamed;
    private static final MethodHandle implAddOpens;
    private static final MethodHandle implAddExportsToAllUnnamed;
    private static final MethodHandle implAddExports;

    static {
        implAddOpensToAllUnnamed = createModuleMethodHandle("implAddOpensToAllUnnamed", String.class);
        implAddOpens = createModuleMethodHandle("implAddOpens", String.class);
        implAddExportsToAllUnnamed = createModuleMethodHandle("implAddExportsToAllUnnamed", String.class);
        implAddExports = createModuleMethodHandle("implAddExports", String.class);

    }

    /**
     *
     */
    public static void exportAllJDKModulePackageToAll() {
        try {
            Map<String, Module> nameToModules = getNameToModule();
            if (nameToModules != null) {
                nameToModules.forEach((name, module) -> module.getPackages().forEach(pkgName -> {
                    if (isJDKModulePackage(pkgName)) {
                        addOpensToAll(module, pkgName);
                        addExportsToAll(module, pkgName);
                    }
                }));
            }
        } catch (Throwable ignored) {

        }
    }

    private static boolean isJDKModulePackage(String modulePackageName) {
        return modulePackageName.startsWith("java.") || modulePackageName.startsWith("jdk.");
    }

    public static void exportAllModulePackageToAll() {
        try {
            Map<String, Module> nameToModules = getNameToModule();
            if (nameToModules != null) {
                nameToModules.forEach((name, module) -> module.getPackages().forEach(pkgName -> {
                    addOpensToAll(module, pkgName);
                    addExportsToAll(module, pkgName);
                }));
            }
        } catch (Throwable ignored) {

        }
    }

    /**
     * @param moduleName
     * @param packageName
     * @see java.lang.Module#implAddOpensToAllUnnamed(String)
     */
    public static boolean addOpensToAllUnnamed(String moduleName, String packageName) {
        return invokeModuleMethod(implAddOpensToAllUnnamed, moduleName, packageName);
    }

    /**
     *
     * @param module
     * @param packageName
     * @see java.lang.Module#implAddOpens(String)
     */
    public static boolean addOpensToAll(Module module, String packageName) {

        return invokeModuleMethod(implAddOpens, module, packageName);
    }

    /**
     * @param module
     * @param packageName
     * @see java.lang.Module#implAddExports(String)
     */
    public static boolean addExportsToAll(Module module, String packageName) {
        return invokeModuleMethod(implAddExports, module, packageName);
    }


    /**
     * invoke ModuleLayer.bootLayer method
     *
     * @param method
     * @param moduleName
     * @param packageName
     * @return
     */
    public static boolean invokeModuleMethod(MethodHandle method, String moduleName, String packageName) {
        Optional<Module> findModule = ModuleLayer.boot().findModule(moduleName);
        if (findModule.isPresent()) {
            try {
                return invokeModuleMethod(method, findModule.get(), packageName);
            } catch (Throwable e) {
                // ignore
            }
        }
        return false;
    }

    public static boolean invokeModuleMethod(MethodHandle method, Module module, String packageName) {
        try {
            method.invoke(module, packageName);
            return true;
        } catch (Throwable e) {
            // ignore
        }
        return false;
    }

    /**
     * create MethodHandle from Module
     *
     * @param methodName
     * @param parameterTypes
     * @return MethodHandle
     */
    private static MethodHandle createModuleMethodHandle(String methodName, Class<?>... parameterTypes) {
        try {
            return UnsafeUtil.implLookup().unreflect(
                    Module.class.getDeclaredMethod(methodName, parameterTypes));
        } catch (Throwable e) {
            // ignore
        }
        return null;
    }

    /**
     * Get ModuleLayer.bootLayer field value
     *
     * @param fieldName
     * @return field value
     */
    private static Object getModuleLayerFieldsValue(String fieldName) {
        ModuleLayer moduleLayer = ModuleLayer.boot();
        try {
            Class<ModuleLayer> moduleLayerClass = ModuleLayer.class;
            Field field = moduleLayerClass.getDeclaredField(fieldName);
            return UnsafeUtil.implLookup().unreflectVarHandle(field).get(moduleLayer);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    /**
     * Get all Modules from System.bootLayer
     *
     * @return modules
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Module> getNameToModule() {
        return (Map<String, Module>) getModuleLayerFieldsValue("nameToModule");
    }

}
