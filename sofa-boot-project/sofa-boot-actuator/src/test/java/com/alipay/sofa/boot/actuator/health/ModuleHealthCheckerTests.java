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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.ArrayList;
import java.util.List;

import static com.alipay.sofa.boot.actuator.health.ModuleHealthChecker.COMPONENT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ModuleHealthChecker}.
 *
 * @author huzijie
 * @version ModuleHealthCheckerTests.java, v 0.1 2023年01月05日 6:10 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ModuleHealthCheckerTests {

    @InjectMocks
    private ModuleHealthChecker     moduleHealthChecker;

    @Mock
    private ApplicationRuntimeModel applicationRuntimeModel;

    @Test
    public void moduleHealthCheckSuccess() {
        Mockito.doReturn(new ArrayList<>()).when(applicationRuntimeModel).getFailed();
        Health health = moduleHealthChecker.isHealthy();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void moduleHealthCheckFail() {
        List<DeploymentDescriptor> failed = new ArrayList<>();
        DeploymentDescriptor deploymentDescriptor = Mockito.mock(DeploymentDescriptor.class);
        Mockito.doReturn("failedModule").when(deploymentDescriptor).getName();
        failed.add(deploymentDescriptor);
        Mockito.doReturn(failed).when(applicationRuntimeModel).getFailed();
        Health health = moduleHealthChecker.isHealthy();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{failedModule=failed}");
    }

    @Test
    public void moduleHealthConfigs() {
        assertThat(moduleHealthChecker.getComponentName()).isEqualTo(COMPONENT_NAME);
        assertThat(moduleHealthChecker.getRetryCount()).isEqualTo(0);
        assertThat(moduleHealthChecker.getRetryTimeInterval()).isEqualTo(1000);
        assertThat(moduleHealthChecker.getTimeout()).isEqualTo(10000);
        assertThat(moduleHealthChecker.isStrictCheck()).isTrue();
    }
}
