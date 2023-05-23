/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBean;
import com.alipay.sofa.runtime.test.beans.service.ServiceWithoutInterface;
import com.alipay.sofa.runtime.test.beans.service.SofaServiceBeanService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ServiceBeanTest
 *
 * @author xunfang
 * @version ServiceBeanTest.java, v 0.1 2023/5/23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=SofaServiceBeanService")
public class ServiceBeanTest {
    @SofaReference(uniqueId = "sofaServiceBeanService")
    private SofaServiceBeanService sofaServiceBeanService;

    @Test
    public void testServiceNoInterface() {
        Assert.assertNotNull(sofaServiceBeanService);
        Assert.assertEquals("SofaServiceBeanService", sofaServiceBeanService.service());
    }

}
