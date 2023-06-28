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
package com.alipay.sofa.boot.isle.deployment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultModuleDeploymentValidator}.
 *
 * @author huzijie
 * @version DefaultModuleDeploymentValidatorTests.java, v 0.1 2023年04月06日 8:47 PM huzijie Exp $
 */
public class DefaultModuleDeploymentValidatorTests {

    @Test
    public void isModuleDeploymentReturnsTrueForValidDeploymentDescriptor() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn("sample-module");
        when(deploymentDescriptor.isSpringPowered()).thenReturn(true);

        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        boolean result = validator.isModuleDeployment(deploymentDescriptor);

        assertThat(result).isTrue();
    }

    @Test
    public void isModuleDeploymentReturnsFalseForMissingModuleName() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn("");
        when(deploymentDescriptor.isSpringPowered()).thenReturn(true);

        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        boolean result = validator.isModuleDeployment(deploymentDescriptor);

        assertThat(result).isFalse();
    }

}
