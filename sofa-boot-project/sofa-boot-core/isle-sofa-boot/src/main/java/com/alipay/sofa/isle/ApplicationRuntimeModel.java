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
package com.alipay.sofa.isle;

import com.alipay.sofa.isle.deployment.DeployRegistry;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.ModuleDeploymentValidator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * contains all deployments of the application
 *
 * @author khotyn 7/25/14 8:15 PM
 */
public class ApplicationRuntimeModel {
    /** deploys */
    private final List<DeploymentDescriptor>        deploys         = new ArrayList<>();
    /** inactive deploys */
    private final List<DeploymentDescriptor>        inactiveDeploys = new ArrayList<>();
    /** failed deployments */
    private final List<DeploymentDescriptor>        failed          = new CopyOnWriteArrayList<>();
    /** installed deployments */
    private final List<DeploymentDescriptor>        installed       = new CopyOnWriteArrayList<>();
    /** module name to deployment */
    private final Map<String, DeploymentDescriptor> springPowered   = new LinkedHashMap<>();
    /** deploy registry */
    private final DeployRegistry                    deployRegistry  = new DeployRegistry();
    /** module deployment validator */
    private ModuleDeploymentValidator               moduleDeploymentValidator;
    /** application name */
    private String                                  appName;
    /** resolved deployments */
    private List<DeploymentDescriptor>              resolvedDeployments;

    public void setModuleDeploymentValidator(ModuleDeploymentValidator moduleDeploymentValidator) {
        this.moduleDeploymentValidator = moduleDeploymentValidator;
    }

    public boolean isModuleDeployment(DeploymentDescriptor deploymentDescriptor) {
        return this.moduleDeploymentValidator.isModuleDeployment(deploymentDescriptor);
    }

    public void addDeployment(DeploymentDescriptor dd) {
        deploys.add(dd);
        deployRegistry.add(dd);
        springPowered.put(dd.getModuleName(), dd);
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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public List<DeploymentDescriptor> getResolvedDeployments() {
        if (resolvedDeployments != null) {
            return resolvedDeployments;
        }

        resolvedDeployments = deployRegistry.getResolvedObjects();
        return resolvedDeployments;
    }

    public DeployRegistry getDeployRegistry() {
        return deployRegistry;
    }

    public DeploymentDescriptor getSpringPoweredDeployment(String springParent) {
        return springPowered.get(springParent);
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
}
