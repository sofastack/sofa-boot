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
package com.alipay.sofa.boot.isle.stage;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DependencyTree;
import com.alipay.sofa.boot.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.log.SofaLogger;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Stage to found sofa modules in classpath
 *
 * @author fengqi.lin
 * @author yangyanzhao
 * @author huzijie
 * @version $Id: ModelCreatingStage.java, v 0.1 2012-3-16 14:17:48 fengqi.lin Exp $
 */
public class ModelCreatingStage extends AbstractPipelineStage {

    public static final String MODEL_CREATING_STAGE_NAME = "ModelCreatingStage";

    protected boolean          allowModuleOverriding;

    @Override
    protected void doProcess() throws Exception {
        getAllDeployments();
        outputModulesMessage();
    }

    protected void getAllDeployments() throws IOException, DeploymentException {
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
                if (application.acceptModule(dd)) {
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

    protected void outputModulesMessage() throws DeploymentException {
        StringBuilder stringBuilder = new StringBuilder();
        if (application.getAllInactiveDeployments().size() > 0) {
            writeMessageToStringBuilder(stringBuilder, application.getAllInactiveDeployments(),
                "All unactivated module list");
        }
        writeMessageToStringBuilder(stringBuilder, application.getAllDeployments(),
            "All activated module list");
        writeMessageToStringBuilder(stringBuilder, application.getResolvedDeployments(),
            "Modules that could install");
        SofaLogger.info(stringBuilder.toString());

        String errorMessage = getErrorMessageByApplicationModule(application);
        if (StringUtils.hasText(errorMessage)) {
            SofaLogger.error(errorMessage);
        }

        if (application.getDeployRegistry().getPendingEntries().size() > 0) {
            throw new DeploymentException(errorMessage.trim());
        }
    }

    protected String getErrorMessageByApplicationModule(ApplicationRuntimeModel application) {
        StringBuilder sbError = new StringBuilder(512);
        if (application.getDeployRegistry().getPendingEntries().size() > 0) {
            sbError.append("\n").append(ErrorCode.convert("01-12000")).append("(")
                .append(application.getDeployRegistry().getPendingEntries().size())
                .append(") >>>>>>>>\n");

            for (DependencyTree.Entry<String, DeploymentDescriptor> entry : application
                .getDeployRegistry().getPendingEntries()) {
                if (application.getAllDeployments().contains(entry.get())) {
                    sbError.append("[").append(entry.getKey()).append("]").append(" depends on ")
                        .append(entry.getWaitsFor())
                        .append(", but the latter can not be resolved.").append("\n");
                }
            }
        }

        if (application.getDeployRegistry().getMissingRequirements().size() > 0) {
            sbError.append("Missing modules").append("(")
                .append(application.getDeployRegistry().getMissingRequirements().size())
                .append(") >>>>>>>>\n");

            for (DependencyTree.Entry<String, DeploymentDescriptor> entry : application
                .getDeployRegistry().getMissingRequirements()) {
                sbError.append("[").append(entry.getKey()).append("]").append("\n");
            }

            sbError.append("Please add the corresponding modules.").append("\n");
        }

        return sbError.toString();
    }

    protected void writeMessageToStringBuilder(StringBuilder sb,
                                               List<DeploymentDescriptor> deploys, String info) {
        int size = deploys.size();
        sb.append("\n").append(info).append("(").append(size).append(") >>>>>>>\n");

        for (int i = 0; i < size; ++i) {
            String symbol = i == size - 1 ? "  └─ " : "  ├─ ";
            sb.append(symbol).append(deploys.get(i).getName()).append("\n");
        }
    }

    protected boolean isAllowModuleOverriding() {
        return this.allowModuleOverriding;
    }

    public void setAllowModuleOverriding(boolean allowModuleOverriding) {
        this.allowModuleOverriding = allowModuleOverriding;
    }

    @Override
    public String getName() {
        return MODEL_CREATING_STAGE_NAME;
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
