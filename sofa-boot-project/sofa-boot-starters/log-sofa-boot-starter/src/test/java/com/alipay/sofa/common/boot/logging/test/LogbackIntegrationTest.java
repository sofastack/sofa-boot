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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.ThreadContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.utils.ReportUtil;
import com.alipay.sofa.common.utils.StringUtil;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
public class LogbackIntegrationTest extends BaseLogIntegrationTest {

    @Test
    public void testDefaultLevel() throws IOException {
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        ConfigurableApplicationContext applicationContext = springApplication.run(new String[] {});
        Environment environment = applicationContext.getEnvironment();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        logger.debug("debug level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(1, contents.size());
        Assert.assertTrue(contents.get(0).contains("info level"));
    }

    /**
     * test logging.level.com.* config
     * @throws IOException
     */
    @Test
    public void testWildLogLevel() throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.LOG_LEVEL_PREFIX + "test.*", "debug");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        ConfigurableApplicationContext applicationContext = springApplication.run(new String[] {});
        Environment environment = applicationContext.getEnvironment();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        logger.debug("debug level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(2, contents.size());
        Assert.assertTrue(contents.get(0).contains("info level"));
        Assert.assertTrue(contents.get(1).contains("debug level"));
        LogEnvUtils.processGlobalSystemLogProperties()
            .remove(Constants.LOG_LEVEL_PREFIX + "test.*");
    }

    /**
     * test logging.level.{space id} config
     * @throws IOException
     */
    @Test
    public void testSpecifyLogLevel() throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.LOG_LEVEL_PREFIX + "test.space", "debug");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        ConfigurableApplicationContext applicationContext = springApplication.run(new String[] {});
        Environment environment = applicationContext.getEnvironment();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        logger.debug("debug level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(2, contents.size());
        Assert.assertTrue(contents.get(0).contains("info level"));
        Assert.assertTrue(contents.get(1).contains("debug level"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.LOG_LEVEL_PREFIX + "test.space");
    }

    /**
     * test logging.config.{space id} config
     * @throws IOException
     */
    @Test
    public void testLogConfig() throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.LOG_CONFIG_PREFIX + TEST_SPACE, "logback-test-conf.xml");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        ConfigurableApplicationContext applicationContext = springApplication.run(new String[] {});
        Environment environment = applicationContext.getEnvironment();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(1, contents.size());
        Assert.assertTrue(contents.get(0).contains("logback-test-conf"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.LOG_CONFIG_PREFIX + TEST_SPACE);
    }

    /**
     * test sofa.middleware.log.internal.level config
     */
    @Test
    public void testInternalLogLevel() {
        ReportUtil.reportDebug("debug");
        Assert.assertFalse(outContent.toString().contains("debug"));
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_INTERNAL_LEVEL, "debug");
        ReportUtil.reportDebug("debug");
        Assert.assertTrue(outContent.toString().contains("debug"));
    }

    /**
     * test sofa.middleware.log.{space id}.console config
     */
    @Test
    public void testSpaceConsoleConfig() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE), "true");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("space console");
        logger.debug("space console debug");
        Assert.assertTrue(outContent.toString().contains("space console"));
        Assert.assertFalse(outContent.toString().contains("space console debug"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE));
    }

    /**
     * test sofa.middleware.log.{space id}.console.level config
     */
    @Test
    public void testSpaceConsoleLevelConfig() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE), "true");
        properties.put(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, TEST_SPACE), "debug");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("space console");
        logger.debug("space console debug");
        Assert.assertTrue(outContent.toString().contains("space console"));
        Assert.assertTrue(outContent.toString().contains("space console debug"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, TEST_SPACE));
    }

    /**
     * test sofa.middleware.log.console config
     */
    @Test
    public void testGlobalConsoleConfig() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("global space console");
        logger.debug("global space console debug");
        Assert.assertTrue(outContent.toString().contains("global space console"));
        Assert.assertFalse(outContent.toString().contains("global space console debug"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
    }

    /**
     * test sofa.middleware.log.console.level config
     */
    @Test
    public void testGlobalConsoleLevelConfig() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "debug");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("global space console");
        logger.debug("global space console debug");
        Assert.assertTrue(outContent.toString().contains("global space console"));
        Assert.assertTrue(outContent.toString().contains("global space console debug"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL);
    }

    /**
     * test space config override global config
     * @throws IOException
     */
    @Test
    public void testSpaceOverrideGlobalConfig() throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "debug");
        properties
            .put(String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE),
                "false");
        properties.put(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, TEST_SPACE), "info");

        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        ConfigurableApplicationContext applicationContext = springApplication.run(new String[] {});
        Environment environment = applicationContext.getEnvironment();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        logger.debug("debug level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(1, contents.size());
        Assert.assertTrue(contents.get(0).contains("info level"));
        Assert.assertFalse(outContent.toString().contains("info level"));
        Assert.assertFalse(outContent.toString().contains("debug level"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL);
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_SWITCH, TEST_SPACE));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            String.format(Constants.SOFA_MIDDLEWARE_SINGLE_LOG_CONSOLE_LEVEL, TEST_SPACE));
    }

    /**
     * test sofa.middleware.log.console.logback.pattern config
     */
    @Test
    public void testLogbackLogConsolePattern() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        properties.put(Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN,
            "logback-test-console-pattern"
                    + Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN_DEFAULT);
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("global space console");
        Assert.assertTrue(outContent.toString().contains("logback-test-console-pattern"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH);
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_LOG_CONSOLE_LOGBACK_PATTERN);
    }

    @Test
    public void testThreadContextConfiguration() {
        try {
            System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
            SPACES_MAP.remove(new SpaceId(TEST_SPACE));
            LoggerSpaceManager.getLoggerBySpace(LogbackIntegrationTest.class.getCanonicalName(),
                TEST_SPACE);
            ThreadContext.put("testKey", "testValue");
            ThreadContext.put("logging.path", "anyPath");
            Map<String, Object> properties = new HashMap<String, Object>();
            SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
            springApplication.setDefaultProperties(properties);
            springApplication.run(new String[] {});
            Assert.assertTrue("testValue".equals(ThreadContext.get("testKey")));
            Assert.assertTrue(Constants.LOGGING_PATH_DEFAULT.equals(ThreadContext
                .get("logging.path")));
        } finally {
            System.getProperties().remove(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        }
    }

    /**
     * test sofa.middleware.log.disable
     */
    @Test
    public void testDisableMiddleLog() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run(new String[] {});
        logger.info("global space console");
        logger.debug("global space console debug");
        Assert.assertFalse(outContent.toString().contains("global space console"));
        Assert.assertFalse(outContent.toString().contains("global space console debug"));
        LogEnvUtils.processGlobalSystemLogProperties().remove(
            Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
    }

    protected File getLogbackDefaultFile(Environment environment) {
        String loggingRoot = environment.getProperty(Constants.LOG_PATH_PREFIX + TEST_SPACE);
        if (StringUtil.isBlank(loggingRoot)) {
            loggingRoot = environment.getProperty(Constants.LOG_PATH);
        }
        return new File(loggingRoot + File.separator + "test-space" + File.separator
                        + "logback-common-default.log");
    }

    @EnableAutoConfiguration
    @Configuration
    static class EmptyConfig {
    }
}