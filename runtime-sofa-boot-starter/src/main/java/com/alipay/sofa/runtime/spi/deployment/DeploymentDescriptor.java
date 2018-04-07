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
package com.alipay.sofa.runtime.spi.deployment;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuanbei 18/3/1
 */
public interface DeploymentDescriptor extends Comparable<DeploymentDescriptor> {
    String getModuleName();

    String getName();

    List<String> getRequiredModules();

    String getProperty(String key);

    String getSpringParent();

    ClassLoader getClassLoader();

    void setApplicationContext(ApplicationContext context);

    ApplicationContext getApplicationContext();

    void addInstalledSpringXml(String fileName);

    boolean isSpringPowered();

    void startDeploy();

    void deployFinish();

    Map<String, Resource> getSpringResources();

    void setSpringResources(Map<String, Resource> springXmls);

    long getElapsedTime();

    long getStartTime();

    List<String> getInstalledSpringXml();
}
