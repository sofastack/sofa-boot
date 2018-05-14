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

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * SOFABoot Module Deployment Descriptor.
 *
 * @author yangyanzhao
 * @version $Id: DeploymentDescriptor.java, v 0.1 2012-1-10 12:28:28 yangyanzhao Exp $
 */
public interface DeploymentDescriptor extends Comparable<DeploymentDescriptor> {
    /**
     * get SOFABoot Module Name.
     *
     * @return SOFABoot Module Name
     */
    String getModuleName();

    /**
     * get Deployment Descriptor Name.
     *
     * @return Deployment Descriptor Name
     */
    String getName();

    /**
     * get modules that dependent on.
     *
     * @return modules that dependent on.
     */
    List<String> getRequiredModules();

    /**
     * get Property.
     *
     * @param key property key
     * @return property value
     */
    String getProperty(String key);

    /**
     * get Spring Parent of this SOFABoot module.
     *
     * @return Spring Parent of this SOFABoot module
     */
    String getSpringParent();

    /**
     * get classloader of this SOFABoot module.
     *
     * @return classloader of this SOFABoot module
     */
    ClassLoader getClassLoader();

    /**
     * set application context of this SOFABoot module.
     *
     * @param context application context of this SOFABoot module.
     */
    void setApplicationContext(ApplicationContext context);

    /**
     * get application context of this SOFABoot module.
     *
     * @return application context of this SOFABoot module
     */
    ApplicationContext getApplicationContext();

    /**
     * add installed spring xml of this SOFABoot module.
     *
     * @param fileName spring xml filename
     */
    void addInstalledSpringXml(String fileName);

    /**
     * get all installed spring xml files of this SOFABoot module.
     *
     * @return installed Spring xml
     */
    List<String> getInstalledSpringXml();

    /**
     * determine whether this SOFABoot module is spring powered.
     *
     * @return true or false
     */
    boolean isSpringPowered();

    /**
     * invoke when deploy this SOFABoot module.
     */
    void startDeploy();

    /**
     * invoke when deploy finished.
     */
    void deployFinish();

    /**
     * get Spring resources of this SOFABoot module.
     *
     * @return spring resources
     */
    Map<String, Resource> getSpringResources();

    /**
     * get deploy elapsed time of this SOFABoot module.
     *
     * @return elapsed time
     */
    long getElapsedTime();

    /**
     * get start deploy time of this SOFABoot module.
     *
     * @return start time
     */
    long getStartTime();
}
