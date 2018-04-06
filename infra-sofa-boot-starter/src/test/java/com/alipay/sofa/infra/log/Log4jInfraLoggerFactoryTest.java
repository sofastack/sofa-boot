/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.infra.log;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.infra.log.base.AbstraceTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Log4jInfrastructureHealthCheckLoggerFactory Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>九月 20, 2016</pre>
 */
public class Log4jInfraLoggerFactoryTest extends AbstraceTestBase {

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
    public void testDebugGetLogger() throws Exception {
        //TODO: Test goes here...
        // 设置级别为Debug
        System.getProperties().put(restLogLevel, "DEBUG");
        try {
            String name1 = "com.test.1";
            org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            logger.debug("test1 debug ok");

            String name2 = "com.test.2";
            org.slf4j.Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
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
    public void testInfoGetLogger() throws Exception {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "INFO");
        try {
            String name1 = "com.test.3";
            org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            System.err.println("\nLoggerName1 : " + logger.getName() + " ,logger1:" + logger);
            assertNotNull(logger);
            logger.info("test1 info ok");

            String name2 = "com.test.4";
            org.slf4j.Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
            System.err.println("\nLoggerName2 : " + logger2.getName() + " ,logger2:" + logger2);
            logger2.info("test2 info ok");

            assertTrue(logger.isInfoEnabled());
            assertFalse(logger.isDebugEnabled());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWarnGetLogger() throws Exception {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "WARN");
        try {
            String name1 = "com.test.5";
            org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            assertNotNull(logger);
            logger.warn("test1 warn ok");

            String name2 = "com.test.6";
            org.slf4j.Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
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
    public void testErrorGetLogger() throws Exception {
        //TODO: Test goes here...
        System.getProperties().put(restLogLevel, "ERROR");
        try {
            String name1 = "com.test.7";
            org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            System.err.println("\nLoggerName1 : " + logger.getName() + " ,logger1:" + logger);
            assertNotNull(logger);
            logger.error("test1 error ok");

            String name2 = "com.test.8";
            org.slf4j.Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
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
