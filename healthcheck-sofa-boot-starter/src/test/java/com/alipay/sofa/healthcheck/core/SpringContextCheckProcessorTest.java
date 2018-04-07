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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: SpringContextCheckProcessorTest.java, v 0.1 2018年03月12日 下午2:38 liangen Exp $
 */
public class SpringContextCheckProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final SpringContextCheckProcessor     springContextCheckProcessor = new SpringContextCheckProcessor();

    @BeforeClass
    public static void init() {
        try {
            applicationContext = new ClassPathXmlApplicationContext(
                "com/alipay/sofa/healthcheck/application_healthcheck_test_error.xml");

        } catch (Exception e) {
            System.out.println(e);
        }
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testSpringContextCheck() {
        boolean result = springContextCheckProcessor.springContextCheck();
        Assert.assertFalse(result);
    }

    @AfterClass
    public static void clean() {
        if (applicationContext != null) {
            applicationContext.close();
            HealthCheckManager.init(null);
        }
    }
}