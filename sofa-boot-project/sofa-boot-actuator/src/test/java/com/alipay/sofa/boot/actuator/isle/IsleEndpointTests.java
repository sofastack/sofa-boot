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
package com.alipay.sofa.boot.actuator.isle;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IsleEndpoint}.
 *
 * @author huzijie
 * @version IsleEndpointTests.java, v 0.1 2023年10月10日 3:56 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class IsleEndpointTests {
    @Mock
    private DeploymentDescriptor installedDeploymentDescriptor;

    @Mock
    private DeploymentDescriptor failedDeploymentDescriptor;

    @Mock
    private DeploymentDescriptor inActivedDeploymentDescriptor;

    private IsleEndpoint         isleEndpoint;

    @BeforeEach
    public void setUp() {
        ApplicationRuntimeModel runtimeModel = new ApplicationRuntimeModel();
        isleEndpoint = new IsleEndpoint(runtimeModel);

        runtimeModel.addInstalled(installedDeploymentDescriptor);
        runtimeModel.addFailed(failedDeploymentDescriptor);
        runtimeModel.addInactiveDeployment(inActivedDeploymentDescriptor);

        Mockito.doReturn("installModule").when(installedDeploymentDescriptor).getModuleName();
        Mockito.doReturn("parent").when(installedDeploymentDescriptor).getSpringParent();
        Mockito.doReturn(List.of("requireA", "requireB")).when(installedDeploymentDescriptor)
            .getRequiredModules();
        Mockito.doReturn("resourceName").when(installedDeploymentDescriptor).getName();

        ClassPathXmlApplicationContext contextA = new ClassPathXmlApplicationContext();
        contextA.refresh();
        Mockito.doReturn(contextA).when(installedDeploymentDescriptor).getApplicationContext();
        Mockito.doReturn(List.of("xmlA", "xmlB")).when(installedDeploymentDescriptor)
            .getInstalledSpringXml();
        Mockito.doReturn(1000L).when(installedDeploymentDescriptor).getElapsedTime();
        Mockito.doReturn(2000L).when(installedDeploymentDescriptor).getStartTime();

        Mockito.doReturn(null).when(failedDeploymentDescriptor).getApplicationContext();

        Mockito.doReturn(null).when(inActivedDeploymentDescriptor).getApplicationContext();
    }

    @Test
    public void isle() {
        IsleEndpoint.IsleDescriptor isleDescriptor = isleEndpoint.modules();
        assertThat(isleDescriptor).isNotNull();

        assertThat(isleDescriptor.getInstalledModuleList().size()).isEqualTo(1);
        IsleEndpoint.ModuleDisplayInfo installeModuleDisplayInfo = isleDescriptor
            .getInstalledModuleList().get(0);
        assertThat(installeModuleDisplayInfo.getName()).isEqualTo("installModule");
        assertThat(installeModuleDisplayInfo.getSpringParent()).isEqualTo("parent");
        assertThat(installeModuleDisplayInfo.getRequireModules()).containsExactly("requireA",
            "requireB");
        assertThat(installeModuleDisplayInfo.getResourceName()).isEqualTo("resourceName");
        assertThat(installeModuleDisplayInfo.getInstallSpringXmls())
            .containsExactly("xmlA", "xmlB");
        assertThat(installeModuleDisplayInfo.getElapsedTime()).isEqualTo(1000L);
        assertThat(installeModuleDisplayInfo.getStartupTime()).isEqualTo(2000L);

        assertThat(isleDescriptor.getFailedModuleList().size()).isEqualTo(1);
        IsleEndpoint.ModuleDisplayInfo failedModuleDisplayInfo = isleDescriptor
            .getFailedModuleList().get(0);
        assertThat(failedModuleDisplayInfo.getElapsedTime()).isEqualTo(0);
        Mockito.verify(failedDeploymentDescriptor, Mockito.never()).getElapsedTime();

        assertThat(isleDescriptor.getInactiveModuleList().size()).isEqualTo(1);
        IsleEndpoint.ModuleDisplayInfo inactiveModuleDisplayInfo = isleDescriptor
            .getInactiveModuleList().get(0);
        assertThat(inactiveModuleDisplayInfo.getElapsedTime()).isEqualTo(0);
        Mockito.verify(inActivedDeploymentDescriptor, Mockito.never()).getElapsedTime();
    }
}
