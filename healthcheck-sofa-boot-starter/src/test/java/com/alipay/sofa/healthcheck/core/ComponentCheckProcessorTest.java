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

import com.alipay.sofa.healthcheck.bean.ReferenceA;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: ComponentCheckProcessor.java, v 0.1 2018年03月10日 下午10:15 liangen Exp $
 */
public class ComponentCheckProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final ComponentCheckProcessor         componentCheckProcessor = new ComponentCheckProcessor();

    @BeforeClass
    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(
            "com/alipay/sofa/healthcheck/application_healthcheck_test.xml");
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testSartupCheckComponentForRetry() {
        ReferenceA.setCount(0);
        ReferenceA.setRetryCount(20);
        ReferenceA.setStrict(true);

        boolean result = componentCheckProcessor.startupCheckComponent();

        Assert.assertTrue(result);
        Assert.assertEquals(6, ReferenceA.getCount());

    }

    @Test
    public void testSartupCheckComponentForNotStrict() {
        ReferenceA.setCount(0);
        ReferenceA.setStrict(true);
        ReferenceA.setRetryCount(4);

        boolean result_1 = componentCheckProcessor.startupCheckComponent();
        Assert.assertFalse(result_1);
        Assert.assertEquals(5, ReferenceA.getCount());

        ReferenceA.setCount(0);
        ReferenceA.setStrict(false);
        ReferenceA.setRetryCount(4);

        boolean result_2 = componentCheckProcessor.startupCheckComponent();
        Assert.assertTrue(result_2);
        Assert.assertEquals(5, ReferenceA.getCount());

    }

    @Test
    public void testHttpCheckComponent() {
        ReferenceA.setCount(4);
        ReferenceA.setStrict(true);
        ReferenceA.setRetryCount(5);

        Map<String, Health> details_1 = new HashMap<>();
        boolean result_1 = componentCheckProcessor.httpCheckComponent(details_1);
        Assert.assertFalse(result_1);

        Assert.assertEquals(5, ReferenceA.getCount());
        Assert.assertEquals(2, details_1.size());
        Assert.assertEquals(Status.DOWN, details_1.get("AAA").getStatus());
        Assert
            .assertEquals("memory is deficiency", details_1.get("AAA").getDetails().get("memory"));
        Assert.assertEquals(Status.UP, details_1.get("BBB").getStatus());
        Assert.assertEquals("network is ok", details_1.get("BBB").getDetails().get("network"));

    }

    @AfterClass
    public static void clean() {
        applicationContext.close();
        HealthCheckManager.init(null);
        StartUpHealthCheckStatus.clean();
    }
}