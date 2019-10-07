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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.sofa.boot.log.InfraLoggerFactory;
import com.alipay.sofa.boot.test.log.base.BaseLogTest;
import com.alipay.sofa.common.log.Constants;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
public class AllInfraLoggerFactoryTest extends BaseLogTest {

    @Before
    public void before() throws Exception {
        super.before();
        // disable all log space
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    @After
    public void after() throws Exception {
        super.after();
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "false");
    }

    @Test
    public void testGetLogger() {
        org.slf4j.Logger logger = InfraLoggerFactory.getLogger(this.getClass());
        Assert.assertNotNull(logger);
        logger.info("ok testGetLogger by class");
        Assert.assertTrue(logger.isErrorEnabled());
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
        org.slf4j.Logger logger = InfraLoggerFactory.getLogger(name1);
        System.err.println("\nLoggerName1 : " + logger.getName() + " ,logger1:" + logger);
        Assert.assertNotNull(logger);
        logger.info("test1 info ok");

        String name2 = "com.test.4";
        org.slf4j.Logger logger2 = InfraLoggerFactory.getLogger(name2);
        System.err.println("\nLoggerName2 : " + logger2.getName() + " ,logger2:" + logger2);
        logger2.info("test2 info ok");

        Assert.assertTrue(logger.isInfoEnabled());
    }
}
