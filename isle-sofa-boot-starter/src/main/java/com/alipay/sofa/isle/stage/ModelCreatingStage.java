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
package com.alipay.sofa.isle.stage;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.impl.DefaultModuleDeploymentValidator;
import com.alipay.sofa.isle.utils.SofaModuleProfileUtil;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author fengqi.lin
 * @author yangyanzhao
 * @version $Id: ModelCreatingStage.java, v 0.1 2012-3-16 14:17:48 fengqi.lin Exp $
 */
public class ModelCreatingStage extends AbstractPipelineStage {
    public ModelCreatingStage(AbstractApplicationContext applicationContext) {
        super(applicationContext);
    }

    protected void doProcess() throws Exception {
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setAppName(appName);
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        getAllDeployments(application);
        applicationContext.getBeanFactory().registerSingleton(
            SofaModuleFrameworkConstants.APPLICATION, application);
    }

    private void getAllDeployments(ApplicationRuntimeModel application) throws IOException {
        Enumeration<URL> urls = appClassLoader
            .getResources(SofaModuleFrameworkConstants.SOFA_MODULE_FILE);
        if (urls == null || !urls.hasMoreElements()) {
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            UrlResource urlResource = new UrlResource(url);
            Properties props = new Properties();
            props.load(urlResource.getInputStream());
            DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                Collections.singletonList(SofaModuleFrameworkConstants.MODULE_NAME),
                Collections.singletonList(SofaModuleFrameworkConstants.REQUIRE_MODULE));
            DeploymentDescriptor dd = DeploymentBuilder.build(url, props,
                deploymentDescriptorConfiguration, appClassLoader);

            if (application.isModuleDeployment(dd)) {
                if (SofaModuleProfileUtil.acceptProfile(applicationContext, dd)) {
                    application.addDeployment(dd);
                } else {
                    application.addInactiveDeployment(dd);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "ModelCreatingStage";
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
