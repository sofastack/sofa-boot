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
package com.alipay.sofa.isle.deployment.impl;

import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author yangyanzhao
 * @version $Id: JarDescriptor.java, v 0.1 2012-1-11 17:43:17 yangyanzhao Exp $
 */
public class JarDeploymentDescriptor extends AbstractDeploymentDescriptor {
    public JarDeploymentDescriptor(URL url,
                                   Properties props,
                                   DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                   ClassLoader classLoader) {
        super(url, props, deploymentDescriptorConfiguration, classLoader);
    }

    @Override
    public void loadSpringXMLs() {
        springResources = new HashMap<>();
        JarFile jarFile;

        try {
            URLConnection con = url.openConnection();
            Assert.isInstanceOf(JarURLConnection.class, con);
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();

            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(SofaModuleFrameworkConstants.SPRING_CONTEXT_PATH)
                    && entryPath.endsWith("xml")) {
                    String fileName = entry.getName().substring(
                        SofaModuleFrameworkConstants.SPRING_CONTEXT_PATH.length() + 1);
                    springResources.put(fileName,
                        convertToByteArrayResource(jarFile.getInputStream(entry)));
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private ByteArrayResource convertToByteArrayResource(InputStream inputStream) {
        try {
            int nRead;
            byte[] data = new byte[2048];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return new ByteArrayResource(buffer.toByteArray());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
