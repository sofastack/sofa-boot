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
import com.alipay.sofa.boot.isle.MockDeploymentDescriptor;
import com.alipay.sofa.boot.isle.SampleDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DefaultModuleDeploymentValidator;
import com.alipay.sofa.boot.isle.deployment.DependencyTree;
import com.alipay.sofa.boot.isle.deployment.DeployRegistry;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.boot.util.LogOutPutUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

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
@ExtendWith(OutputCaptureExtension.class)
public class ModelCreatingStageTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(ModelCreatingStage.class);
    }

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

    @Test
    void outputModulesMessage(CapturedOutput capturedOutput) throws DeploymentException {
        ApplicationRuntimeModel application = Mockito.mock(ApplicationRuntimeModel.class);
        DeployRegistry deployRegistry = Mockito.mock(DeployRegistry.class);
        Mockito.when(application.getDeployRegistry()).thenReturn(deployRegistry);
        Mockito.when(application.getAllInactiveDeployments()).thenReturn(List.of(
                new MockDeploymentDescriptor("inactive1"),
                new MockDeploymentDescriptor("inactive2")
        ));
        Mockito.when(application.getAllDeployments()).thenReturn(List.of(
                new MockDeploymentDescriptor("active1"),
                new MockDeploymentDescriptor("active2")
        ));
        Mockito.when(application.getResolvedDeployments()).thenReturn(List.of(
                new MockDeploymentDescriptor("resolved1"),
                new MockDeploymentDescriptor("resolved2")
        ));
        MockDeploymentDescriptor deploymentDescriptor1 = new MockDeploymentDescriptor("pending1");
        MockDeploymentDescriptor deploymentDescriptor2 = new MockDeploymentDescriptor("pending2");
        List<DependencyTree.Entry<String, DeploymentDescriptor>> pendingEntries = List.of(
                new DependencyTree.Entry<>("pending1", deploymentDescriptor1),
                new DependencyTree.Entry<>("pending2", deploymentDescriptor2));
        Mockito.when(application.getAllDeployments()).thenReturn(List.of(deploymentDescriptor1, deploymentDescriptor2));
        Mockito.when(application.getDeployRegistry().getPendingEntries()).thenReturn(pendingEntries);

        ModelCreatingStage stage = new ModelCreatingStage();
        stage.setApplicationRuntimeModel(application);

        assertThatThrownBy(stage::outputModulesMessage)
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("01-12000");

        assertThat(capturedOutput.getOut()).contains("All unactivated module list")
                .contains("inactive1").contains("inactive2")
                .contains("All activated module list")
                .contains("active1").contains("active2")
                .contains("Modules that could install")
                .contains("resolved1").contains("resolved2")
                .contains("01-12000").contains("pending1").contains("pending2")
                .contains("depends on").contains("can not be resolved");
    }

    @Test
    void testGetErrorMessageByApplicationModule() {
        ApplicationRuntimeModel application = Mockito.mock(ApplicationRuntimeModel.class);
        DeployRegistry deployRegistry = Mockito.mock(DeployRegistry.class);
        Mockito.when(application.getDeployRegistry()).thenReturn(deployRegistry);
        MockDeploymentDescriptor deploymentDescriptor1 = new MockDeploymentDescriptor("pending1");
        MockDeploymentDescriptor deploymentDescriptor2 = new MockDeploymentDescriptor("pending2");
        List<DependencyTree.Entry<String, DeploymentDescriptor>> pendingEntries = List.of(
            new DependencyTree.Entry<>("pending1", deploymentDescriptor1),
            new DependencyTree.Entry<>("pending2", deploymentDescriptor2));
        Mockito.when(application.getAllDeployments()).thenReturn(
            List.of(deploymentDescriptor1, deploymentDescriptor2));
        Mockito.when(application.getDeployRegistry().getPendingEntries())
            .thenReturn(pendingEntries);
        Mockito.when(application.getDeployRegistry().getMissingRequirements()).thenReturn(
            List.of(
                new DependencyTree.Entry<>("missing1", new MockDeploymentDescriptor("missing1")),
                new DependencyTree.Entry<>("missing2", new MockDeploymentDescriptor("missing2"))));

        ModelCreatingStage stage = new ModelCreatingStage();
        stage.setApplicationRuntimeModel(application);

        String errorMessage = stage.getErrorMessageByApplicationModule(application);

        assertThat(errorMessage).contains("01-12000").contains("pending1").contains("pending2")
            .contains("depends on").contains("can not be resolved").contains("Missing modules")
            .contains("missing1").contains("missing2")
            .contains("Please add the corresponding modules");
    }

    @Test
    void testWriteMessageToStringBuilder() {
        StringBuilder sb = new StringBuilder();
        List<DeploymentDescriptor> deploys = List.of(new MockDeploymentDescriptor("dd1"),
            new MockDeploymentDescriptor("dd2"));

        ModelCreatingStage stage = new ModelCreatingStage();
        stage.writeMessageToStringBuilder(sb, deploys, "Test");

        assertThat(sb.toString()).contains("Test").contains("dd1").contains("dd2");
    }

}
