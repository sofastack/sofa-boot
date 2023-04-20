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

import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version MockDeploymentDescriptor.java, v 0.1 2023年04月07日 11:28 AM huzijie Exp $
 */
public class MockDeploymentDescriptor implements DeploymentDescriptor {

    private final String          name;

    private ApplicationContext    applicationContext;

    private final List<String>    requireModules = new ArrayList<>();

    private String                springParent;

    private Map<String, Resource> springResources;
    private List<String>          installedSpringXml;

    public MockDeploymentDescriptor(String name) {
        this.name = name;
    }

    public void addRequiredModule(String name) {
        requireModules.add(name);
    }

    public void setSpringParent(String springParent) {
        this.springParent = springParent;
    }

    public void setSpringResources(Map<String, Resource> springResources) {
        this.springResources = springResources;
    }

    @Override
    public String getModuleName() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getRequiredModules() {
        return requireModules;
    }

    @Override
    public String getProperty(String key) {
        return null;
    }

    @Override
    public String getSpringParent() {
        return springParent;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void addInstalledSpringXml(String fileName) {
        if (installedSpringXml == null) {
            installedSpringXml = new ArrayList<>();
        }
        installedSpringXml.add(fileName);
    }

    @Override
    public List<String> getInstalledSpringXml() {
        return installedSpringXml;
    }

    @Override
    public boolean isSpringPowered() {
        return false;
    }

    @Override
    public void startDeploy() {

    }

    @Override
    public void deployFinish() {

    }

    @Override
    public Map<String, Resource> getSpringResources() {
        return springResources;
    }

    @Override
    public long getElapsedTime() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public void setIgnoreRequireModule(boolean ignoreRequireModule) {

    }

    @Override
    public int compareTo(DeploymentDescriptor o) {
        return o.getName().compareTo(this.getName());
    }
}
