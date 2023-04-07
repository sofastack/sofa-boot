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
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.util.LogOutPutUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ModuleLogOutputStage}.
 *
 * @author huzijie
 * @version ModuleLogOutputStageTests.java, v 0.1 2023年04月07日 12:11 PM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class ModuleLogOutputStageTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(ModuleLogOutputStage.class);
    }

    @Mock
    private ApplicationRuntimeModel application;

    @Test
    void logInstalledModules(CapturedOutput capturedOutput) {
        // mock the dependencies
        DeploymentDescriptor dd1 = mock(DeploymentDescriptor.class);
        DeploymentDescriptor dd2 = mock(DeploymentDescriptor.class);
        when(dd1.getName()).thenReturn("dd1");
        when(dd2.getName()).thenReturn("dd2");
        when(dd1.getElapsedTime()).thenReturn(100L);
        when(dd2.getElapsedTime()).thenReturn(200L);
        when(dd1.getInstalledSpringXml()).thenReturn(List.of("xml1", "xml2"));
        when(dd2.getInstalledSpringXml()).thenReturn(List.of("xml3"));

        when(application.getInstalled()).thenReturn(List.of(dd1, dd2));

        // test the log output
        ModuleLogOutputStage stage = new ModuleLogOutputStage();
        stage.setApplicationRuntimeModel(application);

        stage.logInstalledModules();

        assertThat(capturedOutput.getOut())
            .contains("Spring context initialize success module list").contains("dd1 [100 ms]")
            .contains("dd2 [200 ms]").contains("xml1").contains("xml2").contains("xml3")
            .contains("totalTime = 300 ms");
    }

    @Test
    void logFailedModules(CapturedOutput capturedOutput) {
        DeploymentDescriptor dd1 = mock(DeploymentDescriptor.class);
        DeploymentDescriptor dd2 = mock(DeploymentDescriptor.class);
        when(dd1.getName()).thenReturn("dd1");
        when(dd2.getName()).thenReturn("dd2");

        when(application.getFailed()).thenReturn(List.of(dd1, dd2));

        // test the log output
        ModuleLogOutputStage stage = new ModuleLogOutputStage();
        stage.setApplicationRuntimeModel(application);

        stage.logFailedModules();

        assertThat(capturedOutput.getOut())
            .contains("Spring context initialize failed module list").contains("dd1")
            .contains("dd2");
    }
}
