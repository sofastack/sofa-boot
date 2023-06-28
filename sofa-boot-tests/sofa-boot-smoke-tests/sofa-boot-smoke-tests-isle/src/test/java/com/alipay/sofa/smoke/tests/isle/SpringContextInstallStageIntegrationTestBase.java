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
import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.smoke.tests.isle.util.AddCustomJar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base integration tests for {@link SpringContextInstallStage}.
 *
 * @author huzijie
 * @version SpringContextInstallStageIntegrationTestBase.java, v 0.1 2023年02月02日 7:16 PM huzijie Exp $
 */
@SpringBootTest(classes = IsleSofaBootApplication.class)
@AddCustomJar({ "sample-module", "fail-module", "child-module" })
@TestPropertySource(properties = "sofa.boot.isle.ignoreModuleInstallFailure=true")
public abstract class SpringContextInstallStageIntegrationTestBase {

    @Autowired
    private ApplicationRuntimeModel applicationRuntimeModel;

    @Test
    public void verifyRuntimeModel() {
        // contains 2 Deployments
        assertThat(applicationRuntimeModel.getAllDeployments().size()).isEqualTo(3);
        assertThat(applicationRuntimeModel.getInstalled().size()).isEqualTo(2);
        assertThat(applicationRuntimeModel.getFailed().size()).isEqualTo(1);

        // check module init success
        assertThat(applicationRuntimeModel.getInstalled().stream()).anyMatch(deploymentDescriptor ->
                deploymentDescriptor.getApplicationContext().containsBean("sampleBean"));

        // check module not in installed list
        DeploymentDescriptor failModule = applicationRuntimeModel.getFailed().get(0);
        assertThat(failModule.getModuleName()).isEqualTo("com.alipay.sofa.fail");
    }

    @Test
    public void verifyRefreshParent() {
        Map<String, ApplicationContext> contextMap = applicationRuntimeModel
            .getModuleApplicationContextMap();
        GenericApplicationContext child = (GenericApplicationContext) contextMap
            .get("com.alipay.sofa.child");
        GenericApplicationContext sample = (GenericApplicationContext) contextMap
            .get("com.alipay.sofa.sample");

        assertThat(child).isNotNull();
        assertThat(sample).isNotNull();
        assertThat(child.getParent()).isEqualTo(sample);
        assertThat(child.getStartupDate() > sample.getStartupDate()).isTrue();
    }
}
