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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.healthcheck.impl.ComponentHealthChecker;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmptyConfiguration.class)
@TestPropertySource(properties = { "com.alipay.sofa.healthcheck.component.check.retry.count=10",
                                  "com.alipay.sofa.healthcheck.component.check.retry.interval=30" })
public class HealthCheckerConfigTest {

    private static int         customRetryCount    = 10;
    private static int         customRetryInterval = 30;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testCustomConfig() {
        ComponentHealthChecker componentHealthChecker = ctx.getBean(ComponentHealthChecker.class);
        Assert.assertEquals(customRetryCount, componentHealthChecker.getRetryCount());
        Assert.assertEquals(customRetryInterval, componentHealthChecker.getRetryTimeInterval());
    }
}