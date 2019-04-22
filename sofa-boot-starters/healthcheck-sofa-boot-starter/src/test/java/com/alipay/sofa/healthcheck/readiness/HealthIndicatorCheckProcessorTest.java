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
package com.alipay.sofa.healthcheck.readiness;

import com.alipay.sofa.healthcheck.bean.DiskHealthIndicator;
import com.alipay.sofa.healthcheck.base.BaseHealthCheckTest;
import com.alipay.sofa.healthcheck.configuration.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.healthcheck.core.HealthIndicatorProcessor;
import org.junit.Assert;
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
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthIndicatorCheckProcessorTest extends BaseHealthCheckTest {

    @Configuration
    static class HealthIndicatorConfiguration {
        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health}") boolean health) {
            return new DiskHealthIndicator(health);
        }
    }

    @Test
    public void testCheckIndicatorPassed() {
        initApplicationContext(true);
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertTrue(result);
        Assert.assertTrue(hashMap.size() == 1);
        Assert.assertNotNull(diskHealth);
        Assert.assertTrue(diskHealth.getStatus().equals(Status.UP));
        Assert.assertTrue("hard disk is ok".equals(diskHealth.getDetails().get("disk")));
    }

    @Test
    public void testCheckIndicatorFailed() {
        initApplicationContext(false);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
            .getBean(HealthIndicatorProcessor.class);
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertFalse(result);
        Assert.assertTrue(hashMap.size() == 1);
        Assert.assertNotNull(diskHealth);
        Assert.assertTrue(diskHealth.getStatus().equals(Status.DOWN));
        Assert.assertTrue("hard disk is bad".equals(diskHealth.getDetails().get("disk")));
    }

    private void initApplicationContext(boolean health) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("disk-health-indicator.health", health);
        initApplicationContext(properties, HealthIndicatorConfiguration.class,
            SofaBootHealthCheckAutoConfiguration.class);
    }

}