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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.sofa.boot.log.InfraLoggerFactory;
import com.alipay.sofa.boot.test.log.base.BaseLogTest;
import com.alipay.sofa.common.log.Constants;

/**
 * Log4jInfrastructureHealthCheckLoggerFactory Tester.
 *
 * @author <guanchao.ygc>
 * @since 1.0
 */
public class Log4jInfraLoggerFactoryTest extends BaseLogTest {

    @Before
    public void before() throws Exception {
        super.before();
        //禁用logback
        System.setProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
        //禁用log4j2
        System.setProperty(Constants.LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    @After
    public void after() throws Exception {
        super.after();
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testDebugGetLogger() {
        //TODO: Test goes here...
        // 设置级别为Debug
        System.getProperties().put(restLogLevel, "DEBUG");
        try {
            String name1 = "com.test.1";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            logger.debug("test1 debug ok");

            String name2 = "com.test.2";
            org.slf4j.Logger logger2 = InfraLoggerFactory.getLogger(name2);
            logger2.debug("test2 debug ok");

            assertFalse(logger.isTraceEnabled());

            assertTrue(logger.isInfoEnabled());
            assertTrue(logger.isDebugEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "INFO");
        try {
            String name1 = "com.test.3";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            System.err.println("\nLoggerName1 : " + logger.getName() + " ,logger1:" + logger);
            assertNotNull(logger);
            logger.info("test1 info ok");

            String name2 = "com.test.4";
            org.slf4j.Logger logger2 = InfraLoggerFactory.getLogger(name2);
            System.err.println("\nLoggerName2 : " + logger2.getName() + " ,logger2:" + logger2);
            logger2.info("test2 info ok");

            assertTrue(logger.isInfoEnabled());
            assertFalse(logger.isDebugEnabled());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWarnGetLogger() {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "WARN");
        try {
            String name1 = "com.test.5";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            logger.warn("test1 warn ok");

            String name2 = "com.test.6";
            org.slf4j.Logger logger2 = InfraLoggerFactory.getLogger(name2);
            System.err.println("\nLoggerName2 : " + logger2.getName() + " ,logger2:" + logger2);
            logger2.warn("test2 warn ok");

            assertFalse(logger.isInfoEnabled());
            assertFalse(logger.isDebugEnabled());
            assertTrue(logger.isWarnEnabled());
            assertTrue(logger.isErrorEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testErrorGetLogger() {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "ERROR");
        try {
            String name1 = "com.test.7";
            org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
            System.err.println("\nLoggerName1 : " + logger.getName() + " ,logger1:" + logger);
            assertNotNull(logger);
            logger.error("test1 error ok");

            String name2 = "com.test.8";
            org.slf4j.Logger logger2 = InfraLoggerFactory.getLogger(name2);
            System.err.println("\nLoggerName2 : " + logger2.getName() + " ,logger2:" + logger2);
            logger2.error("test2 error ok");

            assertFalse(logger.isInfoEnabled());
            assertFalse(logger.isDebugEnabled());
            assertFalse(logger.isWarnEnabled());
            assertTrue(logger.isErrorEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
