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
package com.alipay.sofa.infra.log;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.infra.log.base.AbstractTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by yangguanchao on 18/01/04.
 */
public class AllInfraLoggerFactoryTest extends AbstractTestBase {

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
    public void testGetLogger() {
        org.slf4j.Logger logger = InfraHealthCheckLoggerFactory.getLogger(this.getClass());
        assertNotNull(logger);
        logger.info("ok testGetLogger by class");
        assertTrue(logger.isErrorEnabled());
    }

    /**
     * 禁用我们的日志空间管理
     * <p/>
     * 使用业务的日志管理: sofa-rest-log/src/test/log4j.xml 文件
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() {
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
    }
}
