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
package com.alipay.sofa.boot.test.cloud;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;

/**
 * Fix https://github.com/alipay/sofa-boot/pull/268
 *
 * @author qilong.zql
 * @since 2.5.0
 */
@SpringBootTest(classes = SpringCloudEnvTest.SpringCloudEnvTestConfiguration.class)
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
        Assert.assertEquals("sofa-boot-test", SampleSpringContextInitializer.bootstrapEnvironment
            .getProperty(SofaBootConstants.APP_NAME_KEY));
        Assert.assertEquals("sofa-boot-test", SampleSpringContextInitializer.applicationEnvironment
            .getProperty(SofaBootConstants.APP_NAME_KEY));
        Assert.assertEquals("INFO", SampleSpringContextInitializer.bootstrapEnvironment
            .getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("INFO", SampleSpringContextInitializer.applicationEnvironment
            .getProperty("logging.level.com.alipay.test"));
        Assert.assertEquals("WARN", SampleSpringContextInitializer.bootstrapEnvironment
            .getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("WARN", SampleSpringContextInitializer.applicationEnvironment
            .getProperty("logging.level.com.test.demo"));
        Assert.assertEquals("./logs",
            SampleSpringContextInitializer.bootstrapEnvironment.getProperty("logging.path"));
        Assert.assertEquals("./logs",
            SampleSpringContextInitializer.applicationEnvironment.getProperty("logging.path"));
        Assert.assertEquals(null,
            SampleSpringContextInitializer.bootstrapEnvironment.getProperty("any.key"));
        Assert.assertEquals("any.value",
            SampleSpringContextInitializer.applicationEnvironment.getProperty("any.key"));
    }

    @After
    public void clearLogDir() {
        FileSystemUtils.deleteRecursively(new File("./logs"));
    }

    @Configuration
    @EnableAutoConfiguration
    static class SpringCloudEnvTestConfiguration {

    }

}