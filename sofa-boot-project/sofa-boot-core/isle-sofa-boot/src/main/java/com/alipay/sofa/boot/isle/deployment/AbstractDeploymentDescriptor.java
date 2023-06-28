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

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Base implementation of {@link DeploymentDescriptor} to create module for url.
 *
 * @author yangyanzhao
 * @version $Id: AbstractDeploymentDescriptor.java, v 0.1 2012-2-1 15:13:00 yangyanzhao Exp $
 */
public abstract class AbstractDeploymentDescriptor implements DeploymentDescriptor {

    protected final Properties                        properties;

    protected final DeploymentDescriptorConfiguration deploymentDescriptorConfiguration;

    protected final ClassLoader                       classLoader;

    protected final URL                               url;

    protected final List<String>                      installedSpringXml = new ArrayList<>();

    protected final Map<String, Resource>             springResources    = new HashMap<>();

    protected final String                            moduleName;

    protected final String                            name;

    protected final List<String>                      requiredModules;

    protected final String                            parentModule;

    protected ApplicationContext                      applicationContext;

    protected long                                    startTime;

    protected long                                    elapsedTime;

    private boolean                                   ignoreRequireModule;

    public AbstractDeploymentDescriptor(URL url,
                                        Properties properties,
                                        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                        ClassLoader classLoader) {
        this.url = url;
        this.properties = properties;
        this.deploymentDescriptorConfiguration = deploymentDescriptorConfiguration;
        this.classLoader = classLoader;
        this.moduleName = getModuleNameFromProperties();
        this.name = getNameFormUrl();
        this.parentModule = getSpringParentFromProperties();
        this.requiredModules = getRequiredModulesFromProperties();
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getRequiredModules() {
        if (ignoreRequireModule) {
            return null;
        }
        return requiredModules;
    }

    @Override
    public String getSpringParent() {
        return parentModule;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public Map<String, Resource> getSpringResources() {
        return springResources;
    }

    @Override
    public boolean isSpringPowered() {
        return !springResources.isEmpty();
    }

    @Override
    public void addInstalledSpringXml(String fileName) {
        installedSpringXml.add(fileName);
    }

    @Override
    public void startDeploy() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void deployFinish() {
        elapsedTime = System.currentTimeMillis() - startTime;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public List<String> getInstalledSpringXml() {
        Collections.sort(installedSpringXml);
        return installedSpringXml;
    }

    @Override
    public int compareTo(DeploymentDescriptor o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public void setIgnoreRequireModule(boolean ignoreRequireModule) {
        this.ignoreRequireModule = ignoreRequireModule;
    }

    protected String getModuleNameFromProperties() {
        List<String> moduleNameIdentities = deploymentDescriptorConfiguration
            .getModuleNameIdentities();

        if (CollectionUtils.isEmpty(moduleNameIdentities)) {
            return null;
        }

        for (String moduleNameIdentity : moduleNameIdentities) {
            List<String> moduleNames = getFormattedModuleInfo(moduleNameIdentity);
            if (!CollectionUtils.isEmpty(moduleNames)) {
                return moduleNames.get(0);
            }
        }
        return null;
    }

    protected String getNameFormUrl() {
        int jarIndex = url.toString().lastIndexOf(".jar");
        if (jarIndex == -1) {
            String moduleName = getModuleName();
            return moduleName == null ? "" : moduleName;
        }

        String jarPath = url.toString().substring(0, jarIndex + ".jar".length());
        int lastIndex = jarPath.lastIndexOf("/");
        return jarPath.substring(lastIndex + 1);
    }

    protected List<String> getRequiredModulesFromProperties() {
        List<String> requires = new ArrayList<>();

        List<String> requireModuleIdentities = deploymentDescriptorConfiguration
            .getRequireModuleIdentities();

        if (CollectionUtils.isEmpty(requireModuleIdentities)) {
            return requires;
        }

        for (String requireModuleIdentity : requireModuleIdentities) {
            List<String> requireModules = getFormattedModuleInfo(requireModuleIdentity);
            if (!CollectionUtils.isEmpty(requireModules)) {
                requires.addAll(requireModules);
                break;
            }
        }

        String springParent = getSpringParent();
        if (StringUtils.hasText(springParent)) {
            requires.add(springParent);
        }
        return requires.stream().distinct().collect(Collectors.toList());
    }

    protected String getSpringParentFromProperties() {
        List<String> name = getFormattedModuleInfo(DeploymentDescriptorConfiguration.SPRING_PARENT);
        return CollectionUtils.isEmpty(name) ? null : name.get(0);
    }

    protected List<String> getFormattedModuleInfo(String key) {
        String ret = properties.getProperty(key);
        if (StringUtils.hasText(ret)) {
            String[] array = StringUtils.commaDelimitedListToStringArray(ret);
            List<String> list = new ArrayList<>(array.length);
            for (String item : array) {
                int idx = item.indexOf(';');
                if (idx > -1) {
                    item = item.substring(0, idx);
                }
                list.add(item.trim());
            }
            return list;
        }
        return null;
    }

    /**
     * Actually load spring xml resources.
     */
    protected abstract void loadSpringXMLs();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractDeploymentDescriptor that)) {
            return false;
        }
        return Objects.equals(this.getModuleName(), that.getModuleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModuleName());
    }
}
