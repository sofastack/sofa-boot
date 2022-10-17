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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.deployment.impl.DefaultModuleDeploymentValidator;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author fengqi.lin
 * @author yangyanzhao
 * @version $Id: ModelCreatingStage.java, v 0.1 2012-3-16 14:17:48 fengqi.lin Exp $
 */
public class ModelCreatingStage extends AbstractPipelineStage {
    private final boolean                    allowModuleOverriding;

    protected final SofaModuleProfileChecker sofaModuleProfileChecker;

    public ModelCreatingStage(AbstractApplicationContext applicationContext,
                              SofaModuleProperties sofaModuleProperties,
                              SofaModuleProfileChecker sofaModuleProfileChecker) {
        super(applicationContext);
        this.allowModuleOverriding = sofaModuleProperties.isAllowModuleOverriding();
        this.sofaModuleProfileChecker = sofaModuleProfileChecker;
    }

    @Override
    protected void doProcess() throws Exception {
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setAppName(appName);

        SofaRuntimeManager sofaRuntimeManager = applicationContext
            .getBean(SofaRuntimeManager.class);
        application.setSofaRuntimeContext(sofaRuntimeManager.getSofaRuntimeContext());

        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        getAllDeployments(application);
        applicationContext.getBeanFactory().registerSingleton(SofaBootConstants.APPLICATION,
            application);
    }

    protected void getAllDeployments(ApplicationRuntimeModel application) throws IOException,
                                                                         DeploymentException {
        Enumeration<URL> urls = appClassLoader.getResources(SofaBootConstants.SOFA_MODULE_FILE);
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
                    validateDuplicateModule(application.addDeployment(dd), dd);
                } else {
                    application.addInactiveDeployment(dd);
                }
            }
        }
    }

    protected void validateDuplicateModule(DeploymentDescriptor exist, DeploymentDescriptor dd)
                                                                                               throws DeploymentException {
        if (exist != null) {
            if (isAllowModuleOverriding()) {
                SofaLogger.warn("Overriding module deployment for module name '"
                                + dd.getModuleName() + "': replacing '" + exist.getName()
                                + "' with '" + dd.getName() + "'");
            } else {
                throw new DeploymentException(ErrorCode.convert("01-11006", dd.getModuleName(),
                    exist.getName(), dd.getName()));
            }
        }
    }

    protected boolean isAllowModuleOverriding() {
        return this.allowModuleOverriding;
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
