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
package com.alipay.sofa.startup.test;

import com.alipay.sofa.boot.startup.BootStageConstants;
import com.alipay.sofa.boot.startup.StageStat;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.test.configuration.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.test.configuration.SofaStartupHealthCheckAutoConfiguration;
import com.alipay.sofa.startup.test.spring.StartupApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huzijie
 * @version StartupReporterTest.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Import(value = { SofaStartupAutoConfiguration.class, SofaStartupHealthCheckAutoConfiguration.class })
@EnableConfigurationProperties({ HealthCheckProperties.class,
                                SofaRuntimeConfigurationProperties.class })
public class HealthCheckStartupReporterTest {
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        Assert.assertNotNull(startupReporter);
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
        Assert.assertNotNull(startupStaticsModel);
        Assert.assertEquals(6, startupStaticsModel.getStageStats().size());

        StageStat healthCheckStage = startupReporter
            .getStageNyName(BootStageConstants.HEALTH_CHECK_STAGE);
        Assert.assertNotNull(healthCheckStage);
        Assert.assertTrue(healthCheckStage.getElapsedTime() > 0);

    }
}
