/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.boot.test.cloud;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.test.EmptyConfiguration;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Fix https://github.com/alipay/sofa-boot/pull/268
 *
 * @author qilong.zql
 * @since 2.5.0
 */
@SpringBootTest(classes = EmptyConfiguration.class)
@RunWith(SpringRunner.class)
public class SpringCloudEnvTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testIsSpringCloudBootstrapEnvironment() {
        Environment environment = ctx.getEnvironment();
        Assert.assertFalse(SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(environment));
        Assert.assertEquals(1L, SampleSpringContextInitializer.bootstrapContext.get());
        Assert.assertEquals(1L, SampleSpringContextInitializer.applicationContext.get());
        Assert.assertFalse(SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(null));
        Assert.assertEquals("sofa-boot-test",
                SampleSpringContextInitializer.bootstrapEnvironment.getProperty(SofaBootConstants.APP_NAME_KEY));
        Assert.assertEquals("sofa-boot-test",
                SampleSpringContextInitializer.applicationEnvironment.getProperty(SofaBootConstants.APP_NAME_KEY));
        Assert.assertEquals("INFO", SampleSpringContextInitializer.bootstrapEnvironment.getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("INFO", SampleSpringContextInitializer.applicationEnvironment.getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("WARN", SampleSpringContextInitializer.bootstrapEnvironment.getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("WARN", SampleSpringContextInitializer.applicationEnvironment.getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("./logs", SampleSpringContextInitializer.bootstrapEnvironment.getProperty("logging.path"));
        Assert.assertEquals("./logs", SampleSpringContextInitializer.applicationEnvironment.getProperty("logging.path"));
        Assert.assertEquals(null, SampleSpringContextInitializer.bootstrapEnvironment.getProperty("any.key"));
        Assert.assertEquals("any.value", SampleSpringContextInitializer.applicationEnvironment.getProperty("any.key"));
    }

}