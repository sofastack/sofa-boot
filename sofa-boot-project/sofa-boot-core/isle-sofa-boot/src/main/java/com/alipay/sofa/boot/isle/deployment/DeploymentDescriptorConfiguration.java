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
package com.alipay.sofa.boot.isle.deployment;

import java.util.List;

/**
 * Deployment configuration.
 *
 * @author khotyn 9/11/14 4:51 PM
 */
public class DeploymentDescriptorConfiguration {

    public static String       SOFA_MODULE_FILE      = "sofa-module.properties";

    public static String       SPRING_CONTEXT_PATH   = "META-INF/spring";

    public static String       DEFAULT_PROFILE_VALUE = "default";

    /** sofa-module.properties keywords **/
    public static String       SPRING_PARENT         = "Spring-Parent";

    public static String       MODULE_NAME           = "Module-Name";

    public static String       REQUIRE_MODULE        = "Require-Module";

    public static String       MODULE_PROFILE        = "Module-Profile";

    /** module name identities */
    private final List<String> moduleNameIdentities;

    /** require module identities */
    private final List<String> requireModuleIdentities;

    public DeploymentDescriptorConfiguration(List<String> moduleNameIdentities,
                                             List<String> requireModuleIdentities) {
        this.moduleNameIdentities = moduleNameIdentities;
        this.requireModuleIdentities = requireModuleIdentities;
    }

    public List<String> getModuleNameIdentities() {
        return moduleNameIdentities;
    }

    public List<String> getRequireModuleIdentities() {
        return requireModuleIdentities;
    }
}
