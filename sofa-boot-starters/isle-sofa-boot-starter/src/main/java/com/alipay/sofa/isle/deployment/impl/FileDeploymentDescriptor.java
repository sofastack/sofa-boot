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
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants.SOFA_MODULE_FILE;

/**
 *
 * @author yangyanzhao
 * @version $Id: FileDescriptor.java, v 0.1 2012-1-11 17:41:52 yangyanzhao Exp $
*/
public class FileDeploymentDescriptor extends AbstractDeploymentDescriptor {
    public FileDeploymentDescriptor(URL url,
                                    Properties props,
                                    DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                    ClassLoader classLoader) {
        super(url, props, deploymentDescriptorConfiguration, classLoader);
    }

    @Override
    public void loadSpringXMLs() {
        springResources = new HashMap<>();
        File springXml = new File(url.getFile().substring(0,
            url.getFile().length() - SOFA_MODULE_FILE.length()),
            SofaModuleFrameworkConstants.SPRING_CONTEXT_PATH);
        List<File> springFiles = new ArrayList<>();
        if (springXml.exists()) {
            listFiles(springFiles, springXml, ".xml");
        }
        try {
            for (File f : springFiles) {
                springResources.put(f.getAbsolutePath(), new FileSystemResource(f));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void listFiles(List<File> subFiles, File parent, String suffix) {
        File[] files = parent.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(suffix)) {
                subFiles.add(f);
            } else if (f.isDirectory()) {
                listFiles(subFiles, f, suffix);
            }
        }
    }
}
