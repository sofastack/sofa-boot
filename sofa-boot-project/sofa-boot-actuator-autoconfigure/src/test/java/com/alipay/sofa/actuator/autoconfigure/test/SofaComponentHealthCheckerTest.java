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
package com.alipay.sofa.actuator.autoconfigure.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.impl.ComponentHealthChecker;

/**
 * @author abby.zh
 * @since 2.4.10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmptyConfiguration.class)
public class SofaComponentHealthCheckerTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testDefaultConfig() {
        ComponentHealthChecker sofaComponentHealthChecker = ctx
            .getBean(ComponentHealthChecker.class);
        Assert.assertEquals(SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT,
            sofaComponentHealthChecker.getRetryCount());
        Assert.assertEquals(SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL,
            sofaComponentHealthChecker.getRetryTimeInterval());
    }

    @Test
    public void testHealthChecker() {
        Assert.assertNotNull(ctx.getBean("sofaComponentHealthChecker"));

        HealthChecker healthChecker = (HealthChecker) ctx.getBean("sofaComponentHealthChecker");
        Assert.assertTrue(healthChecker.isHealthy().getStatus().equals(Status.UP));
        Assert.assertEquals("SOFABoot-Components", healthChecker.getComponentName());
        Assert.assertEquals(SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT,
            healthChecker.getRetryCount());
        Assert.assertEquals(SofaBootConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL,
            healthChecker.getRetryTimeInterval());
        Assert.assertEquals(true, healthChecker.isStrictCheck());
    }
}