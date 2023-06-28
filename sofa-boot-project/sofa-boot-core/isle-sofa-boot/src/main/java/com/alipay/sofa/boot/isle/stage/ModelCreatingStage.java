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

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DependencyTree;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorFactory;
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Stage to found sofa modules in classpath.
 *
 * @author fengqi.lin
 * @author yangyanzhao
 * @author huzijie
 * @version $Id: ModelCreatingStage.java, v 0.1 2012-3-16 14:17:48 fengqi.lin Exp $
 */
public class ModelCreatingStage extends AbstractPipelineStage {

    private static final Logger           LOGGER                        = SofaBootLoggerFactory
                                                                            .getLogger(ModelCreatingStage.class);

    public static final String            MODEL_CREATING_STAGE_NAME     = "ModelCreatingStage";

    protected final List<String>          ignoreModules                 = new ArrayList<>();

    protected final List<String>          ignoreCalculateRequireModules = new ArrayList<>();

    protected DeploymentDescriptorFactory deploymentDescriptorFactory   = new DeploymentDescriptorFactory();

    protected boolean                     allowModuleOverriding;

    @Override
    protected void doProcess() throws Exception {
        getAllDeployments();
        outputModulesMessage();
    }

    protected void getAllDeployments() throws IOException, DeploymentException {
        String modulePropertyFileName = DeploymentDescriptorConfiguration.SOFA_MODULE_FILE;
        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
            Collections.singletonList(DeploymentDescriptorConfiguration.MODULE_NAME),
            Collections.singletonList(DeploymentDescriptorConfiguration.REQUIRE_MODULE));

        List<DeploymentDescriptor> deploymentDescriptors = getDeploymentDescriptors(
            modulePropertyFileName, deploymentDescriptorConfiguration);

        addDeploymentDescriptors(deploymentDescriptors);
    }

    protected List<DeploymentDescriptor> getDeploymentDescriptors(String modulePropertyFileName,
                                                                  DeploymentDescriptorConfiguration deploymentDescriptorConfiguration)
                                                                                                                                      throws IOException {
        List<DeploymentDescriptor> deploymentDescriptors = new ArrayList<>();

        Enumeration<URL> urls = appClassLoader.getResources(modulePropertyFileName);
        if (urls == null || !urls.hasMoreElements()) {
            return deploymentDescriptors;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            UrlResource urlResource = new UrlResource(url);
            Properties props = loadPropertiesFormUrlResource(urlResource);
            DeploymentDescriptor deploymentDescriptor = createDeploymentDescriptor(url, props,
                deploymentDescriptorConfiguration, appClassLoader, modulePropertyFileName);
            if (ignoreCalculateRequireModules.contains(deploymentDescriptor.getModuleName())) {
                deploymentDescriptor.setIgnoreRequireModule(true);
            }
            deploymentDescriptors.add(deploymentDescriptor);
        }
        return deploymentDescriptors;
    }

    protected Properties loadPropertiesFormUrlResource(UrlResource urlResource) throws IOException {
        Properties props = new Properties();
        props.load(urlResource.getInputStream());
        return props;
    }

    protected DeploymentDescriptor createDeploymentDescriptor(URL url,
                                                              Properties props,
                                                              DeploymentDescriptorConfiguration deploymentDescriptorConfiguration,
                                                              ClassLoader classLoader,
                                                              String modulePropertyName) {
        return deploymentDescriptorFactory.build(url, props, deploymentDescriptorConfiguration,
            classLoader, modulePropertyName);
    }

    protected void addDeploymentDescriptors(List<DeploymentDescriptor> deploymentDescriptors)
                                                                                             throws DeploymentException {
        for (DeploymentDescriptor dd : deploymentDescriptors) {
            if (application.isModuleDeployment(dd)) {
                if (application.acceptModule(dd) && !ignoreModules.contains(dd.getModuleName())) {
                    if (dd.isSpringPowered()) {
                        if (validateDuplicateModule(dd)) {
                            application.addDeployment(dd);
                        }
                    } else {
                        application.addNoSpringPoweredDeployment(dd);
                    }
                } else {
                    application.addInactiveDeployment(dd);
                }
            }
        }
    }

    protected boolean validateDuplicateModule(DeploymentDescriptor dd) throws DeploymentException {
        DeploymentDescriptor exist = application.getDeploymentByName(dd.getModuleName());
        if (exist == null) {
            return true;
        }
        if (Objects.equals(dd.getSpringResources(), exist.getSpringResources())) {
            return false;
        }
        if (allowModuleOverriding) {
            return true;
        }
        throw new DeploymentException(ErrorCode.convert("01-11006", dd.getModuleName(),
            exist.getName(), dd.getName()));
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
        LOGGER.info(stringBuilder.toString());

        String errorMessage = getErrorMessageByApplicationModule(application);
        if (StringUtils.hasText(errorMessage)) {
            LOGGER.error(errorMessage);
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

    public void addIgnoreModule(String moduleName) {
        this.ignoreModules.add(moduleName);
    }

    public void addIgnoredCalculateRequireModule(String moduleName) {
        this.ignoreCalculateRequireModules.add(moduleName);
    }

    public List<String> getIgnoreModules() {
        return ignoreModules;
    }

    public List<String> getIgnoreCalculateRequireModules() {
        return ignoreCalculateRequireModules;
    }

    public boolean isAllowModuleOverriding() {
        return allowModuleOverriding;
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
