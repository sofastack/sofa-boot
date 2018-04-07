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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.bean.AfterHealthCheckCallbackA;
import com.alipay.sofa.healthcheck.bean.AfterHealthCheckCallbackB;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: AfterHealthCheckCallbackProcessorTest.java, v 0.1 2018年03月11日 下午2:39 liangen Exp $
 */
public class AfterHealthCheckCallbackProcessorTest {

    private static ClassPathXmlApplicationContext   applicationContext;
    private final AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = new AfterHealthCheckCallbackProcessor();

    @BeforeClass
    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(
            "com/alipay/sofa/healthcheck/application_healthcheck_test.xml");
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testAfterHealthCheckCallback() {

        AfterHealthCheckCallbackA.setHealth(true);
        AfterHealthCheckCallbackB.setMark(false);
        boolean result_1 = afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback();
        Assert.assertTrue(result_1);
        Assert.assertTrue(AfterHealthCheckCallbackB.isMark());

        AfterHealthCheckCallbackA.setHealth(false);
        AfterHealthCheckCallbackB.setMark(false);
        boolean result_2 = afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback();
        Assert.assertFalse(result_2);
        Assert.assertFalse(AfterHealthCheckCallbackB.isMark());

    }

    @AfterClass
    public static void clean() {
        applicationContext.close();
        HealthCheckManager.init(null);
    }
}