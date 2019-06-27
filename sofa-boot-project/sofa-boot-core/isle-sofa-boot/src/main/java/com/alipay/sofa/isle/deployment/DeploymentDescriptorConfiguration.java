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
package com.alipay.sofa.isle.deployment;

import java.util.ArrayList;
import java.util.List;

/**
 * deployment configuration
 *
 * @author khotyn 9/11/14 4:51 PM
 */
public class DeploymentDescriptorConfiguration {
    /** module name identities */
    private List<String> moduleNameIdentities    = new ArrayList<>();
    /** require module identities */
    private List<String> requireModuleIdentities = new ArrayList<>();

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
