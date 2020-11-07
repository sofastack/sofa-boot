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
package com.alipay.sofa.boot.test.log;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.alipay.sofa.boot.log.InfraLoggerFactory;
import com.alipay.sofa.boot.test.log.base.BaseLogTest;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.env.LogEnvUtils;

/**
 * Log4jRestLoggerFactory Tester.
 *
 * @author <guanchao.ygc>
 * @since 1.0
 */
public class LogbackRestLoggerFactoryTest extends BaseLogTest {

    @Before
    public void before() throws Exception {
        super.before();
        //disable logback
        System.setProperty(Constants.LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        //disable log4j
        System.setProperty(Constants.LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testDebugGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(restLogLevel, "DEBUG");
        try {
            String name1 = "com.test.name";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            assertFalse(logger.isTraceEnabled());
            assertTrue(logger.isDebugEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(restLogLevel);
        }
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(restLogLevel, "INFO");
        try {
            String name1 = "com.test.name";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            assertTrue(logger.isInfoEnabled());
            assertFalse(logger.isDebugEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(restLogLevel);
        }
    }

    @Test
    public void testWarnGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(restLogLevel, "WARN");
        try {
            String name1 = "com.test.name";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            assertFalse(logger.isInfoEnabled());
            assertTrue(logger.isWarnEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(restLogLevel);
        }
    }

    @Test
    public void testErrorGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(restLogLevel, "ERROR");
        try {
            String name1 = "com.test.name";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            assertFalse(logger.isWarnEnabled());
            assertTrue(logger.isErrorEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(restLogLevel);
        }
    }
}
