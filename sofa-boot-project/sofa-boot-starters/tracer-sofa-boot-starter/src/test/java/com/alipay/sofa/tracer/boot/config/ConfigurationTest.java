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
package com.alipay.sofa.tracer.boot.config;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alipay.common.tracer.core.appender.file.TimedRollingFileAppender;
import com.alipay.sofa.tracer.boot.base.ConfigurationHolder;
import com.alipay.sofa.tracer.boot.base.SpringBootWebApplication;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;

/**
 * @author qilong.zql
 * @since 2.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-config.properties")
public class ConfigurationTest {

    @BeforeClass
    public static void before() {
        File defaultDir = new File(System.getProperty("user.home") + File.separator + "logs"
                                   + File.separator + "tracelog");
        File configDir = new File(System.getProperty("user.dir") + File.separator + "logs"
                                  + File.separator + "tracelog");
        System.clearProperty("logging.path");
        if (defaultDir.exists()) {
            FileUtils.deleteQuietly(defaultDir);
        }
        if (configDir.exists()) {
            FileUtils.deleteQuietly(configDir);
        }
    }

    @Test
    public void testAdvanceTracerConfig() {
        SofaTracerProperties sofaTracerProperties = ConfigurationHolder.getSofaTracerProperties();
        Assert.assertEquals("true", sofaTracerProperties.getDisableDigestLog());

        Map<String, String> disableConfiguration = sofaTracerProperties.getDisableConfiguration();
        Assert.assertTrue("v1".equals(disableConfiguration.get("k1")));
        Assert.assertTrue("v2".equals(disableConfiguration.get("k2")));
        Assert.assertEquals(2, disableConfiguration.size());

        Assert.assertEquals(TimedRollingFileAppender.HOURLY_ROLLING_PATTERN,
            sofaTracerProperties.getTracerGlobalRollingPolicy());
        Assert.assertEquals("1", sofaTracerProperties.getTracerGlobalLogReserveDay());
        Assert.assertEquals("1", sofaTracerProperties.getStatLogInterval());
        Assert.assertEquals("1", sofaTracerProperties.getBaggageMaxLength());
    }

    //    @Test
    //    public void testTracerLogDir() {
    //        File defaultDir = new File(System.getProperty("user.home") + File.separator + "logs"
    //                                   + File.separator + "tracelog");
    //        File configDir = new File(System.getProperty("user.dir") + File.separator + "logs"
    //                                  + File.separator + "tracelog");
    //        Assert.assertFalse(defaultDir.exists());
    //        Assert.assertTrue(configDir.exists());
    //    }

}