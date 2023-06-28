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

import com.alipay.sofa.boot.isle.deployment.AbstractDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import org.springframework.core.io.ByteArrayResource;

import java.net.URL;
import java.util.Collections;
import java.util.Properties;

/**
 * @author huzijie
 * @version SampleDeploymentDescriptor.java, v 0.1 2023年02月02日 7:51 PM huzijie Exp $
 */
public class SampleDeploymentDescriptor extends AbstractDeploymentDescriptor {

    private static final DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                                                                                                 Collections
                                                                                                     .singletonList(DeploymentDescriptorConfiguration.MODULE_NAME),
                                                                                                 Collections
                                                                                                     .singletonList(DeploymentDescriptorConfiguration.REQUIRE_MODULE));

    private static final URL                               defaultUrl                        = SampleDeploymentDescriptor.class
                                                                                                 .getResource("");

    public static SampleDeploymentDescriptor create(Properties properties) {
        return create(defaultUrl, properties);
    }

    public static SampleDeploymentDescriptor create(URL url, Properties properties) {
        return new SampleDeploymentDescriptor(url, properties, deploymentDescriptorConfiguration,
            SampleDeploymentDescriptor.class.getClassLoader());
    }

    public SampleDeploymentDescriptor(URL url,
                                      Properties props,
                                      DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                      ClassLoader classLoader) {
        super(url, props, deploymentDescriptorConfiguration, classLoader);
        loadSpringXMLs();
    }

    @Override
    protected void loadSpringXMLs() {
        this.springResources.put(properties.getProperty("xmlName", "sample"),
            new ByteArrayResource(new byte[] {}));
    }
}
