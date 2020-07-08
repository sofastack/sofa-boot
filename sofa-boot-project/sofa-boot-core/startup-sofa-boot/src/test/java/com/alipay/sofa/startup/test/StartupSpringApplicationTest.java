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

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.spring.StartupSpringApplication;
import com.alipay.sofa.startup.test.spring.SampleSpringContextInitializer;
import com.alipay.sofa.startup.test.spring.SofaStartupAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Zhijie
 * @Date: 2020/7/13
 */
public class StartupSpringApplicationTest {

    @Test
    public void testRun() {
        StartupSpringApplication.run(TestSpringApplication.class, new String[] {},
            WebApplicationType.NONE);
        Assert.assertTrue(SofaStartupContext.getAppStartupTime() > 0);
        Assert.assertTrue(SofaStartupContext.getJvmStartupTime() > 0);
        Assert.assertTrue(SofaStartupContext.getAppStartupTime() > SofaStartupContext
            .getJvmStartupTime());
    }

    @Test
    public void testApplyInitializers() {
        ApplicationContext ctx = StartupSpringApplication.run(TestSpringApplication.class,
            new String[] {}, WebApplicationType.NONE);
        SofaStartupContext sofaStartupContext = ctx.getBean(SofaStartupContext.class);
        Assert.assertTrue(sofaStartupContext.getInitializerCost() > 0);
        Assert.assertTrue(sofaStartupContext.getInitializerDetail().size() > 0);
        Assert
            .assertTrue(sofaStartupContext.getInitializerDetail().get(
                "com.alipay.sofa.startup.test.spring.SampleSpringContextInitializer") >= SampleSpringContextInitializer.INITIALIZE_COST_TIME);
    }

    @Configuration
    @ImportAutoConfiguration(SofaStartupAutoConfiguration.class)
    static class TestSpringApplication {
    }
}
