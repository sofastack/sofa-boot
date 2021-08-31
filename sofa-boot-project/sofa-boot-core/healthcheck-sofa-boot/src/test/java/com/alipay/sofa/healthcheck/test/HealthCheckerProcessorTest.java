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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.impl.ComponentHealthChecker;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthChecker;
import com.alipay.sofa.healthcheck.test.bean.MemoryHealthChecker;
import com.alipay.sofa.healthcheck.test.bean.NetworkHealthChecker;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class HealthCheckerProcessorTest {

    private ApplicationContext applicationContext;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    static class HealthCheckerProcessorTestConfiguration {
        @Bean
        public DiskHealthChecker diskHealthChecker() {
            return new DiskHealthChecker();
        }

        @Bean
        public NetworkHealthChecker networkHealthChecker(@Value("${network-health-checker.strict:false}") boolean strict,
                                                         @Value("${network-health-checker.retry-count:0}") int retryCount) {
            return new NetworkHealthChecker(strict, retryCount);
        }

        @Bean
        public MemoryHealthChecker memoryHealthChecker(@Value("${memory-health-checker.count:0}") int count,
                                                       @Value("${memory-health-checker.strict:false}") boolean strict,
                                                       @Value("${memory-health-checker.retry-count:0}") int retryCount) {
            return new MemoryHealthChecker(count, strict, retryCount);
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

    @Test
    public void testInterfaceOrder() {
        initApplicationContext(0, true, 20);
        Map<String, HealthChecker> beansOfType = applicationContext
            .getBeansOfType(HealthChecker.class);
        Map<String, HealthChecker> orderedResult = HealthCheckUtils.sortMapAccordingToValue(
            beansOfType, applicationContext.getAutowireCapableBeanFactory());
        List<String> healthCheckerId = new ArrayList<>(orderedResult.keySet());
        Assert.assertEquals("memoryHealthChecker", healthCheckerId.get(0));
        Assert.assertEquals("networkHealthChecker", healthCheckerId.get(1));
        Assert.assertEquals("diskHealthChecker", healthCheckerId.get(2));
    }

    @Test
    public void testReadinessCheckComponentForRetry() {
        initApplicationContext(0, true, 20);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
            .getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext
            .getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(result);
        Assert.assertEquals(6, memoryHealthChecker.getCount());
        Assert.assertEquals(3, hashMap.size());
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertEquals(memoryHealth.getStatus(), Status.UP);
        Assert.assertEquals(networkHealth.getStatus(), Status.UP);
        Assert.assertEquals("memory is ok", memoryHealth.getDetails().get("memory"));
        Assert.assertEquals("network is ok", networkHealth.getDetails().get("network"));
    }

    @Test
    public void testReadinessCheckComponentForStrict() {
        initApplicationContext(0, true, 4);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
            .getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext
            .getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertFalse(result);
        Assert.assertEquals(4, memoryHealthChecker.getCount());
        Assert.assertEquals(3, hashMap.size());
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertEquals(memoryHealth.getStatus(), Status.DOWN);
        Assert.assertEquals(networkHealth.getStatus(), Status.UP);
        Assert.assertEquals("memory is bad", memoryHealth.getDetails().get("memory"));
        Assert.assertEquals("network is ok", networkHealth.getDetails().get("network"));
    }

    @Test
    public void testStartupCheckComponentForNotStrict() {
        initApplicationContext(0, false, 4);
        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
            .getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext
            .getBean(MemoryHealthChecker.class);
        boolean result = healthCheckerProcessor.readinessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(result);
        Assert.assertEquals(4, memoryHealthChecker.getCount());
        Assert.assertEquals(3, hashMap.size());
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertEquals(memoryHealth.getStatus(), Status.DOWN);
        Assert.assertEquals(networkHealth.getStatus(), Status.UP);
        Assert.assertEquals("memory is bad", memoryHealth.getDetails().get("memory"));
        Assert.assertEquals("network is ok", networkHealth.getDetails().get("network"));
    }

    @Test
    public void testHttpCheckComponent() {
        initApplicationContext(4, false, 5);

        HashMap<String, Health> hashMap = new HashMap<>();
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
            .getBean(HealthCheckerProcessor.class);
        MemoryHealthChecker memoryHealthChecker = applicationContext
            .getBean(MemoryHealthChecker.class);
        healthCheckerProcessor.livenessHealthCheck(hashMap);
        Health memoryHealth = hashMap.get("memoryHealthChecker");
        Health networkHealth = hashMap.get("networkHealthChecker");
        Assert.assertTrue(true);
        Assert.assertEquals(5, memoryHealthChecker.getCount());
        Assert.assertEquals(3, hashMap.size());
        Assert.assertNotNull(memoryHealth);
        Assert.assertNotNull(networkHealth);
        Assert.assertEquals(memoryHealth.getStatus(), Status.DOWN);
        Assert.assertEquals(networkHealth.getStatus(), Status.UP);
        Assert.assertEquals("memory is bad", memoryHealth.getDetails().get("memory"));
        Assert.assertEquals("network is ok", networkHealth.getDetails().get("network"));
    }

    @Test
    public void testComponentHealthCheckerFailedFirst() {
        SofaRuntimeManager manager = new StandardSofaRuntimeManager(
            "testComponentHealthCheckerFailedFirst",
            Thread.currentThread().getContextClassLoader(), null);
        manager.getComponentManager().register(new TestComponent("component1", true));
        manager.getComponentManager().register(new TestComponent("component2", true));
        manager.getComponentManager().register(new TestComponent("component3", false));
        manager.getComponentManager().register(new TestComponent("component4", true));
        manager.getComponentManager().register(new TestComponent("component5", false));
        ComponentHealthChecker componentHealthChecker = new ComponentHealthChecker(
            new SofaRuntimeContext(manager, manager.getComponentManager(), null));
        int i = 0;
        for (Map.Entry<String, Object> entry : componentHealthChecker.isHealthy().getDetails()
            .entrySet()) {
            if (i < 2) {
                Assert.assertEquals(entry.getValue().toString(), "failed");
            } else {
                Assert.assertEquals(entry.getValue().toString(), "passed");
            }
            ++i;
        }
    }

    private void initApplicationContext(int count, boolean strict, int retryCount) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("memory-health-checker.count", count);
        properties.put("memory-health-checker.strict", strict);
        properties.put("memory-health-checker.retry-count", retryCount);
        properties.put("spring.application.name", "HealthCheckerProcessorTest");
        properties.put(SofaBootConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK, true);

        SpringApplication springApplication = new SpringApplication(
            HealthCheckerProcessorTestConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }
}