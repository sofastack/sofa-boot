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
package com.alipay.sofa.smoke.tests.actuator.startup;

import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BootStageConstants;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.boot.startup.StartupSmartLifecycle;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.alipay.sofa.smoke.tests.actuator.sample.beans.InitCostBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for startup reporter.
 *
 * @author huzijie
 * @version DefaultStageStartupReporterTests.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.endpoints.web.exposure.include=startup",
                                  "spring.autoconfigure.exclude=com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration" })
@Import(InitCostBean.class)
public class DefaultStageStartupReporterTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void startupReporter() {
        assertThat(startupReporter).isNotNull();
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.getStartupStaticsModel();
        assertThat(startupStaticsModel).isNotNull();
        assertThat(startupStaticsModel.getStageStats().size()).isEqualTo(5);

        assertThat(startupStaticsModel.getApplicationBootTime() > 0).isTrue();
        assertThat(startupStaticsModel.getApplicationBootElapsedTime() > 0).isTrue();
        assertThat(startupStaticsModel.getAppName()).isEqualTo("smoke-tests-actuator");

        BaseStat jvmStartingStage = startupReporter.getStageNyName(BootStageConstants.JVM_STARTING_STAGE);
        assertThat(jvmStartingStage).isNotNull();
        assertThat(jvmStartingStage.getCost() > 0).isTrue();

        BaseStat environmentPrepareStage = startupReporter.getStageNyName(BootStageConstants.ENVIRONMENT_PREPARE_STAGE);
        assertThat(environmentPrepareStage).isNotNull();
        assertThat(environmentPrepareStage.getCost() > 0).isTrue();
        assertThat(environmentPrepareStage.getStartTime()).isEqualTo(jvmStartingStage.getEndTime());

        ChildrenStat<?> applicationContextPrepareStage = (ChildrenStat<?>) startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        assertThat(applicationContextPrepareStage).isNotNull();
        assertThat(applicationContextPrepareStage.getChildren().isEmpty()).isTrue();
        assertThat(applicationContextPrepareStage.getCost() > 0).isTrue();
        assertThat(environmentPrepareStage.getEndTime()).isEqualTo(applicationContextPrepareStage.getStartTime());

        BaseStat applicationContextLoadStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_LOAD_STAGE);
        assertThat(applicationContextLoadStage).isNotNull();
        assertThat(applicationContextLoadStage.getCost() > 0).isTrue();
        assertThat(applicationContextLoadStage.getStartTime()).isEqualTo(applicationContextPrepareStage.getEndTime());

        BaseStat applicationContextRefreshStage = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE);
        assertThat(applicationContextRefreshStage).isNotNull();
        assertThat(applicationContextRefreshStage.getCost() > 0).isTrue();
        assertThat(applicationContextRefreshStage.getStartTime()).isEqualTo(applicationContextLoadStage.getEndTime());

        assertThat(applicationContextRefreshStage).isInstanceOf(ChildrenStat.class);
        assertThat(((ChildrenStat<?>) applicationContextRefreshStage).getChildren().size()).isEqualTo(1);
        ModuleStat moduleStat = (ModuleStat) ((ChildrenStat<?>) applicationContextRefreshStage).getChildren().get(0);
        assertThat(moduleStat).isNotNull();
        assertThat(moduleStat.getName()).isEqualTo(StartupSmartLifecycle.ROOT_MODULE_NAME);
        assertThat(moduleStat.getEndTime() > moduleStat.getStartTime()).isTrue();
        assertThat(moduleStat.getEndTime() - moduleStat.getStartTime()).isEqualTo(moduleStat.getCost());

        List<BeanStat> beanStats =  moduleStat.getChildren();
        assertThat(beanStats).isNotNull();
        assertThat(beanStats.size() >= 1).isTrue();
        BeanStat initBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("InitCostBean")).findFirst().orElse(null);
        assertThat(initBeanStat).isNotNull();
        assertThat(initBeanStat.getCost()).isEqualTo(initBeanStat.getRefreshElapsedTime());
        assertThat(initBeanStat.getRealRefreshElapsedTime() - InitCostBean.INIT_COST_TIME < 20).isTrue();
        assertThat(initBeanStat.getBeanClassName()).isEqualTo(InitCostBean.class.getName());
        assertThat(initBeanStat.getChildren().isEmpty()).isTrue();
    }
}
