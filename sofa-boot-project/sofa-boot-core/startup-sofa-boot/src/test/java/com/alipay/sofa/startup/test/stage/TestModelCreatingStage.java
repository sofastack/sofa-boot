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
package com.alipay.sofa.startup.test.stage;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.isle.StartupModelCreatingStage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author huzijie
 * @version TestModelCreatingStage.java, v 0.1 2021年01月05日 10:17 上午 huzijie Exp $
 */
public class TestModelCreatingStage extends StartupModelCreatingStage {
    public TestModelCreatingStage(AbstractApplicationContext applicationContext,
                                  StartupReporter startupReporter) {
        super(applicationContext, startupReporter);
    }

    @Override
    protected void getAllDeployments(ApplicationRuntimeModel application) throws IOException {
        Enumeration<URL> urls = appClassLoader.getResources("META-INF/"
                                                            + SofaBootConstants.SOFA_MODULE_FILE);
        if (urls == null || !urls.hasMoreElements()) {
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            UrlResource urlResource = new UrlResource(url);
            Properties props = new Properties();
            props.load(urlResource.getInputStream());
            DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                Collections.singletonList(SofaBootConstants.MODULE_NAME),
                Collections.singletonList(SofaBootConstants.REQUIRE_MODULE));
            DeploymentDescriptor dd = DeploymentBuilder.build(url, props,
                deploymentDescriptorConfiguration, appClassLoader);

            if (application.isModuleDeployment(dd)) {
                if (sofaModuleProfileChecker.acceptModule(dd)) {
                    application.addDeployment(dd);
                } else {
                    application.addInactiveDeployment(dd);
                }
            }
        }
    }
}
