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

import com.alipay.sofa.isle.deployment.impl.FileDeploymentDescriptor;
import com.alipay.sofa.isle.deployment.impl.JarDeploymentDescriptor;
import org.springframework.util.ResourceUtils;
import java.net.URL;
import java.util.Properties;

/**
 * SOFABoot Module deployment creator
 *
 * @author yangyanzhao
 * @version $Id: DeploymentBuilder.java, v 0.1 2012-1-11 07:40:11 yangyanzhao Exp $
 */
public class DeploymentBuilder {
    /**
     * build a SOFABoot Module deployment descriptor
     *
     * @param url SOFABoot module file url
     * @param props properties
     * @param deploymentDescriptorConfiguration deployment descriptor configuration
     * @return deployment descriptor
     */
    public static DeploymentDescriptor build(URL url,
                                             Properties props,
                                             DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                             ClassLoader classLoader) {
        if (ResourceUtils.isJarURL(url)) {
            return new JarDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
                classLoader);
        } else {
            return new FileDeploymentDescriptor(url, props, deploymentDescriptorConfiguration,
                classLoader);
        }
    }
}
