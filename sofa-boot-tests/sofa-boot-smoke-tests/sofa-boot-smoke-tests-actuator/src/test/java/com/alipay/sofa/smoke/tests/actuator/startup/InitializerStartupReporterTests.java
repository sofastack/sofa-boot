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

import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.BootStageConstants;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSOFABootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ApplicationContextInitializer} startup cost reporter.
 *
 * @author huzijie
 * @version InitializerStartupReporterTests.java, v 0.1 2022年03月14日 2:37 PM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSOFABootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"management.endpoints.web.exposure.include=startup"})
@ContextConfiguration(loader = StartupSpringBootContextLoader.class)
public class InitializerStartupReporterTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        BaseStat baseStat = startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        assertThat(baseStat).isInstanceOf(ChildrenStat.class);
        ChildrenStat<BaseStat> applicationContextPrepareStage = (ChildrenStat<BaseStat>) baseStat;
        assertThat(applicationContextPrepareStage).isNotNull();
        List<BaseStat> baseStatList = applicationContextPrepareStage.getChildren();
        assertThat(baseStatList.isEmpty()).isFalse();
        assertThat(baseStatList.stream().anyMatch(stat -> stat.getName().equals("com.alipay.sofa.runtime.SofaRuntimeSpringContextInitializer"))).isTrue();
        assertThat(baseStatList.stream().filter(stat -> stat.getName().equals("com.alipay.sofa.smoke.tests.actuator.startup.spring.StartupInitializer")).findFirst().get()
                .getCost() >= 10).isTrue();
    }
}
