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

import com.alipay.sofa.healthcheck.bean.AfterReadinessCheckCallbackA;
import com.alipay.sofa.healthcheck.bean.HealthIndicatorB;
import com.alipay.sofa.healthcheck.bean.ReferenceA;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckProcessor;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus.HealthIndicatorDetail;
import com.alipay.sofa.healthcheck.util.BaseHealthCheckTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangen
 * @version $Id: ReadinessCheckProcessorTest.java, v 0.1 2018年03月12日 下午4:11 liangen Exp $
 */
public class ReadinessCheckProcessorTest extends BaseHealthCheckTest {

    private final ReadinessCheckProcessor readinessCheckProcessor = new ReadinessCheckProcessor();

    @Configuration
    static class HealthCheckConfiguration {

        @Bean
        public ReferenceA referenceA(@Value("${reference-a.count:0}") int count,
                                     @Value("${reference-a.strict:false}") boolean strict,
                                     @Value("${reference-a.retry-count:0}") int retryCount) {
            return new ReferenceA(count, strict, retryCount);
        }

        @Bean
        public HealthIndicatorB healthIndicatorB(@Value("${health-indicator-b.health:false}") boolean health) {
            return new HealthIndicatorB(health);
        }

        @Bean
        public AfterReadinessCheckCallbackA afterReadinessCheckCallbackA(@Value("${after-readiness-check-callback-a.health:false}") boolean health) {
            return new AfterReadinessCheckCallbackA(health);
        }
    }

    @Test
    public void testCheckHealthSuccess() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("reference-a.count", 5);
        properties.put("health-indicator-b.health", true);
        properties.put("after-readiness-check-callback-a.health", true);
        initApplicationContext(properties, HealthCheckConfiguration.class);

        readinessCheckProcessor.checkHealth();

        boolean springStatus = StartUpHealthCheckStatus.getSpringContextStatus();
        boolean componentStatus = StartUpHealthCheckStatus.getComponentStatus();
        boolean healthIndicatorStatus = StartUpHealthCheckStatus.getHealthIndicatorStatus();
        boolean afterStatus = StartUpHealthCheckStatus.getAfterHealthCheckCallbackStatus();
        Map<String, Health> componentDetail = StartUpHealthCheckStatus.getComponentDetail();
        List<HealthIndicatorDetail> healthIndicatorDetail = StartUpHealthCheckStatus
            .getHealthIndicatorDetails();
        Map<String, Health> afterDetail = StartUpHealthCheckStatus
            .getAfterHealthCheckCallbackDetails();

        Assert.assertTrue(springStatus);
        Assert.assertTrue(componentStatus);
        Assert.assertTrue(healthIndicatorStatus);
        Assert.assertTrue(afterStatus);

        Assert.assertEquals(1, componentDetail.size());
        Assert.assertEquals(1, healthIndicatorDetail.size());
        Assert.assertEquals(1, afterDetail.size());

        Assert.assertEquals("memory is ok", componentDetail.get("AAA").getDetails().get("memory"));
        Assert.assertEquals("HealthIndicatorB", healthIndicatorDetail.get(0).getName());
        Assert.assertEquals("hard disk is ok", healthIndicatorDetail.get(0).getHealth()
            .getDetails().get("hard disk"));
        Assert.assertEquals("server is ok", afterDetail.get("AfterReadinessCheckCallbackA")
            .getDetails().get("server"));

        StartUpHealthCheckStatus.clean();
    }

    @Test
    public void testCheckHealthFail() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("reference-a.count", 0);
        properties.put("reference-a.strict", true);
        properties.put("reference-a.retry-count", 4);
        properties.put("health-indicator-b.health", false);
        properties.put("after-readiness-check-callback-a.health", true);
        initApplicationContext(properties, HealthCheckConfiguration.class);

        readinessCheckProcessor.checkHealth();

        boolean springStatus = StartUpHealthCheckStatus.getSpringContextStatus();
        boolean componentStatus = StartUpHealthCheckStatus.getComponentStatus();
        boolean healthIndicatorStatus = StartUpHealthCheckStatus.getHealthIndicatorStatus();
        boolean afterStatus = StartUpHealthCheckStatus.getAfterHealthCheckCallbackStatus();
        Map<String, Health> componentDetail = StartUpHealthCheckStatus.getComponentDetail();
        List<HealthIndicatorDetail> healthIndicatorDetail = StartUpHealthCheckStatus
            .getHealthIndicatorDetails();
        Map<String, Health> afterDetail = StartUpHealthCheckStatus
            .getAfterHealthCheckCallbackDetails();

        Assert.assertTrue(springStatus);
        Assert.assertFalse(componentStatus);
        Assert.assertFalse(healthIndicatorStatus);
        Assert.assertFalse(afterStatus);

        Assert.assertEquals(1, componentDetail.size());
        Assert.assertEquals(1, healthIndicatorDetail.size());
        Assert.assertEquals(0, afterDetail.size());

        Assert.assertEquals("memory is deficiency",
            componentDetail.get("AAA").getDetails().get("memory"));
        Assert.assertEquals("HealthIndicatorB", healthIndicatorDetail.get(0).getName());
        Assert.assertEquals("hard disk is bad", healthIndicatorDetail.get(0).getHealth()
            .getDetails().get("hard disk"));

        StartUpHealthCheckStatus.clean();
    }
}