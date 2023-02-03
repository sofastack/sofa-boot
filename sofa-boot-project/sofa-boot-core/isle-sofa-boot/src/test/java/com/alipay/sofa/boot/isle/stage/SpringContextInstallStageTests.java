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
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.boot.isle.profile.DefaultSofaModuleProfileChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link SpringContextInstallStage}.
 *
 * @author huzijie
 * @version SpringContextInstallStageTests.java, v 0.1 2023年02月02日 6:18 PM huzijie Exp $
 */
public class SpringContextInstallStageTests {

    private final SpringContextInstallStage stage = new SpringContextInstallStage();

    private ApplicationRuntimeModel         application;

    @BeforeEach
    public void init() {
        application = new ApplicationRuntimeModel();
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        DefaultSofaModuleProfileChecker checker = new DefaultSofaModuleProfileChecker();
        checker.init();
        application.setSofaModuleProfileChecker(checker);
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.refresh();
        stage.setApplicationRuntimeModel(application);
        stage.setSpringContextLoader(new DynamicSpringContextLoader(applicationContext));
    }

    @Test
    public void installFailure() {
        application.getFailed().add(SampleDeploymentDescriptor.create(new Properties()));

        assertThatThrownBy(stage::doProcess)
                .isInstanceOf(DeploymentException.class)
                .hasMessageContaining("11007");

        stage.setIgnoreModuleInstallFailure(true);
        assertThat(catchThrowable(stage::doProcess)).isNull();
        stage.setIgnoreModuleInstallFailure(false);
    }
}
