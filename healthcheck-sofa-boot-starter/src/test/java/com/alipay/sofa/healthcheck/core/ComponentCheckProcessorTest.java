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
import com.alipay.sofa.healthcheck.bean.ReferenceB;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.util.BaseHealthCheckTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: ComponentCheckProcessor.java, v 0.1 2018年03月10日 下午10:15 liangen Exp $
 */
public class ComponentCheckProcessorTest extends BaseHealthCheckTest {

    private final ComponentCheckProcessor componentCheckProcessor = new ComponentCheckProcessor();

    @Before
    public void init() {
        HealthCheckManager.init(this.applicationContext);
    }

    @After
    public void closeContext() {
        HealthCheckManager.init(null);
        StartUpHealthCheckStatus.clean();
        this.applicationContext.close();
    }

    @Configuration
    static class ReferenceConfiguration {

        @Bean
        public ReferenceA referenceA(@Value("${reference-a.count:0}") int count,
                                     @Value("${reference-a.strict:false}") boolean strict,
                                     @Value("${reference-a.retry-count:0}") int retryCount) {
            return new ReferenceA(count, strict, retryCount);
        }

        @Bean
        public ReferenceB referenceB(@Value("${reference-b.strict:false}") boolean strict,
                                     @Value("${reference-b.retry-count:0}") int retryCount) {
            return new ReferenceB(strict, retryCount);
        }
    }

    @Test
    public void testStartupCheckComponentForRetry() {
        initApplicationContext(0, true, 20);

        boolean result = componentCheckProcessor.startupCheckComponent();

        Assert.assertTrue(result);
        Assert.assertEquals(6, applicationContext.getBean(ReferenceA.class).getCount());
    }

    @Test
    public void testStartupCheckComponentForStrict() {
        initApplicationContext(0, true, 4);

        boolean result = componentCheckProcessor.startupCheckComponent();

        Assert.assertFalse(result);
        Assert.assertEquals(5, applicationContext.getBean(ReferenceA.class).getCount());
    }

    @Test
    public void testStartupCheckComponentForNotStrict() {
        initApplicationContext(0, false, 4);

        boolean result = componentCheckProcessor.startupCheckComponent();
        Assert.assertTrue(result);
        Assert.assertEquals(5, applicationContext.getBean(ReferenceA.class).getCount());
    }

    @Test
    public void testHttpCheckComponent() {
        initApplicationContext(4, true, 5);

        Map<String, Health> details = new HashMap<>();
        boolean result = componentCheckProcessor.livenessCheckComponent(details);

        Assert.assertFalse(result);

        Assert.assertEquals(5, applicationContext.getBean(ReferenceA.class).getCount());
        Assert.assertEquals(2, details.size());
        Assert.assertEquals(Status.DOWN, details.get("AAA").getStatus());
        Assert.assertEquals("memory is deficiency", details.get("AAA").getDetails().get("memory"));
        Assert.assertEquals(Status.UP, details.get("BBB").getStatus());
        Assert.assertEquals("network is ok", details.get("BBB").getDetails().get("network"));
    }

    private void initApplicationContext(int count, boolean strict, int retryCount) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("reference-a.count", count);
        properties.put("reference-a.strict", strict);
        properties.put("reference-a.retry-count", retryCount);

        initApplicationContext(properties, ReferenceConfiguration.class);
    }
}