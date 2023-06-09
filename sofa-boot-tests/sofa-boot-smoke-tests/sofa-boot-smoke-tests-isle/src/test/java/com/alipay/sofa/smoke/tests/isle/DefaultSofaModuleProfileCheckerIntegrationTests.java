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
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.smoke.tests.isle.util.AddCustomJar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link DefaultSofaModuleProfileChecker}.
 *
 * @author huzijie
 * @version DefaultSofaModuleProfileCheckerIntegrationTests.java, v 0.1 2023年02月21日 8:25 PM huzijie Exp $
 */
@SpringBootTest(classes = IsleSofaBootApplication.class)
@AddCustomJar({ "dev-module", "test-module", "sample-module", "nospring-module",
               "nospringchild-module" })
@TestPropertySource(properties = "sofa.boot.isle.activeProfiles=dev")
public class DefaultSofaModuleProfileCheckerIntegrationTests {

    @Autowired
    private ApplicationRuntimeModel applicationRuntimeModel;

    @Test
    public void verifyRuntimeModel() {
        // contains 2 Deployments
        assertThat(applicationRuntimeModel.getAllDeployments().size()).isEqualTo(3);
        assertThat(applicationRuntimeModel.getInstalled().size()).isEqualTo(3);
        assertThat(applicationRuntimeModel.getFailed().size()).isEqualTo(0);

        // check module init success
        assertThat(applicationRuntimeModel.getInstalled().stream()).noneMatch(deploymentDescriptor ->
                deploymentDescriptor.getModuleName().equals("com.alipay.sofa.test"));
        assertThat(applicationRuntimeModel.getInstalled().stream()).noneMatch(deploymentDescriptor ->
                deploymentDescriptor.getModuleName().equals("com.alipay.sofa.no-spring"));
    }
}
