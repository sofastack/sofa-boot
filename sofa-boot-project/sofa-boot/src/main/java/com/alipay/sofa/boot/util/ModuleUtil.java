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

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * @author huazhongming
 * @since 4.4.0
 */
public class ModuleUtil {

    private static final Logger              LOGGER = SofaBootLoggerFactory
                                                        .getLogger(ModuleUtil.class);

    private static final MethodHandle        implAddOpensToAllUnnamed;
    private static final MethodHandle        implAddOpens;
    private static final MethodHandle        implAddExportsToAllUnnamed;
    private static final MethodHandle        implAddExports;
    private static final Map<String, Module> nameToModules;

    static {
        implAddOpensToAllUnnamed = createModuleMethodHandle("implAddOpensToAllUnnamed",
            String.class);
        implAddOpens = createModuleMethodHandle("implAddOpens", String.class);
        implAddExportsToAllUnnamed = createModuleMethodHandle("implAddExportsToAllUnnamed",
            String.class);
        implAddExports = createModuleMethodHandle("implAddExports", String.class);
        nameToModules = getNameToModule();
    }

    /**
     * Export all JDK module packages to all.
     */
    public static void exportAllJDKModulePackageToAll() {
        try {
            if (nameToModules != null) {
                nameToModules.forEach((name, module) -> module.getPackages().forEach(pkgName -> {
                    if (isJDKModulePackage(pkgName)) {
                        addOpensToAll(module, pkgName);
                        addExportsToAll(module, pkgName);
                    }
                }));
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to export all JDK module package to all", t);
        }
    }

    private static boolean isJDKModulePackage(String modulePackageName) {
        return modulePackageName.startsWith("java.") || modulePackageName.startsWith("jdk.");
    }

    /**
     * Export all module packages to all.
     */
    public static void exportAllModulePackageToAll() {
        try {
            Map<String, Module> nameToModules = getNameToModule();
            if (nameToModules != null) {
                nameToModules.forEach((name, module) -> module.getPackages().forEach(pkgName -> {
                    addOpensToAll(module, pkgName);
                    addExportsToAll(module, pkgName);
                }));
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to export all module package to all", t);
        }
    }

    /**
     * Updates this module to open a package to all unnamed modules.
     *
     * @param moduleName
     * @param packageName
     */
    public static boolean addOpensToAllUnnamed(String moduleName, String packageName) {
        return invokeModuleMethod(implAddOpensToAllUnnamed, moduleName, packageName);
    }

    /**
     * Updates this module to open a package to all unnamed modules.
     *
     * @param module
     * @param packageName
     */
    public static boolean addOpensToAllUnnamed(Module module, String packageName) {
        return invokeModuleMethod(implAddOpensToAllUnnamed, module, packageName);
    }

    /**
     * Updates this module to export a package to all unnamed modules.
     *
     * @param moduleName
     * @param packageName
     */
    public static boolean addExportsToAllUnnamed(String moduleName, String packageName) {
        return invokeModuleMethod(implAddExportsToAllUnnamed, moduleName, packageName);
    }

    /**
     * Updates this module to export a package to all unnamed modules.
     *
     * @param module
     * @param packageName
     */
    public static boolean addExportsToAllUnnamed(Module module, String packageName) {
        return invokeModuleMethod(implAddExportsToAllUnnamed, module, packageName);
    }

    /**
     * Updates this module to open a package to another module.
     *
     * @param moduleName
     * @param packageName
     */
    public static boolean addOpensToAll(String moduleName, String packageName) {

        return invokeModuleMethod(implAddOpens, moduleName, packageName);
    }

    /**
     * Updates this module to open a package to another module.
     *
     * @param module
     * @param packageName
     */
    public static boolean addOpensToAll(Module module, String packageName) {

        return invokeModuleMethod(implAddOpens, module, packageName);
    }

    /**
     * Updates this module to export a package unconditionally.
     * @param moduleName
     * @param packageName
     */
    public static boolean addExportsToAll(String moduleName, String packageName) {
        return invokeModuleMethod(implAddExports, moduleName, packageName);
    }

    /**
     * Updates this module to export a package unconditionally.
     * @param module
     * @param packageName
     */
    public static boolean addExportsToAll(Module module, String packageName) {
        return invokeModuleMethod(implAddExports, module, packageName);
    }

    /**
     * invoke ModuleLayer method
     *
     * @param method
     * @param moduleName
     * @param packageName
     * @return
     */
    public static boolean invokeModuleMethod(MethodHandle method, String moduleName,
                                             String packageName) {
        Optional<Module> findModule = ModuleLayer.boot().findModule(moduleName);
        if (findModule.isPresent()) {
            try {
                return invokeModuleMethod(method, findModule.get(), packageName);
            } catch (Throwable t) {
                LOGGER.error("Failed to invoke ModuleLayer method: {}", method, t);
            }
        }
        return false;
    }

    public static boolean invokeModuleMethod(MethodHandle method, Module module, String packageName) {
        try {
            method.invoke(module, packageName);
            return true;
        } catch (Throwable t) {
            LOGGER.error("Failed to invoke Module method: {}", method, t);
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
    private static MethodHandle createModuleMethodHandle(String methodName,
                                                         Class<?>... parameterTypes) {
        try {
            return UnsafeUtil.implLookup().unreflect(
                Module.class.getDeclaredMethod(methodName, parameterTypes));
        } catch (Throwable t) {
            LOGGER.error("Failed to create Module method handle: {}", methodName, t);
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
            LOGGER.error("Failed to get ModuleLayer field value: {}", fieldName, t);
        }
        return null;
    }

    /**
     * Get all modules from System.bootLayer
     *
     * @return modules
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Module> getNameToModule() {
        return (Map<String, Module>) getModuleLayerFieldsValue("nameToModule");
    }

}
