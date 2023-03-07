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

import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for health startup reporter.
 *
 * @author huzijie
 * @version HealthStageStartupReporterTests.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.endpoints.web.exposure.include=startup,readiness",
                                  "spring.autoconfigure.exclude=com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration" })
public class HealthStageStartupReporterTests {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void startupReporter() {
        assertThat(startupReporter).isNotNull();
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter
            .getStartupStaticsModel();
        assertThat(startupStaticsModel).isNotNull();
        assertThat(startupStaticsModel.getStageStats().size()).isEqualTo(6);

        ChildrenStat<BaseStat> healthCheckStage = (ChildrenStat<BaseStat>) startupReporter
            .getStageNyName(ReadinessCheckListener.READINESS_CHECK_STAGE);
        assertThat(healthCheckStage).isNotNull();
        assertThat(healthCheckStage.getCost() > 0).isTrue();

        List<BaseStat> children = healthCheckStage.getChildren();
        assertThat(children).isNotNull();
        assertThat(children.size() > 0).isTrue();
    }
}
