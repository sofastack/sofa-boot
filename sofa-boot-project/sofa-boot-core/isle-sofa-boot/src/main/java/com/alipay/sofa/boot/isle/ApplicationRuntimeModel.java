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
package com.alipay.sofa.boot.isle;

import com.alipay.sofa.boot.isle.deployment.DeployRegistry;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.ModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.profile.SofaModuleProfileChecker;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core model contains all deployments of the application.
 *
 * @author khotyn 7/25/14 8:15 PM
 */
public class ApplicationRuntimeModel implements IsleDeploymentModel {

    public static final String                      APPLICATION_RUNTIME_MODEL_NAME = "APPLICATION_RUNTIME_MODEL";
    /** deploys */
    private final List<DeploymentDescriptor>        deploys                        = new ArrayList<>();
    /** inactive deploys */
    private final List<DeploymentDescriptor>        inactiveDeploys                = new ArrayList<>();
    /** failed deployments */
    private final List<DeploymentDescriptor>        failed                         = new CopyOnWriteArrayList<>();
    /** installed deployments */
    private final List<DeploymentDescriptor>        installed                      = new CopyOnWriteArrayList<>();
    /** module name to deployment */
    private final Map<String, DeploymentDescriptor> deploymentMap                  = new LinkedHashMap<>();
    /** deploy registry */
    private final DeployRegistry                    deployRegistry                 = new DeployRegistry();
    /** no spring powered deploys name*/
    private final Set<String>                       noSpringPoweredDeploys         = new HashSet<>();
    /** module deployment validator */
    private ModuleDeploymentValidator               moduleDeploymentValidator;
    /** module profiles checker */
    protected SofaModuleProfileChecker              sofaModuleProfileChecker;
    /** resolved deployments */
    private List<DeploymentDescriptor>              resolvedDeployments;

    public boolean isModuleDeployment(DeploymentDescriptor deploymentDescriptor) {
        return this.moduleDeploymentValidator.isModuleDeployment(deploymentDescriptor);
    }

    public boolean acceptModule(DeploymentDescriptor deploymentDescriptor) {
        return this.sofaModuleProfileChecker.acceptModule(deploymentDescriptor);
    }

    public DeploymentDescriptor addDeployment(DeploymentDescriptor dd) {
        deploys.add(dd);
        deployRegistry.add(dd);
        return deploymentMap.put(dd.getModuleName(), dd);
    }

    public List<DeploymentDescriptor> getAllDeployments() {
        Collections.sort(deploys);
        return deploys;
    }

    public void addInactiveDeployment(DeploymentDescriptor dd) {
        inactiveDeploys.add(dd);
    }

    public List<DeploymentDescriptor> getAllInactiveDeployments() {
        Collections.sort(inactiveDeploys);
        return inactiveDeploys;
    }

    public List<DeploymentDescriptor> getResolvedDeployments() {
        if (resolvedDeployments != null) {
            return resolvedDeployments;
        }

        //remove all required when no spring powered module exist
        deploymentMap.values().forEach(dd -> {
            List<String> requiredModules = dd.getRequiredModules();
            if (requiredModules != null) {
                // if required module is no spring powered, remove it
                requiredModules.removeIf(module -> !deploymentMap.containsKey(module) && noSpringPoweredDeploys.contains(module));
            }
        });

        resolvedDeployments = deployRegistry.getResolvedObjects();
        return resolvedDeployments;
    }

    public void addNoSpringPoweredDeployment(DeploymentDescriptor dd) {
        noSpringPoweredDeploys.add(dd.getModuleName());
    }

    public DeployRegistry getDeployRegistry() {
        return deployRegistry;
    }

    public DeploymentDescriptor getDeploymentByName(String moduleName) {
        return deploymentMap.get(moduleName);
    }

    public void addFailed(DeploymentDescriptor failed) {
        this.failed.add(failed);
    }

    public List<DeploymentDescriptor> getFailed() {
        return failed;
    }

    public void addInstalled(DeploymentDescriptor installed) {
        this.installed.add(installed);
    }

    public List<DeploymentDescriptor> getInstalled() {
        return installed;
    }

    public ModuleDeploymentValidator getModuleDeploymentValidator() {
        return moduleDeploymentValidator;
    }

    public void setModuleDeploymentValidator(ModuleDeploymentValidator moduleDeploymentValidator) {
        this.moduleDeploymentValidator = moduleDeploymentValidator;
    }

    public SofaModuleProfileChecker getSofaModuleProfileChecker() {
        return sofaModuleProfileChecker;
    }

    public void setSofaModuleProfileChecker(SofaModuleProfileChecker sofaModuleProfileChecker) {
        this.sofaModuleProfileChecker = sofaModuleProfileChecker;
    }

    @Override
    @NonNull
    public Map<String, ApplicationContext> getModuleApplicationContextMap() {
        Map<String, ApplicationContext> result = new HashMap<>(8);
        installed.forEach(deploymentDescriptor -> result.put(deploymentDescriptor.getModuleName(), deploymentDescriptor.getApplicationContext()));
        return result;
    }
}
