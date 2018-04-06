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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * AllRestLoggerFactoryTest
 * <p/>
 * Created by yangguanchao on 18/01/04.
 */
public class AllInfraLoggerFactoryTest extends AbstraceTestBase {

    @Before
    public void before() throws Exception {
        super.before();
        //禁用我们的日志空间
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    @After
    public void after() throws Exception {
        super.after();
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");
    }

    @Test
    public void testGetLogger() throws Exception {
        try {
            org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(this.getClass());
            assertNotNull(logger);
            logger.info("ok testGetLogger by class");
            assertTrue(logger.isErrorEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 禁用我们的日志空间管理
     * <p/>
     * 使用业务的日志管理: sofa-rest-log/src/test/log4j.xml 文件
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() throws Exception {
        //TODO: Test goes here...
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
            assertTrue(logger.isDebugEnabled());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
