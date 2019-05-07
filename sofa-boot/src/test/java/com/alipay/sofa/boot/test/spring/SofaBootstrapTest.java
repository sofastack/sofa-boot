/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.boot.test.spring;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.test.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Fix https://github.com/alipay/sofa-boot/issues/371
 *
 * @author qilong.zql
 * @since  2.5.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SofaBootstrapTest {

    @Autowired
    private Environment environment;

    @Test
    public void commandLineArgsTest() {
        Throwable throwable = null;
        try {
            SpringApplication springApplication = new SpringApplication(EmptyConfiguration.class);
            String[] args = { "-A=B" };
            springApplication.run(args);
        } catch (Throwable t) {
            throwable = t;
        }
        Assert.assertNull(throwable);
    }

    @Test
    public void environmentCustomizeTest() {
        MutablePropertySources propertySources = ((StandardEnvironment) environment).getPropertySources();
        Assert.assertNotNull(propertySources.get(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE));
    }
}