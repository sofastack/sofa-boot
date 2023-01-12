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

import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BootStageConstants;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;
import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.alipay.sofa.smoke.tests.actuator.startup.beans.ChildBean;
import com.alipay.sofa.smoke.tests.actuator.startup.beans.InitCostBean;
import com.alipay.sofa.smoke.tests.actuator.startup.beans.ParentBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for isle startup reporter.
 * 
 * @author huzijie
 * @version IsleStageStartupReporterTests.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
                                                                                                                                       "management.endpoints.web.exposure.include=startup",
                                                                                                                                       "com.alipay.sofa.boot.beanLoadCost=1" })
@Import(InitCostBean.class)
public class IsleStageStartupReporterTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        assertThat(startupReporter).isNotNull();
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
        assertThat(startupStaticsModel).isNotNull();
        assertThat(startupStaticsModel.getStageStats().size()).isEqualTo(7);

        BaseStat isleModelCreatingStage = startupReporter.getStageNyName(BootStageConstants.ISLE_MODEL_CREATING_STAGE);
        assertThat(isleModelCreatingStage).isNotNull();
        assertThat(isleModelCreatingStage.getCost() > 0).isTrue();

        BaseStat isleSpringContextInstallStage = startupReporter.getStageNyName(BootStageConstants.ISLE_SPRING_CONTEXT_INSTALL_STAGE);
        assertThat(isleSpringContextInstallStage).isNotNull();
        assertThat(isleSpringContextInstallStage.getCost() > 0).isTrue();

        assertThat(isleSpringContextInstallStage instanceof ChildrenStat).isTrue();
        assertThat(((ChildrenStat<?>) isleSpringContextInstallStage).getChildren().size()).isEqualTo(1);
        ModuleStat moduleStat = (ModuleStat) ((ChildrenStat<?>) isleSpringContextInstallStage).getChildren().get(0);
        assertThat(moduleStat).isNotNull();
        assertThat(moduleStat.getName()).isEqualTo("testModule");
        assertThat(moduleStat.getEndTime() > moduleStat.getStartTime()).isTrue();
        assertThat(moduleStat.getEndTime() - moduleStat.getStartTime()).isEqualTo(moduleStat.getCost());

        List<BeanStat> beanStats = moduleStat.getChildren();
        assertThat(beanStats).isNotNull();
        assertThat(beanStats.size() >= 4).isTrue();

        //test parent bean
        BeanStat parentBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("(parent)")).findFirst().orElse(null);
        assertThat(parentBeanStat).isNotNull();
        assertThat(ChildBean.CHILD_INIT_TIME + ParentBean.PARENT_INIT_TIME - parentBeanStat.getRefreshElapsedTime() < 20).isTrue();
        assertThat(ParentBean.PARENT_INIT_TIME - parentBeanStat.getRealRefreshElapsedTime() < 20).isTrue();
        assertThat(ParentBean.PARENT_INIT_TIME - parentBeanStat.getAfterPropertiesSetTime() < 20).isTrue();
        assertThat(parentBeanStat.getChildren().size()).isEqualTo(1);
        assertThat(parentBeanStat.getBeanClassName()).isEqualTo(ParentBean.class.getName() + " (parent)");

        // test child bean
        BeanStat childBeanStat = parentBeanStat.getChildren().get(0);
        assertThat(childBeanStat).isNotNull();
        assertThat(ChildBean.CHILD_INIT_TIME - childBeanStat.getRealRefreshElapsedTime() < 15).isTrue();
        assertThat(ChildBean.CHILD_INIT_TIME - childBeanStat.getInitMethodTime() < 10).isTrue();
        assertThat(childBeanStat.getChildren().size()).isEqualTo(0);
        assertThat(childBeanStat.getBeanClassName()).isEqualTo(ChildBean.class.getName() + " (child)");

        //test sofa service
        BeanStat serviceBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ServiceFactoryBean")).findFirst().orElse(null);
        assertThat(serviceBeanStat).isNotNull();
        assertThat(serviceBeanStat.getRefreshElapsedTime() > 0).isTrue();
        assertThat(serviceBeanStat.getBeanClassName()).isEqualTo(ServiceFactoryBean.class.getName() + " (sample)");
        assertThat(serviceBeanStat.getInterfaceType()).isEqualTo("com.alipay.sofa.smoke.tests.actuator.startup.beans.SampleService");

        // test sofa reference
        BeanStat referenceBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ReferenceFactoryBean")).findFirst().orElse(null);
        assertThat(referenceBeanStat).isNotNull();
        assertThat(referenceBeanStat.getRefreshElapsedTime() > 0).isTrue();
        assertThat(referenceBeanStat.getBeanClassName()).isEqualTo(ReferenceFactoryBean.class.getName() + " (reference)");
        assertThat(referenceBeanStat.getInterfaceType()).isEqualTo("com.alipay.sofa.smoke.tests.actuator.startup.beans.TestService");

        // test extension bean
        BeanStat extensionBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ExtensionFactoryBean")).findFirst().orElse(null);
        assertThat(extensionBeanStat).isNotNull();
        assertThat(extensionBeanStat.getRefreshElapsedTime() > 0).isTrue();
        assertThat(extensionBeanStat.getBeanClassName()).isEqualTo(ExtensionFactoryBean.class.getName());
        assertThat(extensionBeanStat.getExtensionProperty()).isEqualTo("ExtensionPointTarget: extension");

        // test extension point bean
        BeanStat extensionPointBeanStat = beanStats.stream().filter(beanStat -> beanStat.getBeanClassName().contains("ExtensionPointFactoryBean")).findFirst().orElse(null);
        assertThat(extensionPointBeanStat).isNotNull();
        assertThat(extensionPointBeanStat.getRefreshElapsedTime() > 0).isTrue();
        assertThat(extensionPointBeanStat.getBeanClassName()).isEqualTo(ExtensionPointFactoryBean.class.getName());
        assertThat(extensionPointBeanStat.getExtensionProperty()).isEqualTo("ExtensionPointTarget: extension");
    }
}
