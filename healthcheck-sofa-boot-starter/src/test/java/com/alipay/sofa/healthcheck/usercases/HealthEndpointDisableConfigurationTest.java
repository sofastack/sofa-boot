/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.usercases;

import com.alipay.sofa.healthcheck.base.SofaBootTestApplication;
import com.alipay.sofa.healthcheck.service.SofaBootReadinessCheckEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"com.alipay.sofa.healthcheck.readiness.enabled=false"})
public class HealthEndpointDisableConfigurationTest {

    @Autowired
    public ApplicationContext ctx;

    @Test
    public void test() {
        SofaBootReadinessCheckEndpoint sofaBootReadinessCheckEndpoint = (SofaBootReadinessCheckEndpoint)ctx.getBean("readinessCheck");
        Assert.assertFalse(sofaBootReadinessCheckEndpoint.isEnabled());

        boolean sofaBootVersionEndpointMvcAdapter = ctx.containsBean("sofaBootReadinessCheckMvcEndpoint");
        Assert.assertFalse(sofaBootVersionEndpointMvcAdapter);
    }

}