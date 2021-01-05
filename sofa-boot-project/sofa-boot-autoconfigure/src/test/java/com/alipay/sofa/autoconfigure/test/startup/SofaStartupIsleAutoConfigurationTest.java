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
package com.alipay.sofa.autoconfigure.test.startup;

import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.startup.SofaStartupAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.startup.SofaStartupIsleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.isle.StartupModelCreatingStage;
import com.alipay.sofa.startup.stage.isle.StartupSpringContextInstallStage;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version SofaStartupIsleAutoConfigurationTest.java, v 0.1 2021年01月05日 11:50 上午 huzijie Exp $
 */
public class SofaStartupIsleAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaStartupAutoConfiguration.class,
                                                                     SofaStartupIsleAutoConfiguration.class,
                                                                     SofaModuleAutoConfiguration.class,
                                                                     SofaRuntimeAutoConfiguration.class));

    @Test
    public void startupReporterAndApplicationRuntimeModelExist() {
        contextRunner.run((context -> {
            assertThat(context).hasSingleBean(StartupSpringContextInstallStage.class);
            assertThat(context).hasSingleBean(StartupModelCreatingStage.class);
        }));
    }

    @Test
    public void applicationRuntimeModelNotExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(ApplicationRuntimeModel.class))
                .run((context -> {
            assertThat(context).doesNotHaveBean(SpringContextInstallStage.class);
            assertThat(context).doesNotHaveBean(ModelCreatingStage.class);
        }));
    }

    @Test
    public void startupReporterNotExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(StartupReporter.class))
                .run((context -> {
            assertThat(context).hasSingleBean(SpringContextInstallStage.class);
            assertThat(context).hasSingleBean(ModelCreatingStage.class);
            assertThat(context).doesNotHaveBean(StartupSpringContextInstallStage.class);
            assertThat(context).doesNotHaveBean(StartupModelCreatingStage.class);
        }));
    }
}