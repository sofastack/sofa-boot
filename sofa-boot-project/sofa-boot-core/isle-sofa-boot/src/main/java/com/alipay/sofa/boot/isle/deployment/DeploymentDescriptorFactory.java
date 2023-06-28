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

import org.springframework.util.ResourceUtils;

import java.net.URL;
import java.util.Properties;

/**
 * SOFABoot Module deployment creator.
 *
 * @author yangyanzhao
 * @version $Id: DeploymentBuilder.java, v 0.1 2012-1-11 07:40:11 yangyanzhao Exp $
 */
public class DeploymentDescriptorFactory {

    /**
     * Build a SOFABoot Module deployment descriptor.
     *
     * @param url SOFABoot module file url
     * @param props properties
     * @param deploymentDescriptorConfiguration deployment descriptor configuration
     * @param modulePropertyName moduleProperty file
     * @return deployment descriptor
     */
    public DeploymentDescriptor build(URL url,
                                      Properties props,
                                      DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                      ClassLoader classLoader, String modulePropertyName) {
        if (ResourceUtils.isJarURL(url)) {
            return createJarDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
                classLoader);
        } else {
            return createFileDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
                classLoader, modulePropertyName);
        }
    }

    protected DeploymentDescriptor createJarDeploymentDescriptor(URL url,
                                                                 Properties props,
                                                                 DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                                                 ClassLoader classLoader) {
        return new JarDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
            classLoader);
    }

    protected DeploymentDescriptor createFileDeploymentDescriptor(URL url,
                                                                  Properties props,
                                                                  DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                                                  ClassLoader classLoader,
                                                                  String modulePropertyName) {
        return new FileDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
            classLoader, modulePropertyName);
    }
}
