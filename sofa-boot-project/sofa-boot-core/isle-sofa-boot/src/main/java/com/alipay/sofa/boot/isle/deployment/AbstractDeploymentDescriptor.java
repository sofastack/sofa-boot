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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

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
    private final List<String>                        installedSpringXml = new ArrayList<>();
    private ApplicationContext                        applicationContext;
    private long                                      startTime;
    private long                                      elapsedTime;
    protected Map<String, Resource>                   springResources;
    protected boolean                                 ignoreRequireModule;

    public AbstractDeploymentDescriptor(URL url,
                                        Properties properties,
                                        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                        ClassLoader classLoader) {
        this.url = url;
        this.properties = properties;
        this.deploymentDescriptorConfiguration = deploymentDescriptorConfiguration;
        this.classLoader = classLoader;
    }

    @Override
    public String getModuleName() {
        List<String> moduleNameIdentities = deploymentDescriptorConfiguration
            .getModuleNameIdentities();

        return (String) Stream.of(moduleNameIdentities)
                .filter(ele -> ele != null && ele.size() != 0).flatMap(Collection::stream)
                .map(properties::get).filter(name -> StringUtils.hasText((String) name)).findFirst()
                .orElse(null);
    }

    @Override
    public String getName() {
        int jarIndex = url.toString().lastIndexOf(".jar");
        if (jarIndex == -1) {
            String moduleName = getModuleName();
            return moduleName == null ? "" : moduleName;
        }

        String jarPath = url.toString().substring(0, jarIndex + ".jar".length());
        int lastIndex = jarPath.lastIndexOf("/");
        return jarPath.substring(lastIndex + 1);
    }

    @Override
    public int compareTo(DeploymentDescriptor o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public List<String> getRequiredModules() {
        List<String> requires = new ArrayList<>();
        if (ignoreRequireModule || !isSpringPowered()) {
            return requires;
        }

        List<String> requireModuleIdentities = deploymentDescriptorConfiguration
            .getRequireModuleIdentities();

        if (requireModuleIdentities == null || requireModuleIdentities.size() == 0) {
            return requires;
        }

        for (String requireModuleIdentity : requireModuleIdentities) {
            requires = getFormattedModuleInfo(requireModuleIdentity);
            if (!CollectionUtils.isEmpty(requires)) {
                break;
            }
        }

        String springParent = getSpringParent();
        if (springParent != null) {
            if (requires == null) {
                requires = new ArrayList<>(1);
            }
            requires.add(springParent);
        }
        return requires;
    }

    @Override
    public void setIgnoreRequireModule(boolean ignoreRequireModule) {
        this.ignoreRequireModule = ignoreRequireModule;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getSpringParent() {
        List<String> name = getFormattedModuleInfo(DeploymentDescriptorConfiguration.SPRING_PARENT);
        return name == null ? null : name.get(0);
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
        if (springResources == null) {
            loadSpringXMLs();
        }
        return springResources;
    }

    @Override
    public void addInstalledSpringXml(String fileName) {
        installedSpringXml.add(fileName);
    }

    @Override
    public boolean isSpringPowered() {
        if (springResources == null) {
            this.loadSpringXMLs();
        }
        return !springResources.isEmpty();
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

    private List<String> getFormattedModuleInfo(String key) {
        String ret = properties.getProperty(key);
        if (ret == null || ret.length() == 0) {
            return null;
        }

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
