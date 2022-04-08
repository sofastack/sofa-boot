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

import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.BootStageConstants;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.test.configuration.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.test.spring.StartupApplication;
import com.alipay.sofa.startup.test.spring.StartupSpringBootContextLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author huzijie
 * @version InitializerStartupReporterTest.java, v 0.1 2022年03月14日 2:37 PM huzijie Exp $
 */
@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Import(SofaStartupAutoConfiguration.class)
@ContextConfiguration(loader = StartupSpringBootContextLoader.class)
public class InitializerStartupReporterTest {

    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        ChildrenStat<BaseStat> applicationContextPrepareStage = (ChildrenStat<BaseStat>) startupReporter.getStageNyName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        Assert.assertNotNull(applicationContextPrepareStage);
        List<BaseStat> baseStatList = applicationContextPrepareStage.getChildren();
        Assert.assertFalse(baseStatList.isEmpty());
        Assert.assertTrue(baseStatList.stream().anyMatch(stat -> stat.getName().equals("com.alipay.sofa.runtime.SofaRuntimeSpringContextInitializer")));
        Assert.assertTrue(baseStatList.stream().filter(stat -> stat.getName().equals("com.alipay.sofa.startup.test.beans.StartupInitializer")).findFirst().get()
                .getCost() >= 10);
    }
}
