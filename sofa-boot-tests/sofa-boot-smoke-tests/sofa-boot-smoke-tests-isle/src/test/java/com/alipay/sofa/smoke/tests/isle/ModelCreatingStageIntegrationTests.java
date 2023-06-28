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
package com.alipay.sofa.smoke.tests.isle;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import com.alipay.sofa.smoke.tests.isle.util.AddCustomJar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ModelCreatingStage}.
 *
 * @author huzijie
 * @version ModelCreatingStageIntegrationTests.java, v 0.1 2023年02月21日 8:37 PM huzijie Exp $
 */
@SpringBootTest(classes = IsleSofaBootApplication.class)
@AddCustomJar({ "sample-module", "sample-module", "miss-module", "fail-module", "duplicate-module" })
@TestPropertySource(properties = {
                                  "sofa.boot.isle.ignoreCalculateRequireModules=com.alipay.sofa.miss",
                                  "sofa.boot.isle.ignoreModules=com.alipay.sofa.fail",
                                  "sofa.boot.isle.allowModuleOverriding=true" })
public class ModelCreatingStageIntegrationTests {

    @Autowired
    private ApplicationRuntimeModel applicationRuntimeModel;

    @Test
    public void verifyRuntimeModel() {
        // contains 2 Deployments
        assertThat(applicationRuntimeModel.getAllDeployments().size()).isEqualTo(3);
        assertThat(applicationRuntimeModel.getInstalled().size()).isEqualTo(2);
        assertThat(applicationRuntimeModel.getFailed().size()).isEqualTo(0);

        // check spring xml
        List<DeploymentDescriptor> deploymentDescriptors = applicationRuntimeModel.getAllDeployments();
        DeploymentDescriptor sample = deploymentDescriptors.stream().filter(deploymentDescriptor ->
                deploymentDescriptor.getModuleName().equals("com.alipay.sofa.sample")).findFirst().get();
        assertThat(sample.getSpringResources().size()).isEqualTo(1);
        assertThat(sample.getInstalledSpringXml()).allMatch(s -> s.contains("duplicate-module") && s.contains("service.xml"));

        DeploymentDescriptor miss = deploymentDescriptors.stream().filter(deploymentDescriptor ->
                deploymentDescriptor.getModuleName().equals("com.alipay.sofa.miss")).findFirst().get();
        assertThat(miss.getSpringResources().size()).isEqualTo(1);
        assertThat(miss.getInstalledSpringXml()).allMatch(s -> s.contains("miss-module") && s.contains("service.xml"));
    }
}
