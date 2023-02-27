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
import com.alipay.sofa.boot.isle.SampleDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DefaultModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link ModelCreatingStage}.
 *
 * @author huzijie
 * @version ModelCreatingStageTests.java, v 0.1 2023年02月02日 8:25 PM huzijie Exp $
 */
public class ModelCreatingStageTests {

    private final ModelCreatingStage stage = new ModelCreatingStage();

    @BeforeEach
    public void init() {
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        DefaultSofaModuleProfileChecker checker = new DefaultSofaModuleProfileChecker();
        checker.init();
        application.setSofaModuleProfileChecker(checker);
        stage.setApplicationRuntimeModel(application);
    }

    @Test
    public void duplicatedModule() {
        List<DeploymentDescriptor> deploymentDescriptorList = new ArrayList<>();
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.test");
        deploymentDescriptorList.add(SampleDeploymentDescriptor.create(props));

        props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.test");
        props.setProperty("xmlName", "com.alipay.test");
        deploymentDescriptorList.add(SampleDeploymentDescriptor.create(props));

        assertThatThrownBy(() -> stage.addDeploymentDescriptors(deploymentDescriptorList))
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("11006");
    }

    @Test
    public void duplicatedModuleUseSampleXml() {
        List<DeploymentDescriptor> deploymentDescriptorList = new ArrayList<>();
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.test");
        deploymentDescriptorList.add(SampleDeploymentDescriptor.create(props));

        props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.test");
        deploymentDescriptorList.add(SampleDeploymentDescriptor.create(props));

        Throwable throwable = catchThrowable(() -> stage.addDeploymentDescriptors(deploymentDescriptorList));
        assertThat(throwable).isNull();
    }

    @Test
    public void requireModuleMiss() throws DeploymentException {
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.test");
        props.setProperty(DeploymentDescriptorConfiguration.REQUIRE_MODULE, "com.alipay.dependency");
        List<DeploymentDescriptor> deploymentDescriptorList = new ArrayList<>();
        deploymentDescriptorList.add(SampleDeploymentDescriptor.create(props));

        stage.addDeploymentDescriptors(deploymentDescriptorList);
        assertThatThrownBy(stage::outputModulesMessage)
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("01-12000");
    }
}
