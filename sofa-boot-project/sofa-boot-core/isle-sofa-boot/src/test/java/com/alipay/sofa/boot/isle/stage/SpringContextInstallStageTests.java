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
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.boot.util.LogOutPutUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SpringContextInstallStage}.
 *
 * @author huzijie
 * @version SpringContextInstallStageTests.java, v 0.1 2023年02月02日 6:18 PM huzijie Exp $
 */
@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
public class SpringContextInstallStageTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(SpringContextInstallStage.class);
    }

    private final SpringContextInstallStage stage = new SpringContextInstallStage();

    @Mock
    private ApplicationRuntimeModel         application;

    @BeforeEach
    public void init() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.refresh();
        stage.setApplicationRuntimeModel(application);
        stage.setSpringContextLoader(new DynamicSpringContextLoader(applicationContext));
    }

    @Test
    public void installSpringContextException() {
        when(application.getResolvedDeployments()).thenThrow(new RuntimeException());

        assertThatThrownBy(stage::doProcess)
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("01-11000");
    }

    @Test
    public void moduleInstallFailure() {
        when(application.getFailed()).thenReturn(List.of(SampleDeploymentDescriptor.create(new Properties())));

        assertThatThrownBy(stage::doProcess)
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("11007");

        stage.setIgnoreModuleInstallFailure(true);
        assertThat(catchThrowable(stage::doProcess)).isNull();
        stage.setIgnoreModuleInstallFailure(false);
    }

    @Test
    public void ctxInstallError(CapturedOutput capturedOutput) throws Exception {
        SampleDeploymentDescriptor sampleDeploymentDescriptor = SampleDeploymentDescriptor
            .create(new Properties());
        when(application.getResolvedDeployments()).thenReturn(List.of(sampleDeploymentDescriptor));
        sampleDeploymentDescriptor.setApplicationContext(new GenericApplicationContext());

        stage.doProcess();

        verify(application, times(1)).addFailed(any());
        assertThat(capturedOutput.getOut()).contains("01-11001");
    }

    @Test
    public void ctxIsNull(CapturedOutput capturedOutput) throws Exception {
        when(application.getResolvedDeployments()).thenReturn(
            List.of(SampleDeploymentDescriptor.create(new Properties())));

        stage.doProcess();

        assertThat(capturedOutput.getOut()).contains("01-11003");
    }

    @Test
    public void ctxRefreshError(CapturedOutput capturedOutput) throws Exception {
        SampleDeploymentDescriptor sampleDeploymentDescriptor = SampleDeploymentDescriptor
            .create(new Properties());
        when(application.getResolvedDeployments()).thenReturn(List.of(sampleDeploymentDescriptor));
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        genericApplicationContext.registerBeanDefinition("exceptionBean", new RootBeanDefinition(
            ExceptionBean.class));
        sampleDeploymentDescriptor.setApplicationContext(genericApplicationContext);

        stage.doProcess();

        verify(application, times(2)).addFailed(any());
        assertThat(capturedOutput.getOut()).contains("01-11002");
    }

    static class ExceptionBean {

        public ExceptionBean() {
            throw new RuntimeException("create bean exception");
        }
    }
}
