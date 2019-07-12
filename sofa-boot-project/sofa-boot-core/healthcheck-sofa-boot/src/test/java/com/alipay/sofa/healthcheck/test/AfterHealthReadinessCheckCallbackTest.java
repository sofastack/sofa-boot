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
package com.alipay.sofa.healthcheck.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.test.bean.ApplicationHealthCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.MiddlewareHealthCheckCallback;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class AfterHealthReadinessCheckCallbackTest {

    private ApplicationContext ctx;

    @Test
    public void testAfterReadinessCheckCallbackMarked() {
        initApplicationContext(true, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = ctx
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertTrue(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(hashMap.size() == 2);
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(applicationHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(middleHealth.getDetails().size() == 1);
        Assert.assertTrue(applicationHealth.getDetails().size() == 1);
        Assert.assertTrue("server is ok".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));
    }

    @Test
    public void testAfterReadinessCheckCallbackUnMarked() {
        initApplicationContext(false, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = ctx
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertFalse(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(hashMap.size() == 2);
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(Status.DOWN));
        Assert.assertTrue(applicationHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(middleHealth.getDetails().size() == 1);
        Assert.assertTrue("server is bad".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));
    }

    private void initApplicationContext(boolean health, boolean mark) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("after-readiness-check-callback-a.health", health);
        properties.put("after-readiness-check-callback-b.mark", mark);
        properties.put("spring.application.name", "AfterHealthReadinessCheckCallbackTest");
        SpringApplication springApplication = new SpringApplication(
            AfterHealthReadinessCheckCallbackTestConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ctx = springApplication.run();
    }

    @Configuration
    static class AfterHealthReadinessCheckCallbackTestConfiguration {
        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${after-readiness-check-callback-a.health:false}") boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public ApplicationHealthCheckCallback applicationHealthCheckCallback(@Value("${after-readiness-check-callback-b.mark:false}") boolean mark) {
            return new ApplicationHealthCheckCallback(mark);
        }

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public ReadinessCheckListener readinessCheckListener() {
            return new ReadinessCheckListener();
        }

        @Bean
        public HealthCheckerProcessor healthCheckerProcessor() {
            return new HealthCheckerProcessor();
        }

        @Bean
        public HealthIndicatorProcessor healthIndicatorProcessor() {
            return new HealthIndicatorProcessor();
        }
    }
}