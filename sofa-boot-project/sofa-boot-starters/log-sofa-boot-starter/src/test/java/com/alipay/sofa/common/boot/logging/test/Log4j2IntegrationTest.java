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
package com.alipay.sofa.common.boot.logging.test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.alipay.sofa.common.log.LoggerSpaceManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;

import com.alipay.sofa.common.log.Constants;

/**
 * @author qilong.zql
 * @since 1.0.19
 */
public class Log4j2IntegrationTest extends BaseLogIntegrationTest {

    protected static Map<Object, Object> MANAGER_MAP;

    static {
        try {
            Field MAP = AbstractManager.class.getDeclaredField("MAP");
            MAP.setAccessible(true);
            MANAGER_MAP = (Map<Object, Object>) MAP.get(AbstractManager.class);
        } catch (Throwable throwable) {
            // ignore
        }
    }

    @Before
    public void before() {
        MANAGER_MAP.clear();
        System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        setUpStreams();
    }

    @After
    @Override
    public void restoreStreams() throws Exception {
        super.restoreStreams();
        System.getProperties().remove(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
    }

    protected Logger getLogger() {
        return LoggerSpaceManager.getLoggerBySpace(Log4j2IntegrationTest.class.getCanonicalName(),
            TEST_SPACE);
    }

    /**
     * test log4j2 info log to console
     */
    @Test
    public void testLog4j2InfoToConsole() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE), "true");
        SpringApplication springApplication = new SpringApplication(
            LogbackIntegrationTest.EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run();
        logger = getLogger();
        logger.info("space console");
        Assert.assertTrue(outContent.toString().contains("space console"));
        System.clearProperty(String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH,
            TEST_SPACE));
    }

    @Test
    public void testThreadContextConfiguration() {
        try {
            System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
            ThreadContext.put("testKey", "testValue");
            ThreadContext.put("logging.path", "anyPath");
            Map<String, Object> properties = new HashMap<>();
            SpringApplication springApplication = new SpringApplication(
                LogbackIntegrationTest.EmptyConfig.class);
            springApplication.setDefaultProperties(properties);
            springApplication.run();
            logger = getLogger();
            Assert.assertEquals("testValue", ThreadContext.get("testKey"));
            Assert.assertEquals(Constants.LOGGING_PATH_DEFAULT, ThreadContext.get("logging.path"));
        } finally {
            System.getProperties().remove(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        }
    }
}
