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
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * contains all deployments of the application
 *
 * @author khotyn 7/25/14 8:15 PM
 */
public class ApplicationRuntimeModel implements IsleDeploymentModel {
    /** deploys */
    private final List<DeploymentDescriptor>        deploys         = new ArrayList<>();
    /** inactive deploys */
    private final List<DeploymentDescriptor>        inactiveDeploys = new ArrayList<>();
    /** failed deployments */
    private final List<DeploymentDescriptor>        failed          = new CopyOnWriteArrayList<>();
    /** installed deployments */
    private final List<DeploymentDescriptor>        installed       = new CopyOnWriteArrayList<>();
    /** module name to deployment */
    private final Map<String, DeploymentDescriptor> deploymentMap   = new LinkedHashMap<>();
    /** deploy registry */
    private final DeployRegistry                    deployRegistry  = new DeployRegistry();
    /** module deployment validator */
    private ModuleDeploymentValidator               moduleDeploymentValidator;
    /** application name */
    private String                                  appName;
    /** resolved deployments */
    private List<DeploymentDescriptor>              resolvedDeployments;

    private SofaRuntimeContext                      sofaRuntimeContext;

    public SofaRuntimeContext getSofaRuntimeContext() {
        return sofaRuntimeContext;
    }

    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    public void setModuleDeploymentValidator(ModuleDeploymentValidator moduleDeploymentValidator) {
        this.moduleDeploymentValidator = moduleDeploymentValidator;
    }

    public boolean isModuleDeployment(DeploymentDescriptor deploymentDescriptor) {
        return this.moduleDeploymentValidator.isModuleDeployment(deploymentDescriptor);
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

    public DeploymentDescriptor getDeploymentByName(String springParent) {
        return deploymentMap.get(springParent);
    }

    @Deprecated
    public DeploymentDescriptor getSpringPoweredDeployment(String springParent) {
        return deploymentMap.get(springParent);
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

    @Override
    @NonNull
    public Map<String, ApplicationContext> getModuleApplicationContextMap() {
        Map<String, ApplicationContext> result = new HashMap<>(8);
        installed.forEach(deploymentDescriptor -> result.put(deploymentDescriptor.getModuleName(), deploymentDescriptor.getApplicationContext()));
        return result;
    }
}
