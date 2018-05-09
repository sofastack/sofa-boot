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

import java.util.*;

/**
 *
 * @author yangyanzhao
 * @version $Id: DeployRegistry.java, v 0.1 2012-1-12 上午12:34:44 yangyanzhao Exp
 *          $
 */
public class DeployRegistry extends DependencyTree<String, DeploymentDescriptor> {
    // this is needed to handle requiredBy dependencies
    private final Map<String, DeploymentDescriptor> deployments = Collections
                                                                    .synchronizedSortedMap(new TreeMap<String, DeploymentDescriptor>());

    public void add(DeploymentDescriptor deployment) {
        deployments.put(deployment.getName(), deployment);
    }

    public void remove(String key) {
        deployments.remove(key);
    }

    @Override
    public List<Entry<String, DeploymentDescriptor>> getResolvedEntries() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getResolvedEntries();
    }

    @Override
    public List<Entry<String, DeploymentDescriptor>> getMissingRequirements() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getMissingRequirements();
    }

    @Override
    public DeploymentDescriptor get(String key) {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.get(key);
    }

    @Override
    public Collection<Entry<String, DeploymentDescriptor>> getEntries() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getEntries();
    }

    @Override
    public List<DeploymentDescriptor> getResolvedObjects() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getResolvedObjects();
    }

    @Override
    public List<DeploymentDescriptor> getPendingObjects() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getPendingObjects();
    }

    @Override
    public Entry<String, DeploymentDescriptor> getEntry(String key) {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getEntry(key);
    }

    @Override
    public List<Entry<String, DeploymentDescriptor>> getPendingEntries() {
        if (!deployments.isEmpty()) {
            commitDeployments();
        }
        return super.getPendingEntries();
    }

    private void commitDeployments() {
        for (DeploymentDescriptor fd : deployments.values()) {
            add(fd.getModuleName(), fd, fd.getRequiredModules());
        }
        deployments.clear();
    }
}
