/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.health.test;

import com.alipay.sofa.healthcheck.impl.SofaComponentHealthChecker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmptyConfiguration.class)
@TestPropertySource(properties = {"com.alipay.sofa.healthcheck.component.check.retry.count=10", "com.alipay.sofa.healthcheck.component.check.retry.interval=30"})
public class HealthCheckerConfigTest {

    private static int customRetryCount = 10;
    private static int customRetryInterval = 30;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testCustomConfig() {
        SofaComponentHealthChecker sofaComponentHealthChecker = ctx.getBean(SofaComponentHealthChecker.class);
        Assert.assertEquals(customRetryCount, sofaComponentHealthChecker.getRetryCount());
        Assert.assertEquals(customRetryInterval, sofaComponentHealthChecker.getRetryTimeInterval());
    }
}